package net.aegistudio.api.gen;

import java.util.function.BiPredicate;

import net.aegistudio.api.Primitive;

public interface Filter extends BiPredicate<TypeTable.Result, SymbolTable.Class> {
	public static Filter PRIMITIVE(Primitive primitive) {
		return (result, clazz) -> result.primitive != null && 
			result.primitive.ordinal() == primitive.ordinal(); 
	}
	
	public static Filter SINGLE(Filter inner) {
		return (result, clazz) -> !result.variant && inner.test(result, clazz);
	}
	
	public static Filter VARIANT(Filter inner) {
		return (result, clazz) -> result.variant && inner.test(result, clazz);
	}

	public static final Filter VALUE = (result, type) -> result.primitive == null 
			&& type.ordinal() == SymbolTable.Class.VALUE.ordinal();
	
	public static final Filter INTERFACE = (result, type) -> result.primitive == null
			&& type.ordinal() == SymbolTable.Class.INTERFACE.ordinal();
	
	public static final Filter CALLBACK = (result, type) -> result.primitive == null
			&& type.ordinal() == SymbolTable.Class.CALLBACK.ordinal();
}
