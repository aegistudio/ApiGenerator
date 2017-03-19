package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.function.Supplier;

public class Protocol {
	@SuppressWarnings("unchecked")
	protected Supplier<Packet>[] registries = new Supplier[256];
	
	public void register(Supplier<Packet> packet) {
		Packet stub = packet.get(); 
		if(registries[stub.id()] != null)
			throw new IllegalStateException(
					"Conflict packet id " + stub.id() + ".");
		registries[stub.id()] = packet;
	}
	
	public Packet parse(DataInputStream input) throws IOException {
		int pid = input.read();
		if(pid < 0) throw new EOFException();
		Supplier<Packet> factory = registries[pid];
		if(factory == null) 
			throw new IOException("Unrecognized packet id.");
		
		Packet packet = factory.get();
		packet.read(input);
		return packet;
	}
	
	public void transfer(DataOutputStream output, 
			Packet packet) throws IOException {
		output.write(packet.id());
		packet.write(output);
	}
	
	public Protocol() {
		register(PacketClientHello::new);
		register(PacketServerHello::new);
		register(PacketCall::new);
		register(PacketReturn::new);
		register(PacketException::new);
	}
}
