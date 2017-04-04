package net.aegistudio.api;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultFormat implements GeneratorFormat {
	protected final int predPadding, keyWidth, midPadding, valueWidth, length;
	protected final String predPaddingText;
	protected PrintStream out;
	
	public DefaultFormat(PrintStream out, 
			int predPadding, int keyWidth, int midPadding, int valueWidth) {
		this.out = out;
		this.predPadding = predPadding;
		this.keyWidth = keyWidth;
		this.midPadding = midPadding;
		this.valueWidth = valueWidth;
		this.length = predPadding + keyWidth + midPadding + valueWidth;
		
		StringBuilder predBuilder = new StringBuilder();
		for(int i = 0; i < predPadding; i ++) 
			predBuilder.append(' ');
		this.predPaddingText = new String(predBuilder);
	}
	
	public void bannerText(String text) {
		TextTrimmer trimmer = new TextTrimmer(text);
		do {
			out.println(trimmer.trim(length));
		} 
		while(trimmer.remain());
	}
	
	private final List<TextTrimmer> keyBuffer = new ArrayList<>();
	private final List<TextTrimmer> valueBuffer = new ArrayList<>();
	
	public void beginSection() {
		keyBuffer.clear();
		valueBuffer.clear();
	}
	
	public void sectionText(String key, String value) {
		keyBuffer.add(new TextTrimmer(key));
		valueBuffer.add(new TextTrimmer(value));
	}
	
	public void endSection() {
		for(int i = 0; i < keyBuffer.size(); i ++) {
			TextTrimmer key = keyBuffer.get(i);
			TextTrimmer value = valueBuffer.get(i);
			oneSection(key, value);
		}
	}
	
	protected void oneSection(TextTrimmer key, TextTrimmer value) {
		do {
			out.print(predPaddingText);
			String keyCurrent = key.trim(keyWidth);
			out.print(keyCurrent);
			
			int midWidth = keyWidth - keyCurrent.length() + midPadding;
			for(int i = 0; i < midWidth; i++) out.print(' ');
			
			out.print(value.trim(valueWidth));
			out.println();
		}
		while(key.remain() || value.remain());
	}
}

class TextTrimmer {
	private String text;
	public TextTrimmer(String text) {
		this.text = text;
	}
	
	public String trim(int length) {
		if(!remain()) return "";
		int currentLength = length;
		if(text.length() < length)
			currentLength = text.length();
		
		String feed = text.substring(0, currentLength);
		this.text = text.substring(currentLength);
		return feed;
	}
	
	public boolean remain() {
		return text.length() > 0;
	}
}