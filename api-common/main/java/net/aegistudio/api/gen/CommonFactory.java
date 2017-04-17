package net.aegistudio.api.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.aegistudio.api.Document;

public abstract class CommonFactory implements GeneratorFactory {
	protected Interfacing[] allInterfaces(Document dom, boolean client) {
		List<Interfacing> interfaces = new ArrayList<>();
		Arrays.stream(dom.interfaces())
			.map(HandleInterfacing::new)
			.forEach(interfaces::add);
		interfaces.add(new HostInterfacing(dom, client));
		return interfaces.toArray(new Interfacing[0]);
	}
	
	public Function<Document, Interfacing[]> allInterfaces(boolean client) {
		return document -> allInterfaces(document, client);
	}
	
	protected Interfacing[] allCallbacks(Document dom) {
		return Arrays.stream(dom.callbacks())
				.map(HandleInterfacing::new)
				.toArray(Interfacing[]::new);
	}
}
