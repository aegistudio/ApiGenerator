package net.aegistudio.api.javagen;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.TypeTable;

public class JavaTypeTable extends TypeTable {
	public JavaTypeTable() {
		super(".", "void", type -> type + "[]");
		super.primitive(Primitive.BYTE, "byte");
		super.primitive(Primitive.SHORT, "short");
		super.primitive(Primitive.INT, "int");
		super.primitive(Primitive.LONG, "long");
		super.primitive(Primitive.FLOAT, "float");
		super.primitive(Primitive.DOUBLE, "double");
		super.primitive(Primitive.STRING, "String");
	}
}
