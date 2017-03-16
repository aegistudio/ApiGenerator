package net.aegistudio.api;

/**
 * A type definition. A type could either be primitive types
 * as is defined in different languages, or objects 
 * that are composition of different primitive types or
 * even smaller objects.
 * 
 * The variant field defines whether the type definition 
 * refers to a list. The length of list will be transferred
 * ahead of its values. 
 * 
 * @author aegistudio
 */

public interface Type {
	public String name();
	
	public boolean variant();
}