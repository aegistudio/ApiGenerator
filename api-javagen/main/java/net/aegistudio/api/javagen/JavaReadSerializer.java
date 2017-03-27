package net.aegistudio.api.javagen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.ComposeSerializer;
import net.aegistudio.api.gen.Filter;

public class JavaReadSerializer extends ComposeSerializer { 
	public JavaReadSerializer() {
		// None string primitives.
		primitiveFilter(Primitive.BYTE, "Byte");
		primitiveFilter(Primitive.SHORT, "Short");
		primitiveFilter(Primitive.INT, "Int");
		primitiveFilter(Primitive.LONG, "Long");
		primitiveFilter(Primitive.FLOAT, "Float");
		primitiveFilter(Primitive.DOUBLE, "Double");
		
		// String serialization.
		compositeFilter(Filter.PRIMITIVE(Primitive.STRING), "ApiString");
		
		// Value or handle objects.
		compositeFilter(Filter.VALUE, "<typeSingle>");
		compositeFilter(Filter.INTERFACE, "<typeSingle>");
		compositeFilter(Filter.CALLBACK, "<typeSingle>");
	}
	
	private void compositeFilter(Filter filter, String object) {
		super.add(Filter.SINGLE(filter), 
				"<id> = <object>.read(<stream>, <host>);"
					.replace("<object>", object));
		super.add(Filter.VARIANT(filter), 
				"<id> = ApiVariant.read(<stream>, <host>, <typeSingle>[]::new, <object>::read);"
					.replace("<object>", object));
	}
	
	private void primitiveFilter(Primitive primitive, String keyword) {
		Filter is = Filter.PRIMITIVE(primitive);
		super.add(Filter.SINGLE(is), 
				"<id> = <stream>.read<keyword>();"
					.replace("<keyword>", keyword));
		super.add(Filter.VARIANT(is), 
				"<id> = ApiVariant.read<keyword>(<stream>);"
					.replace("<keyword>", keyword));		
	}
}