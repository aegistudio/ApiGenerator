package net.aegistudio.api.java;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import net.aegistudio.api.java.Connection.Factory;
import net.aegistudio.api.java.extprim.ApiFloat;
import net.aegistudio.api.java.io.StreamConnection;

class TestApiClient extends ApiHost {
	public TestApiClient(Factory factory) {
		super(factory);
	}
	
	public int functionCall1() throws ApiException {
		return call(0, 1, new byte[0], result -> {
			return new DataInputStream(new ByteArrayInputStream(result))
				.readInt();
		});
	}
	
	public float functionCall2() throws ApiException {
		return call(0, 2, new byte[0], result -> {
			return ApiFloat.readFloat(
					new DataInputStream(new ByteArrayInputStream(result)));
		});
	}
}

class TestApiServer extends ApiHost {
	public TestApiServer(Factory factory) {
		super(factory);
	}
	
	public byte[] handleFunctionCall(int call, byte[] request) throws ApiException {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			if(call == 1) dataOutput.writeInt(functionCall1());
			else if(call == 2) ApiFloat.writeDouble(dataOutput, functionCall2());
			else throw new ApiException("No such call with id " + call);
		}
		catch(IOException e) {
			throw new ApiException(e);
		}
		return byteArrayOutput.toByteArray();
	}
	
	public int functionCall1() {
		return 1;
	}
	
	public float functionCall2(){
		return 2.0f;
	}
}

public class TestApiHost extends ThreadingTest {
	TestApiClient client;
	TestApiServer server;
	
	public void setUp() throws IOException {
		super.setUp();
		client = new TestApiClient(StreamConnection.setup(iClient, oClient));
		server = new TestApiServer(StreamConnection.setup(iServer, oServer));
	}
	
	public void tearDown() {
		client.close();
		server.close();
		super.tearDown();
	}
	
	public @Test void testServer() throws ApiException {
		// Server side verify.
		runWithBomb(() -> {
			for(int i = 0; i < 500; i ++) {
				assertEquals(server.functionCall1(), 1);
				assertEquals(server.functionCall2(), 2.0f);	
			}
		});
	}
	
	public @Test void testClient() throws ApiException {	
		// Client side verify.
		runWithBomb(() -> {
			for(int i = 0; i < 500; i ++) {
				try {
					assertEquals(client.functionCall1(), 1);
					assertEquals(client.functionCall2(), 2.0f);
				}
				catch(ApiException apie) {
					apie.printStackTrace();
				}
			}
		});
	}
}