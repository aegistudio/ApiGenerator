package net.aegistudio.api.javagen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.aegistudio.api.Document;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.Generator;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.Serializer;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

public abstract class JavaPerspectGenerator<Perspect> implements Generator {
	public final JavaTypeTable typeTable = new JavaTypeTable();
	public final JavaReadSerializer readSerial = new JavaReadSerializer();
	public final JavaWriteSerializer writeSerial = new JavaWriteSerializer();

	@Override
	public void generate(Context context, Document dom) throws IOException {
		Context sourceFolder = context.step("main/java");
		Context namespaceFolder = sourceFolder.step(
				dom.namespace().concatenate("/"));
		SymbolTable symbolTable = new SymbolTable(dom);
		
		for(Perspect perspect : perspect(dom)) {
			OutputStream output = namespaceFolder.file(name(perspect) + ".java");
			IndentPrintStream print = new IndentPrintStream(output);
			
			construct(perspect, symbolTable, dom.namespace(), print);
			
			output.close();
		}
	}
	
	protected abstract Perspect[] perspect(Document document);
	
	protected abstract String name(Perspect perspect);
	
	protected abstract void construct(Perspect perspect, SymbolTable symbolTable, 
			Namespace namespace, IndentPrintStream printer) throws IOException;
	
	protected void ioMethod(SymbolTable symbolTable, Namespace namespace,
			Serializer whichSerializer, String inputStream, String apiHosting,
			PrintStream print, Type[] types, String[] names) throws IOException {
		assert types.length == names.length;
		for(int i = 0; i < types.length; i ++) {
			TypeTable.Result typeResult = typeTable.convertType(types[i]);
			SymbolTable.Class symbolClass = symbolTable.lookup(types[i]);
			
			if(symbolClass == SymbolTable.Class.UNDEFINED)
				throw new IllegalArgumentException(
						"Undefined symbol " + typeResult.name(namespace) + "!");
			
			whichSerializer.serialize(print, namespace, 
					inputStream, apiHosting, names[i], 
					typeResult, symbolClass);
		}
	}
}
