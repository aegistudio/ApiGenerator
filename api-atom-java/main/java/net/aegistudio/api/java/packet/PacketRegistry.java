package net.aegistudio.api.java.packet;

import java.util.function.Supplier;

import net.aegistudio.api.java.ApiException;

// Even packets are written as concrete implementation of 
// Packet interface, the kinds and structure of each packet
// is known and should stay the same among different languages.

public class PacketRegistry {
	@SuppressWarnings("unchecked")
	public final Supplier<Packet>[] registries = new Supplier[256]; 
	
	public void register(Supplier<Packet> registry) {
		Packet someNew = registry.get();
		if(registries[someNew.id()] != null)
			throw new IllegalArgumentException("Already registered.");
		registries[someNew.id()] = registry;
	}
	
	{
		register(PacketCall::new);
		register(PacketReturn::new);
		register(PacketException::new);
	}
	
	public Packet newPacket(int packetId) 
			throws ApiException {
		if(packetId < 0 || packetId >= 256 || registries[packetId] == null)
			throw new ApiException(
					"Unrecognized packet id #" + packetId + ".");
		return registries[packetId].get();
	}
	
	public int lookPacket(Packet packet) {
		return packet.id();
	}
}
