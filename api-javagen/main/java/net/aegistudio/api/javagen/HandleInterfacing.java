package net.aegistudio.api.javagen;

import net.aegistudio.api.Interface;
import net.aegistudio.api.Method;

public class HandleInterfacing implements Interfacing {
	protected final Interface interfac;
	public HandleInterfacing(Interface interfac) {
		this.interfac = interfac;
	}
	
	@Override
	public String name() {
		return interfac.name();
	}

	@Override
	public Method[] methods() {
		return interfac.methods();
	}

	@Override
	public boolean host() {
		return false;
	}
}
