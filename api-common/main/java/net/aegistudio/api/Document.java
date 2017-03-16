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
	
	public String clientHost();
	
	public String serverHost();
	
	public Interface[] interfaces();
	
	public Interface[] callbacks();
	
	public Value[] values();
	
	public Method[] functions();
}
