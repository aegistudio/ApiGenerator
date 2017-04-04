package net.aegistudio.api.javagen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.Generator;

public class JavaPomGenerator implements Generator {
	protected final String flavourText, languageText, source;
	public JavaPomGenerator(String flavourText, 
			String languageText, String source) {
		this.flavourText = flavourText;
		this.languageText = languageText;
		this.source = source;
	}
	
	@Override
	public void generate(Context context, Document dom) throws IOException {
		BufferedReader templateReader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream("pom.xml.template")));
		StringBuilder builder = new StringBuilder();
		for(String line = templateReader.readLine(); 
				line != null; line = templateReader.readLine()) {
			builder.append(line);
			builder.append("\n");
		}
		
		String template = new String(builder);
		context.file("pom.xml").write(template
				.replace("${api.groupId}", dom.namespace().concatenate("."))
				.replace("${api.distribution}", dom.distribution())
				.replace("${api.version}", dom.version())
				.replace("${api.language}", languageText)
				.replace("${api.flavour}", flavourText)
				.replace("${api.source}", source)
				.getBytes());
	}
}
