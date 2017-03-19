package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketException;
import net.aegistudio.api.java.packet.Protocol;

public class ReaderThread extends Thread {
	protected final DataInputStream in;
	protected final Protocol protocol;
	protected final Consumer<Packet> packetConsumer;
	public ReaderThread(InputStream inputStream, 
			Protocol protocol, Consumer<Packet> packet) {
		this.in = new DataInputStream(inputStream);
		this.protocol = protocol;
		this.packetConsumer = packet;
	}
	
	public void run() {
		try {
			while(open) {
				Packet packet = protocol.parse(in);
				packetConsumer.accept(packet);
			}
		}
		catch(IOException e) {
			if(open) {
				PacketException packet = new PacketException();
				
				packet.caller = 0;
				packet.exception = new ApiException(e);
				packetConsumer.accept(packet);
			}
		}
	}
	
	protected boolean open = true;
	@SuppressWarnings("deprecation")
	public void close() {
		try {
			open = false;
			this.stop();
			this.in.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
