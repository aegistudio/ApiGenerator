package net.aegistudio.api;

/**
 * Defines a method. For functions
 * elements, it will be configured 
 * as a method in ApiHost.
 * 
 * @author aegistudio
 */

public interface Method {
	public String name();
	
	public Type result();
	
	public interface Parameter {
		public String name();
		
		public Type type();
	}
	
	public Method.Parameter[] parameters();
}
