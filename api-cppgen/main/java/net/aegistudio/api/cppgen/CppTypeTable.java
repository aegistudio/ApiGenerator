package net.aegistudio.api.cppgen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

/**
 * <p>Symbol decorate policy:</p>
 * 
 * <ul>
 * <li>Client side: callbacks are pointers and interfaces are objects.
 * <li>Server side: callbacks are objects and interfaces are pointers.
 * </ul>
 * 
 * <p>In c++ language, pointers are decorated with * while objects stay
 * undecorated.</p>
 * 
 * @see net.aegistudio.api.gen.TypeTable.Result
 * @author aegistudio
 */

public class CppTypeTable extends TypeTable {
	public CppTypeTable(boolean clientSide) {
		super("::", "void", (symbol, name) -> {
			boolean matches = symbol.equals(clientSide? 
				SymbolTable.Class.CALLBACK: 
				SymbolTable.Class.INTERFACE);
			
			if(matches) return name + "*";
			else return name;
		}, type -> "api::variant<" + type + ">");
		
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
