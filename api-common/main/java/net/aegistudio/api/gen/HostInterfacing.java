package net.aegistudio.api.gen;

import net.aegistudio.api.Document;
import net.aegistudio.api.Method;

public class HostInterfacing implements Interfacing {
	protected final Document document;
	protected final boolean client;
	public HostInterfacing(Document document, boolean clientOrServer) {
		this.document = document;
		this.client = clientOrServer;
	}
	
	@Override
	public String name() {
		return client? document.clientHost(): 
				document.serverHost();
	}

	@Override
	public Method[] methods() {
		return document.functions();
	}

	@Override
	public boolean host() {
		return true;
	}
}
