package net.aegistudio.api.javagen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.aegistudio.api.Document;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;
import net.aegistudio.api.Value;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.Generator;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

public class JavaStubGenerator implements Generator {
	public final JavaTypeTable typeTable = new JavaTypeTable();
	public final JavaReadSerializer readSerial = new JavaReadSerializer();
	public final JavaWriteSerializer writeSerial = new JavaWriteSerializer();
	
	@Override
	public void generate(Context context, Document dom) throws IOException {
		Context sourceFolder = context.step("main/java");
		Context namespaceFolder = sourceFolder.step(
				dom.namespace().concatenate("/"));
		SymbolTable symbolTable = new SymbolTable(dom);
		
		for(Value valueType : dom.values()) {
			OutputStream valueOutput = namespaceFolder.file(valueType.name());
			IndentPrintStream valuePrint = new IndentPrintStream(valueOutput);
			
			constructValue(symbolTable, dom.namespace(), valueType, valuePrint);
			
			valuePrint.close();
		}
	}

	protected void readMethod(SymbolTable symbolTable, Namespace namespace,
			String inputStream, String apiHosting,
			PrintStream print, Type[] types, String[] names) throws IOException {
		assert types.length == names.length;
		for(int i = 0; i < types.length; i ++) {
			TypeTable.Result typeResult = typeTable.convertType(types[i]);
			SymbolTable.Class symbolClass = symbolTable.lookup(types[i]);
			
			if(symbolClass == SymbolTable.Class.UNDEFINED)
				throw new IllegalArgumentException(
						"Undefined symbol " + typeResult.name(namespace) + "!");
			
			readSerial.serialize(print, namespace, 
					inputStream, apiHosting, names[i], 
					typeResult, symbolClass);
		}
	}
	
	protected void constructValue(SymbolTable symbolTable, Namespace namespace, 
			Value valueType, IndentPrintStream valuePrint) throws IOException {
		// Java file header.
		valuePrint.println("package <namespace>;"
				.replace("<namespace>", namespace.concatenate(".")));
		valuePrint.println();
		
		valuePrint.println("import java.io.*;");
		valuePrint.println("import net.aegistudio.api.java.extprim.*;");
		
		// Java class body begins.
		valuePrint.println("public class <type> {"
				.replace("<type>", valueType.name()));
		valuePrint.push();
		
		// Construct fields.
		
		// Java class body ends.
		valuePrint.pop();
		valuePrint.println("}");
	}
}
