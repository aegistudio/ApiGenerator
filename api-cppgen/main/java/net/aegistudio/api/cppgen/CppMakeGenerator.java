package net.aegistudio.api.cppgen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.aegistudio.api.Document;
import net.aegistudio.api.Interface;
import net.aegistudio.api.Value;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.Generator;

public class CppMakeGenerator implements Generator {
	protected final CppToolchain tool;
	protected final String flavourText, languageText, apiHome;
	protected final boolean client;
	public CppMakeGenerator(CppToolchain tool, String flavourText, 
			boolean client, String languageText, String apiHome) {
		this.tool = tool;
		this.flavourText = flavourText;
		this.languageText = languageText;
		this.apiHome = apiHome;
		this.client = client;
	}

	@Override
	public void generate(Context context, Document dom) throws IOException {
		BufferedReader templateReader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(tool.whichMake + ".make.template")));
		StringBuilder builder = new StringBuilder();
		for(String line = templateReader.readLine(); 
				line != null; line = templateReader.readLine()) {
			builder.append(line);
			builder.append("\n");
		}
		
		List<String> sources = new ArrayList<>();
		sources.add(client? dom.clientHost() : dom.serverHost());
		
		Arrays.stream(dom.interfaces())
			.map(Interface::name).forEach(sources::add);
		Arrays.stream(dom.values())
			.map(Value::name).forEach(sources::add);
		Arrays.stream(dom.callbacks())
			.map(Interface::name).forEach(sources::add);
		
		StringBuilder sourceBuilder = new StringBuilder();
		sources.forEach(name -> sourceBuilder.append(name + ".obj "));
		
		String template = new String(builder);
		context.file("Makefile").write(template
				.replace("${api.groupId}", dom.namespace().concatenate("."))
				.replace("${api.distribution}", dom.distribution())
				.replace("${api.version}", dom.version())
				.replace("${api.language}", languageText)
				.replace("${api.flavour}", flavourText)
				.replace("${api.sources}", new String(sourceBuilder))
				.replace("${api.home}", apiHome)
				
				.replace("${tool.compiler}", tool.compiler)
				.replace("${tool.linker}", tool.linker)
				.replace("${tool.libtool}", tool.libtool)
				.getBytes());
	}
}
