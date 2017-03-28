package net.aegistudio.api.javagen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.ComposeSerializer;
import net.aegistudio.api.gen.Filter;

public class JavaWriteSerializer extends ComposeSerializer {
	public JavaWriteSerializer() {
		// None string primitives.
		primitiveFilter(Primitive.BYTE, "Byte");
		primitiveFilter(Primitive.SHORT, "Short");
		primitiveFilter(Primitive.INT, "Int");
		primitiveFilter(Primitive.LONG, "Long");
		primitiveFilter(Primitive.FLOAT, "Float");
		primitiveFilter(Primitive.DOUBLE, "Double");
		
		// String primitive.
		Filter string = Filter.PRIMITIVE(Primitive.STRING);
		compositeFilter(string, "ApiString");
		
		// Value or handle objects.

		compositeFilter(Filter.VALUE, "<typeSingle>");
		compositeFilter(Filter.INTERFACE, "<typeSingle>");
		compositeFilter(Filter.CALLBACK, "<typeSingle>");
	}
	
	private void compositeFilter(Filter filter, String object) {
		super.add(Filter.SINGLE(filter), 
				"<object>.write(<id>, <stream>, <host>);"
					.replace("<object>", object));
		super.add(Filter.VARIANT(filter), 
				"ApiVariant.write(<stream>, <host>, <id>, <object>::write);"
					.replace("<object>", object));
	}
	
	private void primitiveFilter(Primitive primitive, String keyword) {
		Filter is = Filter.PRIMITIVE(primitive);
		super.add(Filter.SINGLE(is), 
				"<stream>.write<keyword>(<id>);"
					.replace("<keyword>", keyword));
		super.add(Filter.VARIANT(is), 
				"ApiVariant.write<keyword>(<stream>, <id>);"
					.replace("<keyword>", keyword));		
	}
}
