package net.aegistudio.api.gen;

import java.util.Map;
import java.util.TreeMap;

import net.aegistudio.api.Document;
import net.aegistudio.api.Interface;
import net.aegistudio.api.Primitive;
import net.aegistudio.api.Type;
import net.aegistudio.api.Value;

/**
 * Symbol table holds all type names defined
 * in this API. You can look up symbol table 
 * when generating code.
 * 
 * @author aegistudio
 */

public class SymbolTable {
	public enum Class {
		UNDEFINED, VOID, VALUE, INTERFACE, CALLBACK;
	}
	
	protected final Map<String, Class> table = new TreeMap<>();
	
	public SymbolTable(Document document) {
		for(Value value : document.values()) 
			table.put(value.name(), Class.VALUE);
		for(Interface interfac : document.interfaces())
			table.put(interfac.name(), Class.INTERFACE);
		for(Interface callback : document.callbacks())
			table.put(callback.name(), Class.CALLBACK);
	}
	
	public Class lookup(Type type) {
		if(type == null) return Class.VOID;
		if(Primitive.parse(type.name()) != null) 
			return Class.VALUE;
		return table.getOrDefault(
				type.name(), Class.UNDEFINED);
	}
}
