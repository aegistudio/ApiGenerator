package net.aegistudio.api.gen;

import net.aegistudio.api.Document;
import net.aegistudio.api.Interface;
import net.aegistudio.api.Method;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Value;

public class FlavourDocument implements Document {
	protected final String flavourText;
	protected final Document innerDocument;
	public FlavourDocument(String flavourText, Document document) {
		this.flavourText = flavourText;
		this.innerDocument = document;
	}
	
	@Override
	public Namespace namespace() {
		return new Namespace(innerDocument
				.namespace().concatenate(flavourText, "."));
	}

	@Override
	public String clientHost() {
		return innerDocument.clientHost();
	}

	@Override
	public String serverHost() {
		return innerDocument.serverHost();
	}

	@Override
	public String version() {
		return innerDocument.version();
	}

	@Override
	public String distribution() {
		return innerDocument.distribution();
	}

	@Override
	public Interface[] interfaces() {
		return innerDocument.interfaces();
	}

	@Override
	public Interface[] callbacks() {
		return innerDocument.callbacks();
	}

	@Override
	public Value[] values() {
		return innerDocument.values();
	}

	@Override
	public Method[] functions() {
		return innerDocument.functions();
	}
}
