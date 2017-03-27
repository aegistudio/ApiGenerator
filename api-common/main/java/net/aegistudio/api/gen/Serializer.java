package net.aegistudio.api.gen;

import java.io.IOException;
import java.io.PrintStream;

import net.aegistudio.api.Namespace;

/**
 * Generalize a value read or write statement.
 * 
 * @author aegistudio
 */

public interface Serializer {
	public void serialize(PrintStream printStream, Namespace namespace, 
			String ioStream, String apiHost, String identifier, 
			TypeTable.Result typeResult, SymbolTable.Class typeClass) throws IOException;
}
