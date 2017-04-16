package net.aegistudio.api.cppgen;

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

public class CppValueGenerator extends CppPerspectGenerator<Value> {
	public CppValueGenerator(boolean clientSide) {
		super(clientSide);
	}

	@Override
	protected Value[] perspect(Document document) {
		return document.values();
	}

	@Override
	protected String name(Value perspect) {
		return perspect.name();
	}
	
	@Override
	protected void construct(Value perspect, SymbolTable symbolTable, Namespace namespace,
			IndentPrintStream includePrinter, IndentPrintStream sourcePrinter) throws IOException {
		// Profile symbol table.
		Field[] fieldList = perspect.fields();
		Type[] typeList = Arrays.stream(fieldList)
				.map(Field::type).toArray(Type[]::new);
		TypeTable.Result[] resultList = Arrays.stream(typeList)
				.map(typeTable::convertType)
				.toArray(TypeTable.Result[]::new);
		SymbolTable.Class[] symbolList = Arrays.stream(typeList)
				.map(symbolTable::lookup)
				.toArray(SymbolTable.Class[]::new);
		
		// Profile read/write method.
		String readMethod = "_EX(" + perspect.name() 
			+ ") <midfix>read(api::ApiHost& _host, \n" 
			+ "\t\tapi::InputStream& _inputStream)";
		String writeMethod = "void <midfix>write(" + perspect.name() 
			+ "& _object, api::ApiHost& _host, \n"
			+ "\t\tapi::OutputStream& _outputStream)";
		
		includePrinter.println("// Dependency headers.");
		Arrays.stream(super.filterType(typeList, symbolTable))
			.forEach(type -> includePrinter.println(
					"#include \"" + type.name() + ".h\""));
		includePrinter.println();
		
		includePrinter.println("// Open class namespace.");
		String[] theNamespacing = namespace.namespace();
		for(int i = 0; i < theNamespacing.length; i ++) {
			String namespacing = theNamespacing[i];
			includePrinter.println("namespace " + namespacing + " { ");
		}
		includePrinter.println();
		
		includePrinter.println("// Class definition.");
		includePrinter.println("class " + perspect.name() + "{");
		
		// Field printing.
		includePrinter.print("public:");
		includePrinter.push();
		includePrinter.println();
		for(int i = 0; i < fieldList.length; i ++) {
			StringBuilder fieldBuilder = new StringBuilder();
			fieldBuilder.append(resultList[i]
					.name(symbolList[i], namespace));
			fieldBuilder.append(' ');
			fieldBuilder.append(fieldList[i].name());
			fieldBuilder.append(";");
			includePrinter.println(new String(fieldBuilder));
		}
		includePrinter.println();
		
		// Method list printing.
		includePrinter.println(perspect.name() + "();");	// constructor.
		includePrinter.println();
		
		includePrinter.println("static " 
				+ readMethod.replace("<midfix>", "") 
				+ ";");
		includePrinter.println();							// read method.
		
		includePrinter.println("static " + 
				writeMethod.replace("<midfix>", "") 
				+ ";");
		includePrinter.println();							// write method.
		
		includePrinter.pop();
		includePrinter.println("};");
		includePrinter.println();
		
		includePrinter.println("// Close class namespace");
		// Class un-namespacing.
		for(int i = theNamespacing.length - 1; i >= 0; i --) {
			String namespacing = theNamespacing[i];
			includePrinter.println("}; /* namespace " + namespacing + " */ ");
		}
		includePrinter.println();
		
		// Include the value include file.
		sourcePrinter.println("#include \"" 
				+ namespace.concatenate(perspect.name(), "/")
				+ ".h\"");
		sourcePrinter.println("using namespace " 
				+ namespace.concatenate("::") + ";");
		sourcePrinter.println();
		
		// Now the constructor.
		sourcePrinter.println("// Implement value constructor.");
		sourcePrinter.println(perspect.name() + "::" 
				+ perspect.name() + "():");
		
		StringBuilder initializeTable = new StringBuilder();
		for(int i = 0; i < fieldList.length; i ++) {
			if(i > 0) initializeTable.append(", ");
			initializeTable.append(fieldList[i].name());
			initializeTable.append("(");
		
			// If it is list, set the initial length to zero.
			if(typeList[i].variant())
				initializeTable.append("0");
			
			// If it is primitive, set the initial length to zero.
			else if(resultList[i].primitive != null)
				initializeTable.append("0");
			
			// If it is client side, set the callback pointer to NULL.
			else if(clientSide && symbolList[i].equals(SymbolTable.Class.CALLBACK))
				initializeTable.append("NULL");
			
			// If it is server side, set the interface pointer to NULL.
			else if((!clientSide) && symbolList[i].equals(SymbolTable.Class.INTERFACE))
				initializeTable.append("NULL");
			
			initializeTable.append(")");
		}
		sourcePrinter.println("\t" + new String(initializeTable) + " {}");
		sourcePrinter.println();
		
		// Now the read method.
		sourcePrinter.println("// Implement read method.");
		sourcePrinter.println(readMethod.replace("<midfix>", 
				perspect.name() + "::") + " {");
		sourcePrinter.push();
		
		sourcePrinter.println(perspect.name() + " result;");
		String[] readResultList = Arrays.stream(fieldList)
				.map(Field::name)
				.map(name -> "result." + name)
				.toArray(String[]::new);
		super.ioMethod(symbolTable, namespace, readSerializer, "_inputStream", 
				"_host", sourcePrinter, typeList, readResultList);
		sourcePrinter.println("return result;");
		
		sourcePrinter.pop();
		sourcePrinter.println("}");
		sourcePrinter.println();
		
		// Now the write method.
		sourcePrinter.println("// Implement write method.");
		sourcePrinter.println(writeMethod.replace("<midfix>", 
				perspect.name() + "::") + " {");
		sourcePrinter.push();
		
		String[] writeObjectList = Arrays.stream(fieldList)
				.map(Field::name)
				.map(name -> "_object." + name)
				.toArray(String[]::new);
		super.ioMethod(symbolTable, namespace, writeSerializer, "_outputStream", 
				"_host", sourcePrinter, typeList, writeObjectList);
		
		sourcePrinter.pop();
		sourcePrinter.println("}");
		
	}
}
