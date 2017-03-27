package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.Arrays;

import net.aegistudio.api.Document;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;
import net.aegistudio.api.Value;
import net.aegistudio.api.Value.Field;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.SymbolTable;

public class JavaValueGenerator extends JavaPerspectGenerator<Value> {
	
	protected void construct(Value valueType, SymbolTable symbolTable, 
			Namespace namespace, IndentPrintStream valuePrint) throws IOException {
		// Java file header.
		valuePrint.println("package <namespace>;"
				.replace("<namespace>", namespace.concatenate(".")));
		valuePrint.println();
		
		valuePrint.println("import java.io.*;");
		valuePrint.println("import net.aegistudio.api.java.*;");
		valuePrint.println("import net.aegistudio.api.java.extprim.*;");
		valuePrint.println();
		
		// Java class body begins.
		valuePrint.println("public class <type> {"
				.replace("<type>", valueType.name()));
		valuePrint.push();
		valuePrint.println();
		
		// Construct fields.
		for(Field field : valueType.fields()) {
			String type = typeTable.convertType(
					field.type()).name(namespace);
			String name = field.name();
			valuePrint.println("public <type> <id>;"
					.replace("<type>", type)
					.replace("<name>", name));
		}
		valuePrint.println();
		
		// Prepare I/O methods.
		Type[] resultTypes = Arrays.stream(valueType.fields())
				.map(Field::type).toArray(Type[]::new);
		
		// Construct read method.
		valuePrint.println("public static <type> read(DataInputStream dataInputStream, "
				.replace("<type>", valueType.name()));
		valuePrint.println("\tApiHost apiHost) throws IOException, ApiException {");
		valuePrint.push();
		valuePrint.println("<type> result = new <type>();"
				.replace("<type>", valueType.name()));
		String[] resultFields = Arrays.stream(valueType.fields()).map(Field::name)
			.map(name -> "result." + name).toArray(String[]::new);
		this.ioMethod(symbolTable, namespace, readSerial, "dataInputStream", 
				"apiHost", valuePrint, resultTypes, resultFields);
		valuePrint.println("return result;");
		valuePrint.pop();
		valuePrint.println("}");
		valuePrint.println();
		
		// Construct write method.
		valuePrint.println("public void write(DataOutputStream dataOutputStream, ");
		valuePrint.println("\tApiHost apiHost) throws IOException, ApiException {");
		valuePrint.push();
		String[] directFields = Arrays.stream(valueType.fields())
				.map(Field::name).toArray(String[]::new);
		this.ioMethod(symbolTable, namespace, writeSerial, "dataOutputStream", 
				"apiHost", valuePrint, resultTypes, directFields);
		valuePrint.pop();
		valuePrint.println("}");
		valuePrint.println();
		
		// Java class body ends.
		valuePrint.pop();
		valuePrint.println("}");
	}

	@Override
	protected Value[] perspect(Document document) {
		return document.values();
	}

	@Override
	protected String name(Value perspect) {
		return perspect.name();
	}
}
