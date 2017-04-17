package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.Map;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.CommonFactory;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.Generator;

public class JavaGeneratorFactory extends CommonFactory {
	@Override
	public void initialize(Map<String, String> configuration) {
		configuration.put("source", "main/java");
	}

	@Override
	public void help(Map<String, String> help) {
		help.put("source", "The export path for the generated source files. " 
							+ "The default value would be 'main/java'.");
		help.put("generate-pom", "Whether would generate pom.xml for the project."
							+ "The default value would be set to false / unspecified.");
	}
	
	@Override
	public Generator create(Map<String, String> configuration, boolean client, 
			String languageText, String flavourText) {
		String source = configuration.get("source");
		boolean generatePom = configuration.containsKey("generate-pom");
		final Generator javaValue = new JavaValueGenerator();
		final Generator interfaceGenerator = client? 
				new JavaRemoteGenerator(this.allInterfaces(client)):
				new JavaLocalGenerator(this.allInterfaces(client));
		
		final Generator callbackGenerator = client?
				new JavaLocalGenerator(this::allCallbacks):
				new JavaRemoteGenerator(this::allCallbacks);
		
		final Generator pomGenerator = 
				new JavaPomGenerator(flavourText, languageText, source);
				
		return new Generator() {
			@Override
			public void generate(Context context, Document dom) throws IOException {
				Context sourceDirectory = context.step(source);
	
				javaValue.generate(sourceDirectory, dom);
				interfaceGenerator.generate(sourceDirectory, dom);
				callbackGenerator.generate(sourceDirectory, dom);
				if(generatePom) pomGenerator.generate(context, dom);
			}
		};
	}
}
