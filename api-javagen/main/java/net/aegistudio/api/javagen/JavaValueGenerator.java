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
import net.aegistudio.api.gen.TypeTable;

public class JavaValueGenerator extends JavaPerspectGenerator<Value> {
	
	protected void construct(Value valueType, SymbolTable symbolTable, 
			Namespace namespace, IndentPrintStream valuePrint) throws IOException {
		Type[] resultTypes = Arrays.stream(valueType.fields())
				.map(Field::type).toArray(Type[]::new);
				
		// Java file header.
		super.javaHeader(valuePrint, namespace);
		
		// Java class body begins.
		valuePrint.println("public class <type> {"
				.replace("<type>", valueType.name()));
		valuePrint.push();
		
		// Construct fields.
		for(Field field : valueType.fields()) {
			TypeTable.Result typeResult = typeTable
					.convertType(field.type());
			SymbolTable.Class symbolClass = symbolTable
					.lookup(field.type());
			String type = typeResult.name(symbolClass, namespace);
			String name = field.name();
			valuePrint.println("public <type> <id>;"
					.replace("<type>", type)
					.replace("<id>", name));
		}
		valuePrint.println(); 
		
		// Construct read method.
		valuePrint.println(("public static <type> read(DataInputStream dataInputStream, " 
				+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", valueType.name()));
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
		valuePrint.println(("public static void write(<type> value, DataOutputStream dataOutputStream, "
				+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", valueType.name()));
		valuePrint.push();
		String[] directFields = Arrays.stream(valueType.fields())
				.map(Field::name).map(name -> "value." + name).toArray(String[]::new);
		this.ioMethod(symbolTable, namespace, writeSerial, "dataOutputStream", 
				"apiHost", valuePrint, resultTypes, directFields);
		valuePrint.pop();
		valuePrint.println("}");
		
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
