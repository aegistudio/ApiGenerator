package net.aegistudio.api;

public interface GeneratorFormat {
	public void bannerText(String text);
	
	public void beginSection();
	
	public void sectionText(String key, String value);
	
	public void endSection();
}
