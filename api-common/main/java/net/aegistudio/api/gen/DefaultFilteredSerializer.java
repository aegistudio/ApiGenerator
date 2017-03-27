package net.aegistudio.api.gen;

import java.io.IOException;
import java.io.PrintStream;

import net.aegistudio.api.Namespace;
import net.aegistudio.api.gen.SymbolTable.Class;
import net.aegistudio.api.gen.TypeTable.Result;

public class DefaultFilteredSerializer implements FilteredSerializer {
	protected final Filter filter;
	protected final String statement;
	public DefaultFilteredSerializer(Filter filter, String statement) {
		this.filter = filter;
		this.statement = statement;
	}
	
	@Override
	public void serialize(PrintStream printStream, Namespace namespace, 
			String ioStream, String apiHost, String identifier, 
			Result typeResult, SymbolTable.Class typeClass) throws IOException {
		String result = this.statement
				.replace("<stream>", ioStream)
				.replace("<id>", identifier)
				.replace("<host>", apiHost)
				.replace("<typeSingle>", typeResult.component(namespace))
				.replace("<type>", typeResult.name(namespace));
		printStream.println(result);
	}

	@Override
	public boolean test(Result t, Class u) {
		return filter.test(t, u);
	}
}
