package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.aegistudio.api.java.ApiException;

public class ReaderThread<T> extends Thread {
	protected final DataInputStream in;
	protected final Protocol<T> protocol;
	protected final Consumer<T> packetConsumer;
	protected final Consumer<Exception> errorConsumer;
	
	public ReaderThread(InputStream inputStream, 
			Protocol<T> protocol, Consumer<T> packet,
			Consumer<Exception> error) {
		this.in = new DataInputStream(inputStream);
		this.protocol = protocol;
		this.packetConsumer = packet;
		this.errorConsumer = error;
	}
	
	public void run() {
		try {
			while(open) {
				try {
					T packet = protocol.parse(in);
					new Thread(() ->
						packetConsumer.accept(packet)).start();
				}
				catch(ApiException e) {
					if(open) errorConsumer.accept(e);
				}
			}
		}
		catch(IOException e) {
			if(open) errorConsumer.accept(e);
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
