package net.aegistudio.api.java.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.Connection;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketCall;
import net.aegistudio.api.java.packet.PacketException;
import net.aegistudio.api.java.packet.PacketRegistry;

public class StreamConnection implements Connection {
	protected final ReaderThread<Packet> reader;
	protected final WriterThread<Packet> writer;
	protected final Protocol<Packet> protocol;
	protected final Consumer<Packet> packet;
	
	public StreamConnection(InputStream inputStream, 
			OutputStream outputStream, Consumer<Packet> packet) {
		this.packet = packet;
		this.protocol = new DefaultProtocol(new PacketRegistry());
		this.reader = new ReaderThread<Packet>(inputStream, protocol, 
				packet, this::readErrorConsume);
		this.writer = new WriterThread<Packet>(outputStream, protocol, 
				packet, this::writeErrorConsume);
		
		this.reader.start();
		this.writer.start();
	}
	
	private void readErrorConsume(Exception e) {
		PacketException ePacket = new PacketException();
		ePacket.caller = 0;
		ePacket.exception = new ApiException(e);
		this.packet.accept(ePacket);
	}
	
	private void writeErrorConsume(Packet packet, Exception e) {
		if(packet instanceof PacketCall) {
			PacketException ePacket = new PacketException();
			ePacket.exception = new ApiException(e);
			ePacket.caller = ((PacketCall)packet).caller;
			this.packet.accept(ePacket);
		}
	}

	@Override
	public void send(Packet packet) {
		this.writer.send(packet);
	}

	@Override
	public void close() {
		this.reader.close();
		this.writer.close();
	}
	
	public static Connection.Factory setup(
			InputStream inputStream, OutputStream outputStream) {
		return packet -> 
			new StreamConnection(inputStream, outputStream, packet);
	}
}
