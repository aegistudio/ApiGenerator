package net.aegistudio.api.cppgen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.ComposeSerializer;
import net.aegistudio.api.gen.Filter;

public class CppWriteSerializer extends ComposeSerializer {
	public CppWriteSerializer() {
		// Invoke stream api directly.
		primitiveFilter(Primitive.BYTE, "Byte");
		primitiveFilter(Primitive.SHORT, "Short");
		primitiveFilter(Primitive.INT, "Int");	
		primitiveFilter(Primitive.LONG, "Long");
		primitiveFilter(Primitive.FLOAT, "Float");
		primitiveFilter(Primitive.DOUBLE, "Double");
		
		// api::String::write(someString, inputStream)
		compositeFilter(Filter.PRIMITIVE(Primitive.STRING), 
				"api::String::write(<id>, <stream>)");
		
		// some::namespace::SomeObject.write(object, apiHost, inputStream)
		String composeWriter = "<typeSingle>.write(<id>, <host>, <stream>)";
		compositeFilter(Filter.INTERFACE, composeWriter);
		compositeFilter(Filter.CALLBACK, composeWriter);
	}
	
	private void primitiveFilter(Primitive primitive, String which) {
		Filter filter = Filter.PRIMITIVE(primitive);
		this.compositeFilter(filter, "<stream>.write<which>(<id>)"
				.replace("<which>", which));
	}
	
	private void compositeFilter(Filter filter, String serializer) {
		super.add(Filter.SINGLE(filter), serializer);
		
		String componentSerial = serializer.replace(
				"<id>", "<id>[i_<id>]");
		
		super.add(Filter.VARIANT(filter), (
				"<stream>.writeInt(<id>.length);\n" +
				"int32_t i_<id>; for(i_<id> = 0; i < <id>.length; i ++)\n" + 
				"\t" + componentSerial + ";"));
	}
}
