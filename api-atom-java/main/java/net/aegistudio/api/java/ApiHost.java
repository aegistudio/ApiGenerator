package net.aegistudio.api.java;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

public abstract class ApiHost extends ApiLocal {
	protected final Map<Integer, ApiObject> registries = new WeakHashMap<>();
	protected final Map<ApiObject, Integer> reverseRegistries = new WeakHashMap<>();
	
	protected final Connection connection;
	public ApiHost(Connection.Factory factory) {
		connection = factory.open(this::handle);
		registries.put(0, this);
		reverseRegistries.put(this, 0);
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
			ApiObject object = retrive(packetCall.callee);
			if(!(object instanceof ApiLocal))
				throw new ApiException("Not a callable ApiObject.");
			ApiLocal interfac = (ApiLocal)object;
			byte[] callResult = interfac.response(this,
						packetCall.call, packetCall.parameter);
			

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
	
	public interface RequesterBlock {
		public void request(DataOutputStream dataOutputStream) throws Exception;
	}
	
	public interface ResponderBlock<T> {
		public T respond(DataInputStream dataInputStream) throws Exception;
	}
	
	public <T> T call(int callee, int call, 
			ResponderBlock<T> responder) throws ApiException {
		return call(callee, call, i-> {}, responder);
	}
	
	public void call(int callee, int call, RequesterBlock request)
			throws ApiException {
		call(callee, call, request, (result) -> null);
	}
	
	public void call(int callee, int call) throws ApiException {
		call(callee, call, i -> {}, (result) -> null);
	}
	
	public <T> T call(int callee, int call, RequesterBlock request, 
			ResponderBlock<T> responder) throws ApiException {
		try {
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutput);
			
			request.request(dataOutputStream);
			
			byte[] parameter = byteArrayOutput.toByteArray();
			Transaction<T> transaction = new Transaction<>(responder);
			
			PacketCall packet = new PacketCall();
			packet.caller = marshal(transaction);
			packet.callee = callee;
			packet.call = call;
			packet.parameter = parameter;
			
			connection.send(packet);
			T result = transaction.call();
			demarshal(transaction);
			
			return result;
		}
		catch(Exception e) {
			if(e instanceof ApiException)
				throw (ApiException)e;
			else throw new ApiException(e);
		}
	}
	
	public void close() {
		connection.close();
	}
}
