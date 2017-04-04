package net.aegistudio.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.FileContext;
import net.aegistudio.api.gen.Generator;
import net.aegistudio.api.gen.GeneratorFactory;
import net.aegistudio.api.javagen.JavaGeneratorFactory;
import net.aegistudio.api.xmldom.XmlDocument;

public class GeneratorMain {
	final Map<String, Boolean> flavourMap = new TreeMap<>(); {
		flavourMap.put("client", true);
		flavourMap.put("server", false);
		flavourMap.put("consumer", true);
		flavourMap.put("provider", false);
		flavourMap.put("stub", true);
		flavourMap.put("skeleton", false);
	}
	
	final Map<String, GeneratorFactory> generatorMap = new TreeMap<>(); {
		generatorMap.put("java", new JavaGeneratorFactory());
	}
	
	public void displayHelp(GeneratorFormat format) {
		format.bannerText("Usage: ApiGenerator <dom> <flavour> <language> [--<optionKey>[=<optionValue>]]...");
		format.bannerText("");
		
		// Display help for DOM.
		format.bannerText("Supported DOM(s):");
		format.beginSection();
		format.sectionText(".xml", "Parse specified DOM as a XML DOM.");
		format.endSection();
		format.bannerText("");
		
		// Display help for flavours.
		format.bannerText("Supported flavour(s):");
		format.beginSection();
		
		format.sectionText("client", 
				"Generate API code for user/client of this API.");
		format.sectionText("server", 
				"Generate API code for provider/server of this API.");
		format.sectionText("consumer", 
				"Same as flavour \"client\".");
		format.sectionText("provider", 
				"Same as flavour \"server\".");
		format.sectionText("stub", "Same as flavour \"client\". " +
				"(Resembles the stub in java RPC.)");
		format.sectionText("skeleton", "Same as flavour \"server\". " +
				"(Resembles the skeleton in java RPC.)");
		format.endSection();
		format.bannerText("");
		
		// Display help for API generators.
		generatorMap.forEach((name, factory) -> {
			format.bannerText("Supported option(s) for " + name + ":");
			format.beginSection();
			
			Map<String, String> help = new TreeMap<>();
			factory.help(help);
			help.forEach(format::sectionText);
			
			format.endSection();
			format.bannerText("");
		});
		
		// A default option.
		format.bannerText("Global option(s):");
		format.beginSection();
		format.sectionText("output-directory", 
				"Specify the root directory for where the generated code " +
				"will output to. Defaultly set to \".\"");
		format.sectionText("output-format", 
				"Specify the format for generated code output. Maybe \"file\", " +
				"\"jar\", \"console\". Defaultly set to \"file\".");
		format.sectionText("output-signature", 
				"Set the output directory to ${distribution}-${language}-" + 
				"${flavour}. Once this flag is set. Exclusive with " +
				"\"output-directory\".");	
		format.endSection();
	}
	
	public Document parseXmlDom(File file) throws Exception {
		return XmlDocument.read(new FileInputStream(file));
	}
	
	public Document parseDom(String dom) {
		try {
			if(dom.endsWith(".xml")) 
				return parseXmlDom(new File(dom));
			throw new Exception("Unsupported DOM foramt.");
		}
		catch(Exception e) {
			throw new IllegalArgumentException(
					"Error while parsing DOM file: " + e.getMessage());
		}
	}
	
	public boolean parseFlavour(String flavour) {
		Boolean clientFlavour = flavourMap.get(
				flavour.toLowerCase());
		if(clientFlavour == null)
			throw new IllegalArgumentException(
					"Unrecognized flavour: " + flavour);
		else return clientFlavour;
	}
	
	public GeneratorFactory parseGenerator(String generator) {
		GeneratorFactory factory = generatorMap.get(
				generator.toLowerCase());
		if(factory == null)
			throw new IllegalArgumentException(
					"Unrecognized language: " + generator);
		return factory;
	}
	
	public void generate(Document dom, 
			boolean clientFlavour, String flavourText,
			GeneratorFactory language, String languageText,
			List<String> options) throws IOException {
		Map<String, String> generatorOption = new TreeMap<>();
		generatorOption.put("output-directory", ".");
		generatorOption.put("output-format", "file");
		
		language.initialize(generatorOption);
		options.forEach(entry -> {
			if(!entry.startsWith("--")) return;
			entry = entry.substring("--".length());
			int equalIndex = entry.indexOf("=");
			String key, value;
			if(equalIndex >= 0) {
				value = entry.substring(equalIndex + 1);
				key = entry.substring(0, equalIndex);
			}
			else {
				value = null;
				key = entry;
			}
			generatorOption.put(key.toLowerCase(), value);
		});
		
		Generator generator = language.create(generatorOption, 
				clientFlavour, languageText, flavourText);
		String outputDirectoryPath = generatorOption.get("output-directory");
		if(generatorOption.containsKey("output-signature")) 
			outputDirectoryPath = dom.distribution() 
				+ "-" + languageText + "-" + flavourText;
		File outputDirectory = new File(outputDirectoryPath);
		if(!outputDirectory.exists()) outputDirectory.mkdir();
		
		Context context = new FileContext(outputDirectory);
		generator.generate(context, dom);
	}
	
	public static void main(String[] argumentsArray) {
		GeneratorMain main = new GeneratorMain();
		GeneratorFormat format = new DefaultFormat(
				System.out, 4, 17, 4, 36);
		List<String> arguments = Arrays.asList(argumentsArray);
		
		boolean displayHelp =
			arguments.size() == 0 ||
			arguments.contains("-h") ||
			arguments.contains("--help") ||
			arguments.contains("-?");
		
		if(displayHelp) main.displayHelp(format);
		else try {
			if(arguments.size() < 1) 
				throw new IllegalArgumentException(
						"Missing DOM file for descripting API.");
			String targetDom = arguments.get(0);
			Document dom = main.parseDom(targetDom);
			
			if(arguments.size() < 2)
				throw new IllegalArgumentException(
						"Missing target flavour for API.");
			String flavour = arguments.get(1);
			boolean clientFlavour = main.parseFlavour(flavour);
			
			if(arguments.size() < 3)
				throw new IllegalArgumentException(
						"Missing target language for API.");
			String language = arguments.get(2);
			GeneratorFactory factory = main.parseGenerator(language);
			
			main.generate(dom, clientFlavour, flavour, factory, 
					language, arguments.subList(3, arguments.size()));
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		catch(IOException e) {
			e.printStackTrace(System.out);
		}
	}
}
