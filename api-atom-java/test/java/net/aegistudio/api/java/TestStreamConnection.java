package net.aegistudio.api.java;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import net.aegistudio.api.java.io.StreamConnection;
import net.aegistudio.api.java.packet.PacketCall;

public class TestStreamConnection {
	
	int counter = 0;
	public @Test void test() throws IOException, InterruptedException {
		// Configure in-process pipes.
		PipedOutputStream oClient = new PipedOutputStream();
		PipedOutputStream oServer = new PipedOutputStream();
		PipedInputStream iClient = new PipedInputStream();	iClient.connect(oServer);
		PipedInputStream iServer = new PipedInputStream();	iServer.connect(oClient);
		
		// The client connection should not receive any packet.
		Connection client = new StreamConnection(iClient, oClient, 
				packet -> assertTrue(false));
		
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
		
		// Send the client packet for 100 times and wait for complete.
		for(int i = 0; i < 1000; i ++) 
			client.send(sent);
		while(counter < 1000) Thread.yield();
		assertEquals(counter, 1000);
		
		// Clean up and close.
		client.close();
		server.close();
	}
}
