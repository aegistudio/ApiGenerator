package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketException;

public class ReaderThread extends Thread {
	protected final DataInputStream in;
	protected final Protocol protocol;
	protected final Consumer<Packet> packetConsumer;
	protected final Consumer<PacketException> routeBackConsumer;
	
	public ReaderThread(InputStream inputStream, 
			Protocol protocol, Consumer<Packet> packet,
			Consumer<PacketException> routeBack) {
		this.in = new DataInputStream(inputStream);
		this.protocol = protocol;
		this.packetConsumer = packet;
		this.routeBackConsumer = routeBack;
	}
	
	public void run() {
		try {
			while(open) {
				Packet packet = protocol.parse(in);
				packetConsumer.accept(packet);
			}
		}
		catch(ApiException e) {
			if(open) {
				PacketException packet = new PacketException();
				
				packet.caller = 0;
				packet.exception = e;
				
				routeBackConsumer.accept(packet);
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
