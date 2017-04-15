package net.aegistudio.api.cppgen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.ComposeSerializer;
import net.aegistudio.api.gen.Filter;

public class CppReadSerializer extends ComposeSerializer {
	public CppReadSerializer() {
		// Invoke stream api directly.
		primitiveFilter(Primitive.BYTE, "Byte");
		primitiveFilter(Primitive.SHORT, "Short");
		primitiveFilter(Primitive.INT, "Int");	
		primitiveFilter(Primitive.LONG, "Long");
		primitiveFilter(Primitive.FLOAT, "Float");
		primitiveFilter(Primitive.DOUBLE, "Double");
		
		// api::String::read(inputStream)
		compositeFilter(Filter.PRIMITIVE(Primitive.STRING), 
				"api::String::read(<stream>)");
		
		// some::namespace::SomeObject.read(apiHost, inputStream)
		String composeReader = "<typeSingle>.read(<host>, <stream>)"; 
		compositeFilter(Filter.INTERFACE, composeReader);
		compositeFilter(Filter.CALLBACK, composeReader);
	}
	
	private void primitiveFilter(Primitive primitive, String which) {
		Filter filter = Filter.PRIMITIVE(primitive);
		this.compositeFilter(filter, "<stream>.read<which>()"
				.replace("<which>", which));
	}
	
	private void compositeFilter(Filter filter, String serializer) {
		super.add(Filter.SINGLE(filter), 
				"<id> = <serializer>;"
				.replace("<serializer>", serializer));
		super.add(Filter.VARIANT(filter), (
				"<id> = api::variant<<typeSingle>>(<stream>.readInt());\n" +
				"int32_t i_<id>; for(i_<id> = 0; i < <id>.length; i ++)\n" + 
				"\t<id>[i_<id>] = <serializer>;")
				.replace("<serializer>", serializer));
	}
}
