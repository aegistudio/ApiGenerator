package net.aegistudio.api.gen;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.aegistudio.api.Namespace;
import net.aegistudio.api.gen.TypeTable.Result;

public class ComposeSerializer implements Serializer {
	protected final List<FilteredSerializer> decisionChain = new ArrayList<>();
	public void add(FilteredSerializer filter) {
		decisionChain.add(filter);
	}
	
	public void add(Filter result, String statement) {
		add(new DefaultFilteredSerializer(result, statement));
	}

	@Override
	public void serialize(PrintStream printStream, Namespace namespace, 
			String ioStream, String apiHost, String identifier, 
			Result typeResult, SymbolTable.Class typeClass) throws IOException {
		for(FilteredSerializer decision : decisionChain) 
			if(decision.test(typeResult, typeClass)) {
				decision.serialize(printStream, namespace, ioStream, 
						apiHost, identifier, typeResult, typeClass);
				return;
			}
		throw new IllegalArgumentException(
				"No matching serializer for " + typeResult.name(namespace) + "!");
	}
}
