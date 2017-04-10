package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketRegistry;

public class DefaultProtocol implements Protocol<Packet> {
	protected final PacketRegistry registry;
	public DefaultProtocol(PacketRegistry registry) {
		this.registry = registry;
	}
	
	public Packet parse(DataInputStream input) 
			throws IOException, ApiException {
		
		int pid = input.read();
		if(pid < 0) throw new EOFException();
		
		Packet packet = registry.newPacket(pid);
		
		packet.read(input);
		return packet;
	}
	
	public void transfer(DataOutputStream output, 
			Packet packet) throws IOException {
		output.write(registry.lookPacket(packet));
		packet.write(output);
	}
}
