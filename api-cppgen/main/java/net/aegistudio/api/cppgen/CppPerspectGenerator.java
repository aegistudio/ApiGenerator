package net.aegistudio.api.cppgen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.aegistudio.api.Document;
import net.aegistudio.api.Method;
import net.aegistudio.api.Method.Parameter;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;
import net.aegistudio.api.gen.CommonGenerator;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

public abstract class CppPerspectGenerator<Perspect> extends CommonGenerator {
	public final boolean clientSide;
	public final CppReadSerializer readSerializer;
	public final CppWriteSerializer writeSerializer;
	
	public CppPerspectGenerator(boolean clientSide) {
		super(new CppTypeTable(clientSide));
		this.clientSide = clientSide;
		this.readSerializer = new CppReadSerializer();
		this.writeSerializer = new CppWriteSerializer();
	}

	@Override
	public void generate(Context context, Document dom) throws IOException {
		Context includeFolder = context.step("include");
		for(String namespacing : dom.namespace().namespace())
			includeFolder = includeFolder.step(namespacing);
		
		Context sourceFolder = context.step("src");
		SymbolTable symbolTable = new SymbolTable(dom);
		
		for(Perspect perspect : perspect(dom)) {
			OutputStream includeOutput = includeFolder
					.file(name(perspect) + ".h");
			IndentPrintStream includePrint = new IndentPrintStream(includeOutput);
			includePrint.println("#pragma once");
			includePrint.println("#include \"apiGenerate.h\"");
			includePrint.println();
			
			OutputStream sourceOutput = sourceFolder
					.file(name(perspect) + ".cpp");
			IndentPrintStream sourcePrint = new IndentPrintStream(sourceOutput);
			
			construct(perspect, symbolTable, dom.namespace(), 
					includePrint, sourcePrint);
			
			includeOutput.close();
			sourceOutput.close();
		}
	}
	
	protected void openNamespace(Namespace namespace, IndentPrintStream printStream) {
		String[] theNamespacing = namespace.namespace();
		printStream.println("// Open class namespace.");
		for(int i = 0; i < theNamespacing.length; i ++) {
			String namespacing = theNamespacing[i];
			printStream.println("namespace " + namespacing + " { ");
		}
		printStream.println();
	}

	protected void closeNamespace(Namespace namespace, IndentPrintStream printStream) {
		String[] theNamespacing = namespace.namespace();
		// Class un-namespacing.
		printStream.println("// Close class namespace");
		for(int i = theNamespacing.length - 1; i >= 0; i --) {
			String namespacing = theNamespacing[i];
			printStream.println("}; /* namespace " + namespacing + " */ ");
		}
		printStream.println();
	}
	
	protected abstract Perspect[] perspect(Document document);
	
	protected abstract String name(Perspect perspect);
	
	protected abstract void construct(Perspect perspect, SymbolTable symbolTable, 
			Namespace namespace, IndentPrintStream includePrinter, 
			IndentPrintStream sourcePrinter) throws IOException;
	
	protected Type[] filterType(Type[] types, SymbolTable symbolTable) {
		return Arrays.stream(types)
				.filter(type -> type != null)
				.filter(type -> typeTable.convertType(type).primitive == null)
				.filter(type -> {
					SymbolTable.Class symbolClass = symbolTable.lookup(type);
					if(symbolClass.equals(SymbolTable.Class.CALLBACK)) return true;
					if(symbolClass.equals(SymbolTable.Class.INTERFACE)) return true;
					if(symbolClass.equals(SymbolTable.Class.VALUE)) return true;
					return false;
				})
				.toArray(Type[]::new);
	}
	
	protected void methodSignature(String prefix, String midfix, String postfix, 
			SymbolTable symbolTable, Namespace namespace, Method method, 
			IndentPrintStream printer) throws IOException {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(prefix);
		
		// Signature of the return type.
		stringBuilder.append("_EX(");
		TypeTable.Result returnTypeResult 
			= typeTable.convertType(method.result());
		SymbolTable.Class returnClassResult
			= symbolTable.lookup(method.result());
		if(returnClassResult.equals(SymbolTable.Class.VOID))
			stringBuilder.append("void*");
		else stringBuilder.append(returnTypeResult
				.name(returnClassResult, namespace));
		stringBuilder.append(") ");
		
		// Signature of the name.
		stringBuilder.append(midfix);
		stringBuilder.append(method.name());
		stringBuilder.append("(");
		
		// Signature of the param list.
		String[] paramListName = Arrays
				.stream(method.parameters())
				.map(Parameter::name)
				.map(name -> "_" + name)
				.toArray(String[]::new);
		TypeTable.Result[] paramListType = Arrays
				.stream(method.parameters())
				.map(Parameter::type)
				.map(typeTable::convertType)
				.toArray(TypeTable.Result[]::new);
		SymbolTable.Class[] paramListClass = Arrays
				.stream(method.parameters())
				.map(Parameter::type)
				.map(symbolTable::lookup)
				.toArray(SymbolTable.Class[]::new);
		
		for(int i = 0; i < paramListName.length; i ++) {
			if(i > 0) stringBuilder.append(", ");
			stringBuilder.append(paramListType[i]
					.name(paramListClass[i], namespace));
			stringBuilder.append(" ");
			stringBuilder.append(paramListName[i]);
		}
		
		// End of method signature.
		stringBuilder.append(")");
		stringBuilder.append(postfix);
		printer.println(new String(stringBuilder));
	}
}
