package net.aegistudio.api.cppgen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.TypeTable;

public class CppTypeTable extends TypeTable {
	public CppTypeTable() {
		super("::", "void", type -> "api::variant<" + type + ">");
		
		// #include <stdint.h>	// standard integer type.
		super.primitive(Primitive.BYTE, "int8_t");
		super.primitive(Primitive.SHORT, "int16_t");
		super.primitive(Primitive.INT, "int32_t");
		super.primitive(Primitive.LONG, "int64_t");
		
		super.primitive(Primitive.FLOAT, "float");
		super.primitive(Primitive.DOUBLE, "double");
		
		// #include <string> // std::string of STL.
		super.primitive(Primitive.STRING, "std::string");
	}
}
