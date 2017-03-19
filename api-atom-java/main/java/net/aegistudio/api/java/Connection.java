package net.aegistudio.api.java;

import java.util.function.Consumer;

import net.aegistudio.api.java.packet.Packet;

public interface Connection {
	public void send(Packet packet);
	
	public void close();
	
	public interface Factory {
		public Connection open(Consumer<Packet> packet);
	}
}
