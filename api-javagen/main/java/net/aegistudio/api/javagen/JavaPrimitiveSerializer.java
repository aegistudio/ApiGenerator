package net.aegistudio.api.javagen;

import java.io.IOException;
import java.io.PrintStream;

import net.aegistudio.api.Primitive;
import net.aegistudio.api.gen.TypeTable.Result;

public class JavaPrimitiveSerializer implements FilteredSerializer {
	protected final Primitive which;
	protected final String readMethod, writeMethod;
	public JavaPrimitiveSerializer(Primitive which, 
			String readMethod, String writeMethod) {
		this.which = which;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
	}
	
	@Override
	public void read(PrintStream printStream, String readerName, 
			Result typeResult) throws IOException {
		
		if(typeResult.variant) {
			// ApiVariant.read(<reader>, typeResult.name[]::new, DataInputStream::readMethod)
		}
		else {
			// <reader>.<readMethod>()
			printStream.print(readerName + "." + readMethod + "()");
		}
	}

	@Override
	public void write(PrintStream printStream, String writerName, 
			String identifier, Result typeResult) throws IOException {
		
	}
	
	@Override
	public boolean accept(Result type) {
		if(type.primitive == null) return false;
		return type.primitive.equals(which);
	}
}
