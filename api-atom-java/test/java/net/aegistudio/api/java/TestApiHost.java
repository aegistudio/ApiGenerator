package net.aegistudio.api.java;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import net.aegistudio.api.java.Connection.Factory;
import net.aegistudio.api.java.extprim.ApiFloat;
import net.aegistudio.api.java.extprim.ApiVariant;
import net.aegistudio.api.java.io.StreamConnection;

class TestApiClient extends ApiHost {
	public TestApiClient(Factory factory) {
		super(factory);
	}
	
	public int functionCall1() throws ApiException {
		return call(0, 0, dataInputStream -> {
			int resultValue = 0;
			resultValue = dataInputStream.readInt(); 
			return resultValue;
		});
	}
	
	public float functionCall2() throws ApiException {
		return call(0, 1, dataInputStream -> {
			float resultValue = 0;
			resultValue = ApiFloat.readFloat(dataInputStream);
			return resultValue;
		});
	}
	
	public float arraySum(float[] floatArray) throws ApiException {
		return call(0, 2, dataOutputStream -> {
			ApiVariant.writeFloat(dataOutputStream, floatArray);
		}, dataInputStream -> {
			float resultValue = 0;
			resultValue = ApiFloat.readFloat(dataInputStream);
			return resultValue;
		});
	}

	@Override
	protected Facade[] callTable() {
		throw new IllegalStateException("No call table on client.");
	}
}

class TestApiServer extends ApiHost {
	public TestApiServer(Factory factory) {
		super(factory);
	}
	
	private Facade[] callTable = new Facade[] {
		this::invokeFunctionCall1,
		this::invokeFunctionCall2,
		this::invokeFunctionCall3,
	};
	
	@Override
	protected Facade[] callTable() {
		return callTable;
	}
	
	private void invokeFunctionCall1(DataInputStream dataInput, 
			DataOutputStream dataOutput) throws IOException{
		dataOutput.writeInt(functionCall1());
	}
	
	private void invokeFunctionCall2(DataInputStream dataInput,
			DataOutputStream dataOutput) throws IOException {
		ApiFloat.writeFloat(dataOutput, functionCall2());
	}
	
	private void invokeFunctionCall3(DataInputStream dataInput,
			DataOutputStream dataOutput) throws IOException {
		float[] input = ApiVariant.readFloat(dataInput);
		ApiFloat.writeFloat(dataOutput, arraySum(input));
	}
	
	public int functionCall1() {
		return 1;
	}
	
	public float functionCall2() {
		return 2.0f;
	}
	
	public float arraySum(float[] input) {
		float sum = 0.f;
		for(int i = 0; i < input.length; i ++)
			sum += input[i];
		return sum;
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
			for(int i = 0; i < 1000; i ++) {
				assertEquals(server.functionCall1(), 1);
				assertEquals(server.functionCall2(), 2.0f);
				assertEquals(server.arraySum(
						new float[] {1.0f, 2.0f, 3.0f}), 6.0f);
			}
		});
	}
	
	public @Test void testClient() throws ApiException {	
		// Client side verify.
		runWithBomb(() -> {
			for(int i = 0; i < 1000; i ++) {
				try {
					assertEquals(client.functionCall1(), 1);
					assertEquals(client.functionCall2(), 2.0f);
					assertEquals(client.arraySum(
							new float[] {1.0f, 2.0f, 3.0f}), 6.0f);
				}
				catch(ApiException apie) {
					apie.printStackTrace();
				}
			}
		});
	}
}