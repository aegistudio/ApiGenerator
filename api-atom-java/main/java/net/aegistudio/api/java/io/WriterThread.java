package net.aegistudio.api.java.io;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WriterThread<T> extends Thread {
	protected final DataOutputStream out;
	protected final BlockingQueue<T> senderQueue = new BlockingQueue<>();
	protected final Protocol<T> protocol;
	protected final Consumer<T> packetConsumer;
	protected final BiConsumer<T, Exception> errorConsumer;
	
	public WriterThread(OutputStream outputStream, 
			Protocol<T> protocol, Consumer<T> packet,
			BiConsumer<T, Exception> errorConsumer) {
		this.out = new DataOutputStream(outputStream);
		this.protocol = protocol;
		this.packetConsumer = packet;
		this.errorConsumer = errorConsumer;
	}
	
	public void send(T packet) {
		senderQueue.add(packet);
	}
	
	public void run() {
		try {
			while(open) {
				T packet = senderQueue.remove();
				try {
					protocol.transfer(out, packet);
					out.flush();
				}
				catch (Exception e) {
					if(open) errorConsumer.accept(packet, e);
				}
			}
		}
		catch(InterruptedException ie) {
			
		}
	}
	
	public boolean open = true;
	@SuppressWarnings("deprecation")
	public void close() {
		try {
			open = false;
			this.interrupt();
			this.stop();
			this.out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
