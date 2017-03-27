package net.aegistudio.api.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;

/**
 * Refers to a remote interface on server side, or a
 * remote callback on client side.
 * 
 * @author aegistudio
 */

public class ApiRemote extends ApiObject {
	protected final int handle;
	protected final ApiHost host;
	
	protected ApiRemote(ApiHost host, int objectId) {
		this.handle = objectId;
		this.host = host;
	}
	
	public int handle() {
		return handle;
	}
	
	protected static <T extends ApiRemote> T read(
			DataInputStream dataInputStream, ApiHost apiHost, 
			BiFunction<ApiHost, Integer, T> factory) throws IOException, ApiException {
		return factory.apply(apiHost, dataInputStream.readInt());
	}
	
	public void write(DataOutputStream dataOutputStream, 
			ApiHost apiHost) throws IOException, ApiException {
		dataOutputStream.writeInt(handle);
	}
}