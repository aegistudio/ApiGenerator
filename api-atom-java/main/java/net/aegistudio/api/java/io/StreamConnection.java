package net.aegistudio.api.java.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;

import net.aegistudio.api.java.Connection;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.Protocol;

public class StreamConnection implements Connection {
	protected final ReaderThread reader;
	protected final WriterThread writer;
	protected final Protocol protocol;
	public StreamConnection(InputStream inputStream, 
			OutputStream outputStream, Consumer<Packet> packet) {
		this.protocol = new Protocol();
		this.reader = new ReaderThread(inputStream, protocol, packet);
		this.writer = new WriterThread(outputStream, protocol, packet);
		
		this.reader.start();
		this.writer.start();
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
	
	public static Function<Consumer<Packet>, StreamConnection> setup(
			InputStream inputStream, OutputStream outputStream) {
		return packet -> 
			new StreamConnection(inputStream, outputStream, packet);
	}
}
