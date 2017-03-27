package net.aegistudio.api.gen;

import java.util.function.Function;

import net.aegistudio.api.Namespace;
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
		protected final String rawName;
		protected final Function<String, String> predict;
		public final Primitive primitive;
		public final boolean variant;
		
		private Result(Primitive primitive, String rawName, boolean variant) {
			this.primitive = primitive;
			this.variant = variant;
			this.predict = variant? 
					variance : invariance;
			this.rawName = rawName;
		}
		
		public Result(String rawName, boolean variant) {
			this(null, rawName, variant);
		}
		
		public Result(Primitive primitive, boolean variant) {
			this(primitive, primitiveTypes[primitive.ordinal()], variant);
		}
		
		public String component(Namespace namespace) {
			String typeName;
			if(primitive != null) typeName = rawName;
			else typeName = namespace.concatenate(
					rawName, commonSeparator);
			return typeName;
		}
		
		public String name(Namespace namespace) {
			return predict.apply(component(namespace));
		}
	}
	
	public class VoidResult extends Result {

		public VoidResult() {
			super(voidType, false);
		}
		
		public String component(Namespace namespace) {
			return rawName;
		}
	}
	
	public Result convertType(Type type) {
		if(type == null) return new VoidResult();
		boolean variant = type.variant();
		Primitive assume = Primitive.parse(type.name()); 
		if(assume != null) return new Result(assume, variant);
		return new Result(type.name(), variant);
	}
}
