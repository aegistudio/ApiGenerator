package net.aegistudio.api;

/**
 * A value is composition of even smaller
 * value objects, primitive types or 
 * handles (either client or server side).
 * 
 * @author aegistudio
 */

public interface Value {
	public String name();
	
	public interface Field {
		public String name();
		public Type type();
	}
	public Value.Field[] fields();
}
