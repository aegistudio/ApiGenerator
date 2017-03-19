package net.aegistudio.api.java;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public abstract class ApiObject {
	public Set<WeakReference<ApiHost>> responder = new HashSet<>();
	
	public void register(ApiHost connection) {
		responder.add(new WeakReference<>(connection));
		connection.marshal(ApiObject.this);
	}
	
	public void destroy() {
		responder.forEach(reference -> {
			ApiHost notifying = reference.get();
			if(notifying != null) notifying.demarshal(ApiObject.this);
		});
	}
	
	public void finalize() {
		destroy();
	}
}
