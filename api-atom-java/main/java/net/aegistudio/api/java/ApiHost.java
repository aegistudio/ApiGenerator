package net.aegistudio.api.java;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketCall;
import net.aegistudio.api.java.packet.PacketClientHello;
import net.aegistudio.api.java.packet.PacketException;
import net.aegistudio.api.java.packet.PacketReturn;
import net.aegistudio.api.java.packet.PacketServerHello;

public abstract class ApiHost {
	protected final Map<Integer, ApiObject> registries = new WeakHashMap<>();
	protected final Map<ApiObject, Integer> reverseRegistries = new WeakHashMap<>();
	
	protected final Connection connection;
	public ApiHost(Connection.Factory factory) {
		connection = factory.open(this::handle);
	}
	
	private Map<Class<? extends Packet>, Consumer<Packet>> 
		responders = new HashMap<>(); {
		
		responderPut(PacketCall.class, this::handlePacketCall);
		responderPut(PacketReturn.class, this::handlePacketReturn);
		responderPut(PacketException.class, this::handlePacketException);
		responderPut(PacketClientHello.class, this::handleClientHello);
		responderPut(PacketServerHello.class, this::handleServerHello);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Packet> void responderPut(
			Class<T> packet, Consumer<T> consumer) {
		responders.put(packet, (Consumer<Packet>)consumer);
	}
	
	public void handle(Packet packet) {
		responders.get(packet.getClass()).accept(packet);
	}
	
	protected void handlePacketCall(PacketCall packetCall) {
		Packet resultPacket = null;
		try {
			byte[] callResult;
			if(packetCall.callee == 0) 
				callResult = handleFunctionCall(
						packetCall.call, packetCall.parameter);
			else {
				ApiObject object = retrive(packetCall.callee);
				if(!(object instanceof ApiInterface))
					throw new ApiException("Not a callable ApiObject.");
				ApiInterface interfac = (ApiInterface)object;
				callResult = interfac.response(
							packetCall.call, packetCall.parameter);
			}

			PacketReturn packetReturn = new PacketReturn();
			packetReturn.caller = packetCall.caller;
			packetReturn.result = callResult;
			resultPacket = packetReturn;
		}
		catch(Exception e) {
			PacketException packetException = new PacketException();
			packetException.caller = packetCall.caller;
			packetException.exception = new ApiException(e);
			resultPacket = packetException;
		}
		connection.send(resultPacket);
	}
	
	protected byte[] handleFunctionCall(int functionCall, 
			byte[] parameter) throws ApiException {
		throw new ApiException("No supported function call.");
	}
	
	private void retriveTransaction(int callerId, 
			Consumer<Transaction<?>> then) {
		try {
			ApiObject caller = retrive(callerId);
			if(!(caller instanceof Transaction)) 
				throw new ApiException("Not an transaction!");
			then.accept((Transaction<?>)caller);
		}
		catch(ApiException apie) {
			handleApiException(apie);			
		}
	}
	
	protected void handlePacketReturn(PacketReturn packetReturn) {
		retriveTransaction(packetReturn.caller, transaction -> 
			transaction.supply(packetReturn.result));
	}
	
	protected void handlePacketException(PacketException packetException) {
		if(packetException.caller == 0) handleApiException(
				new ApiException(packetException.exception));
		else retriveTransaction(packetException.caller, transaction -> 
			transaction.encounter(packetException.exception));
	}
	
	protected void handleApiException(ApiException e) {
		e.printStackTrace();
	}
	
	protected void handleClientHello(PacketClientHello clientHello) {
		handleApiException(new ApiException(
				"Cannot perform client hello on this side."));
	}
	
	protected void handleServerHello(PacketServerHello clientHello) {
		handleApiException(new ApiException(
				"Cannot perform server hello on this side."));
	}
	
	public int marshal(ApiObject apiObject) {
		Integer code = reverseRegistries.get(apiObject); 
		if(code != null) return code;
		
		code = apiObject.hashCode();
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
	
	public <T> T call(int callee, int call, byte[] request, 
			ResponderBlock<T> responder) throws ApiException {
		Transaction<T> transaction = new Transaction<>(responder);
		
		PacketCall packet = new PacketCall();
		packet.caller = marshal(transaction);
		packet.callee = callee;
		packet.call = call;
		packet.parameter = request;
		
		connection.send(packet);
		T result = transaction.call();
		demarshal(transaction);
		
		return result;
	}
	
	public void close() {
		connection.close();
	}
}
