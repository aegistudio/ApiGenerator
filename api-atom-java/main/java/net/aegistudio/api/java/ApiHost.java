package net.aegistudio.api.java;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketCall;

public abstract class ApiHost {
	protected final Map<Integer, ApiObject> registries = new TreeMap<>();
	protected final Map<ApiObject, Integer> reverseRegistries = new HashMap<>();
	
	protected final Connection connection;
	public ApiHost(Connection.Factory factory) {
		connection = factory.open(this::handle);
	}
	
	public abstract void handle(Packet packet);
	
	public int marshal(ApiObject apiObject) {
		Integer code = reverseRegistries.get(apiObject); 
		if(code != null) return code;
		
		code = registries.hashCode();
		while(registries.get(code) != null) code ++;
		
		registries.put(code, apiObject);
		reverseRegistries.put(apiObject, code);
		apiObject.register(this);
		return code;
	}

	public ApiObject retrive(int objectId) throws ApiException {
		ApiObject result = registries.get(objectId);
		if(result == null) throw new ApiException(
				"Object " + objectId + " not exists.");
		return result;
	}

	public void demarshal(ApiObject apiObject) {
		if(!reverseRegistries.containsKey(apiObject)) return;
		int objectId = reverseRegistries.get(apiObject);
		reverseRegistries.remove(apiObject);
		registries.remove(objectId);
	}
	
	public interface ResponderBlock<T> {
		public T respond(byte[] response) throws Exception;
	}
	
	public <T> T call(int call, byte[] request, 
			ResponderBlock<T> responder) throws ApiException {
		Transaction<T> transaction = new Transaction<>(responder);
		
		PacketCall packet = new PacketCall();
		packet.caller = marshal(transaction);
		packet.call = call;
		packet.parameter = request;
		
		connection.send(new PacketCall());
		return transaction.call();
	}
}
