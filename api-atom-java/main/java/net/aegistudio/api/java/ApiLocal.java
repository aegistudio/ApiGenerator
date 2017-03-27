package net.aegistudio.api.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ApiLocal extends ApiObject {
	public interface Facade {
		public void invoke(ApiHost apiHost, DataInputStream inputStream, 
				DataOutputStream outputStream) throws Exception;
	}
	
	protected abstract Facade[] callTable();
	
	protected int callOffset() { return 0; }
	
	public byte[] response(ApiHost apiHost, int call, byte[] data) throws ApiException {
		int index = call - callOffset();
		Facade[] table = callTable();
		if(index < 0 || index >= table.length)
			throw new ApiException("No such call #" + call + ".");
		
		ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(data);
		DataInputStream dataInput = new DataInputStream(byteArrayInput);
		
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		
		try {
			table[index].invoke(apiHost, dataInput, dataOutput);
			return byteArrayOutput.toByteArray();
		}
		catch(Exception e) {
			throw new ApiException(e);
		}
	}
	
	protected static <T extends ApiLocal> T read(DataInputStream dataInputStream, 
			ApiHost apiHost, Class<T> concrete) throws ApiException, IOException {
		return concrete.cast(apiHost.retrive(dataInputStream.readInt()));
	}
	
	protected static void write(ApiLocal local, DataOutputStream dataOutputStream, 
			ApiHost apiHost) throws ApiException, IOException {
		if(local != null) 
			dataOutputStream.writeInt(apiHost.marshal(local));
		else dataOutputStream.writeInt(0);
	}
}
