package net.aegistudio.api.gen;

import java.util.function.Function;

import net.aegistudio.api.Document;
import net.aegistudio.api.Primitive;
import net.aegistudio.api.Type;

public class TypeTable {
	protected final String[] primitiveTypes;
	protected String commonSeparator;
	protected String voidType;
	protected Function<String, String> variance;
	protected Function<String, String> invariance = s -> s;
	
	public TypeTable(String commonSeparator, String voidType, 
			Function<String, String> variance) {
		
		int allPrimitives = Primitive.values().length;
		this.primitiveTypes = new String[allPrimitives];
		this.commonSeparator = commonSeparator;
		this.voidType = voidType;
		this.variance = variance;
	}
	
	public void primitive(
			Primitive primitive, String primitiveType) {
		int ordinal = primitive.ordinal();
		this.primitiveTypes[ordinal] = primitiveType;
	}
	
	public class Result {
		public final String name;
		public final Primitive primitive;
		public final boolean variant;
		
		public Result(String rawName, boolean variant) {
			this.primitive = null;
			this.variant = variant;
			Function<String, String> predict = 
					variant? variance : invariance;
			this.name = predict.apply(rawName);
		}
		
		public Result(Primitive primitive, boolean variant) {
			this(primitiveTypes[primitive.ordinal()], variant);
		}
	}
	
	public Result convertType(Document document, Type type) {
		if(type == null) return new Result(voidType, false);
		boolean variant = type.variant();
		Primitive assume = Primitive.parse(type.name()); 
		if(assume != null) return new Result(assume, variant);
		return new Result(Document.concatenate(document.namespace(), 
				type.name(), commonSeparator), variant);
	}
}
