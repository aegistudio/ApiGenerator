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
import net.aegistudio.api.java.packet.PacketException;
import net.aegistudio.api.java.packet.PacketReturn;

public abstract class ApiHost extends ApiLocal {
	/** Storage for weak objects. **/
	protected final Map<Integer, ApiObject> registries = new WeakHashMap<>();
	protected final Map<ApiObject, Integer> reverseRegistries = new WeakHashMap<>();
	
	/** Storage for transactions. **/
	protected final Map<Integer, ApiObject> transaction = new HashMap<>();
	protected final Map<ApiObject, Integer> reverseTransaction = new HashMap<>();
	
	protected final Connection connection;
	public ApiHost(Connection.Factory factory) {
		connection = factory.open(this::handle);
		registries.put(0, this);
		reverseRegistries.put(this, 0);
	}
	
	// Default implementation for call table.
	protected Facade[] callTable() {	return new Facade[0]; 	}
	
	private Map<Class<? extends Packet>, Consumer<Packet>> 
		responders = new HashMap<>(); {
		
		responderPut(PacketCall.class, this::handlePacketCall);
		responderPut(PacketReturn.class, this::handlePacketReturn);
		responderPut(PacketException.class, this::handlePacketException);
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
	
	private interface ThenOperation {
		public int then(ApiObject object, Map<Integer, ApiObject> forward, Map<ApiObject, Integer> backward);
	}
	
	private int discriminant(ApiObject apiObject, ThenOperation then) {
		Map<Integer, ApiObject> forward;
		Map<ApiObject, Integer> backward;
		if(apiObject instanceof Transaction) {
			forward = this.transaction;
			backward = this.reverseTransaction;
		}
		else {
			forward = this.registries;
			backward = this.reverseRegistries;
		}
		return then.then(apiObject, forward, backward);
	}
	
	public synchronized int marshal(ApiObject apiObject) {
		return discriminant(apiObject, this::thenMarshal);
	}
	
	private synchronized int thenMarshal(ApiObject apiObject,
			Map<Integer, ApiObject> forward, Map<ApiObject, Integer> backward) {
		
		Integer code = backward.get(apiObject); 
		if(code != null) return code;
		
		synchronized(forward) {
			code = apiObject.hashCode();
			while(forward.get(code) != null) code ++;
			
			forward.put(code, apiObject);
			backward.put(apiObject, code);
			apiObject.register(this);
		}
		return code;
	}

	public synchronized ApiObject retrive(int objectId) throws ApiException {
		ApiObject result;
		synchronized(this.transaction) {
			result = transaction.get(objectId);
		}
		
		if(result == null) synchronized(this.registries) {
			result = registries.get(objectId);
		}
		
		if(result == null) 
			throw new ApiException(
					"Object " + objectId + " not exists.");

		return result;
	}
	
	private synchronized int thenDemarshal(ApiObject apiObject, 
			Map<Integer, ApiObject> forward, Map<ApiObject, Integer> backward) {
		if(backward.containsKey(apiObject)) {
			synchronized(forward) {
				int objectId = backward.remove(apiObject);
				forward.remove(objectId);
			}
		}
		return 0;
	}
	
	public synchronized void demarshal(ApiObject apiObject) {
		if(apiObject == null) return;
		discriminant(apiObject, this::thenDemarshal);
	}
	
	public interface RequesterBlock {
		public void request(DataOutputStream dataOutputStream) throws Exception;
	}
	
	public interface ResponderBlock<T> {
		public T respond(DataInputStream dataInputStream) throws Exception;
	}
	
	public <T> T call(int callee, int call, 
			ResponderBlock<T> responder) throws ApiException {
		return call(callee, call, i -> {}, responder);
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
