package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.packet.Packet;

public interface Protocol {
	public Packet parse(DataInputStream input) 
			throws IOException, ApiException;
	
	public void transfer(DataOutputStream output, 
			Packet packet) throws IOException;
}
