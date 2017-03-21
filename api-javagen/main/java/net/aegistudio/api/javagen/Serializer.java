package net.aegistudio.api.javagen;

import java.io.IOException;
import java.io.PrintStream;

import net.aegistudio.api.gen.TypeTable;

/**
 * Generalize a value read or write statement.
 * 
 * @author aegistudio
 */

public interface Serializer {
	public void read(PrintStream printStream, String identifier, 
			TypeTable.Result typeResult) throws IOException;
	
	public void write(PrintStream printStream, String writerName, 
			String identifier, TypeTable.Result typeResult) throws IOException;
}
