package net.aegistudio.api.java.io;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.packet.Packet;
import net.aegistudio.api.java.packet.PacketCall;
import net.aegistudio.api.java.packet.PacketException;
import net.aegistudio.api.java.packet.Protocol;

public class WriterThread extends Thread {
	protected final DataOutputStream out;
	protected final BlockingQueue<Packet> senderQueue = new BlockingQueue<>();
	protected final Protocol protocol;
	protected final Consumer<Packet> packetConsumer;
	
	public WriterThread(OutputStream outputStream, 
			Protocol protocol, Consumer<Packet> packet) {
		this.out = new DataOutputStream(outputStream);
		this.protocol = protocol;
		this.packetConsumer = packet;
	}
	
	public void send(Packet packet) {
		senderQueue.add(packet);
	}
	
	public void run() {
		try {
			while(open) {
				Packet packet = senderQueue.remove();
				try {
					protocol.transfer(out, packet);
					out.flush();
				}
				catch (Exception e) {
					if(open && packet instanceof PacketCall) {
						PacketException ePacket = new PacketException();
						ePacket.exception = new ApiException(e);
						ePacket.caller = ((PacketCall)packet).caller;
						packetConsumer.accept(ePacket);
					}
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
			this.stop();
			this.out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
