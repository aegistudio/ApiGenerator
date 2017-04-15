package net.aegistudio.api.javagen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.aegistudio.api.Document;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.gen.CommonGenerator;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.SymbolTable;

public abstract class JavaPerspectGenerator<Perspect> extends CommonGenerator {
	protected JavaPerspectGenerator() {
		super(new JavaTypeTable());
	}

	public final JavaReadSerializer readSerial = new JavaReadSerializer();
	public final JavaWriteSerializer writeSerial = new JavaWriteSerializer();

	@Override
	public void generate(Context context, Document dom) throws IOException {
		Context namespaceFolder = context.step(
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
	
	protected void javaHeader(PrintStream printStream, Namespace namespace) {
		printStream.println("package <namespace>;"
				.replace("<namespace>", namespace.concatenate(".")));
		printStream.println();
		
		printStream.println("import java.io.*;");
		printStream.println("import net.aegistudio.api.java.*;");
		printStream.println("import net.aegistudio.api.java.extprim.*;");
		printStream.println();
	}
}
