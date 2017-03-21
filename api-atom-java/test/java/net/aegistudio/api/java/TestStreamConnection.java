package net.aegistudio.api.java;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.aegistudio.api.java.io.StreamConnection;
import net.aegistudio.api.java.packet.PacketCall;

public class TestStreamConnection extends ThreadingTest {
	
	int counter;
	public void setUp() throws IOException { 
		super.setUp();
		counter = 0; 
	}
	
	public @Test void test() throws IOException, InterruptedException {	
		// Construct the packet to send.
		String parameter = "TestCall";
		PacketCall sent = new PacketCall();
		sent.call = 1;
		sent.caller = 2;
		sent.callee = 3;
		sent.parameter = parameter.getBytes();	
		
		// The server should receive the packets the same as the sent ones.
		Connection server = new StreamConnection(iServer, oServer, packet -> {
			assertTrue(packet instanceof PacketCall);
			PacketCall received = (PacketCall) packet;
			assertEquals(received.call, sent.call);
			assertEquals(received.caller, sent.caller);
			assertEquals(received.callee, sent.callee);
			assertEquals(new String(received.parameter), parameter);
			counter ++;
		});
		
		// The client connection should not receive any packet.
		Connection client = new StreamConnection(iClient, oClient, 
				packet -> assertTrue(false));
		for(int i = 0; i < 1000; i ++) client.send(sent);
				
		// Wait for complete.
		runWhile(() -> (counter < 1000));
		
		// Clean up and close.
		client.close();
		server.close();
	}
}
