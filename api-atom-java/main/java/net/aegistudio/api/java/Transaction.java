package net.aegistudio.api.java;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.aegistudio.api.java.ApiHost.ResponderBlock;

public class Transaction<T> extends ApiObject {
	public ResponderBlock<T> block;
	public Transaction(ResponderBlock<T> responder) {
		this.block = responder;
	}
	
	protected byte[] resultBlock;
	protected Exception encounter;
	public synchronized T call() throws ApiException {
		try {
			if(resultBlock == null) this.wait();
			if(encounter != null) throw encounter;
			return block.respond(new DataInputStream(
					new ByteArrayInputStream(resultBlock)));
		}
		catch(Exception e) {
			throw new ApiException(e);
		}
	}
	
	public synchronized void supply(byte[] result) {
		if(resultBlock == null) {
			resultBlock = result;
			this.notify();
		}
	}
	
	public synchronized void encounter(Exception e) {
		if(resultBlock == null) {
			encounter = e;
			this.notify();
		}
	}
}
