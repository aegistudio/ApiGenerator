package net.aegistudio.api.cppgen;

import java.io.IOException;
import java.util.Map;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.CommonFactory;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.FlavourDocument;
import net.aegistudio.api.gen.Generator;

public class CppGeneratorFactory extends CommonFactory {
	@Override
	public void initialize(Map<String, String> configuration) {
		configuration.put("api-home", "");
		configuration.put("generate-make", null);
	}

	@Override
	public void help(Map<String, String> help) {
		help.put("api-home", "The path to api-atom-cpp assembly. We reference it for"
							+ "its header files when generate Makefile, just set to " 
							+ "the assembly root folder.");
		help.put("generate-make", "Whether would generate Makefile and what style of "
							+ "toolchain for the project. Candidate styles are: " + CppToolchain.supported()
							+ ". The default value would be set to null / unspecified.");
	}
	
	@Override
	public Generator create(Map<String, String> configuration, boolean client, 
			String languageText, String flavourText) {
		final Generator cppValue = new CppValueGenerator(client);
		final Generator interfaceGenerator = client? 
				new CppRemoteGenerator(client, this.allInterfaces(client)):
				new CppLocalGenerator(client, this.allInterfaces(client));
		
		final Generator callbackGenerator = client?
				new CppLocalGenerator(client, this::allCallbacks):
				new CppRemoteGenerator(client, this::allCallbacks);
		
		Generator attemptMakeGenerator = null;
		if(configuration.get("generate-make") != null) {
			CppToolchain tool = CppToolchain.parse(configuration.get("generate-make"));
			attemptMakeGenerator = new CppMakeGenerator(tool, flavourText, client, 
					languageText, configuration.get("api-home"));
		}
		final Generator makeGenerator = attemptMakeGenerator;
				
		return new Generator() {
			@Override
			public void generate(Context context, Document dom) throws IOException {
				FlavourDocument fdom = new FlavourDocument(flavourText, dom);
				
				cppValue.generate(context, fdom);
				interfaceGenerator.generate(context, fdom);
				callbackGenerator.generate(context, fdom);
				if(makeGenerator != null) 
					makeGenerator.generate(context, dom);
			}
		};
	}
}
