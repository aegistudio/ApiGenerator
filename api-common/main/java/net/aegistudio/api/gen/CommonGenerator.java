package net.aegistudio.api.gen;

import java.io.IOException;
import java.io.PrintStream;

import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;

public abstract class CommonGenerator implements Generator {
	protected final TypeTable typeTable;
	
	protected CommonGenerator(TypeTable typeTable) {
		this.typeTable = typeTable;
	}

	protected void ioMethod(SymbolTable symbolTable, Namespace namespace,
			Serializer whichSerializer, String inputStream, String apiHosting,
			PrintStream print, Type[] types, String[] names) throws IOException {
		assert types.length == names.length;
		for(int i = 0; i < types.length; i ++) {
			TypeTable.Result typeResult = typeTable.convertType(types[i]);
			SymbolTable.Class symbolClass = symbolTable.lookup(types[i]);
			
			if(symbolClass == SymbolTable.Class.UNDEFINED)
				throw new IllegalArgumentException(
						"Undefined symbol " + typeResult.className(namespace) + "!");
			
			whichSerializer.serialize(print, namespace, 
					inputStream, apiHosting, names[i], 
					typeResult, symbolClass);
		}
	}
}
