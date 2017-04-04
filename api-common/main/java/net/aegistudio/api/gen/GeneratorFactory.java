package net.aegistudio.api.gen;

import java.util.Map;

public interface GeneratorFactory {
	/**
	 * Initialize available configuration for a generation process.
	 */
	public void initialize(Map<String, String> configuration);
	
	/**
	 * Display available arguments and their detail text.
	 */
	public void help(Map<String, String> help);
	
	/**
	 * Create an API Generator for a provided configuration,
	 */
	public Generator create(Map<String, String> configuration, boolean client,
			String languageText, String flavourText);
}
