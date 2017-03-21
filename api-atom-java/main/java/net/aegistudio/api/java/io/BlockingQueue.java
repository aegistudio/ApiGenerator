package net.aegistudio.api.java.io;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This queue will block until some one prime
 * something into it.
 * 
 * @author luohaoran
 *
 */

public class BlockingQueue<T> {
	public Queue<T> queue = new LinkedList<>();
	
	public synchronized T remove() 
			throws InterruptedException {
		
		if(queue.isEmpty()) this.wait();
		return queue.remove();
	}
	
	public synchronized void add(T t) {
		queue.add(t);
		notify();
	}
}