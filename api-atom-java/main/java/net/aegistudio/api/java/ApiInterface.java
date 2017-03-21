package net.aegistudio.api.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class ApiInterface extends ApiObject {
	public interface Facade {
		public void invoke(DataInputStream inputStream, 
				DataOutputStream outputStream) throws Exception;
	}
	
	protected abstract Facade[] callTable();
	
	protected int callOffset() { return 0; }
	
	public byte[] response(int call, byte[] data) throws ApiException {
		int index = call - callOffset();
		Facade[] table = callTable();
		if(index < 0 || index >= table.length)
			throw new ApiException("No such call #" + call + ".");
		
		ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(data);
		DataInputStream dataInput = new DataInputStream(byteArrayInput);
		
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		
		try {
			table[index].invoke(dataInput, dataOutput);
			return byteArrayOutput.toByteArray();
		}
		catch(Exception e) {
			throw new ApiException(e);
		}
	}
}
