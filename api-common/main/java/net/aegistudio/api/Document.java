package net.aegistudio.api;

/**
 * The root element of an API definition.
 * 
 * In the DOM. The API document is always consists of 4 basic
 * child nodes: value, interface, function and callback.
 * 
 * @author aegistudio
 */

public interface Document {
	public String[] namespace();
	
	public static String concatenate(
			String[] namespace, String separator) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < namespace.length; i ++) {
			if(i > 0) builder.append(separator);
			builder.append(namespace);
		}
		return new String(builder);
	}
	
	public static String concatenate(
			String[] namespace, String type, String separator) {
		String former = concatenate(namespace, separator);
		return former.length() == 0? type : former + separator + type;
	}
	
	public String clientHost();
	
	public String serverHost();
	
	public Interface[] interfaces();
	
	public Interface[] callbacks();
	
	public Value[] values();
	
	public Method[] functions();
}
