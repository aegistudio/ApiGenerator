package net.aegistudio.api.cppgen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.ComposeSerializer;
import net.aegistudio.api.gen.Filter;

public class CppReadSerializer extends ComposeSerializer {
	// Client side then interfaces would be object while 
	// callbacks would be pointer. Vice versa.
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
		
		// some::namespace::SomeObject::read(apiHost, inputStream)
		exceptFilter(Filter.VALUE);
		exceptFilter(Filter.INTERFACE);
		exceptFilter(Filter.CALLBACK);
	}
	
	private void primitiveFilter(Primitive primitive, String which) {
		Filter filter = Filter.PRIMITIVE(primitive);
		this.compositeFilter(filter, 
				"<stream>.read<which>()"
				.replace("<which>", which));
	}
	
	private void compositeFilter(Filter filter, String serializer) {
		super.add(Filter.SINGLE(filter),
				"<id> = " + serializer + ";");
		super.add(Filter.VARIANT(filter), 
				"<id> = api::variant<<typeSingle>>(<stream>.readInt()); {\n" +
				"\tint32_t i; for(i = 0; i < <id>.length(); i ++)\n" + 
				"\t<id>[i] = " + serializer + ";\n" +
				"}");
	}
	
	private void exceptFilter(Filter filter) {
		super.add(Filter.SINGLE(filter), 
				"tryAssign(<typeSingle>, <id>, <id>, \n" + 
				"\t<class>::read(<host>, <stream>));");
		super.add(Filter.SINGLE(filter),
				  "<id> = <type>(<stream>.readInt()); {\n"
				+ "\tint32_t i; for(i = 0; i < <id>.length(); i ++)\n"
				+ "\ttryAssign(<typeSingle>, <id>[i], except,\n"
				+ "\t\t<class>::read(<host>, <stream>));\n"
				+ "}");
	}
}
