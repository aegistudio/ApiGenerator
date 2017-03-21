package net.aegistudio.api.java;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;

/**
 * Atom test based on thread communications.
 * 
 * @author aegistudio
 */

public class ThreadingTest {
	PipedOutputStream oClient, oServer;
	PipedInputStream iClient, iServer;
			
	public @Before void setUp() throws IOException {
		oClient = new PipedOutputStream();
		oServer = new PipedOutputStream();
		
		iClient = new PipedInputStream();
		iClient.connect(oServer);
		
		iServer = new PipedInputStream();
		iServer.connect(oClient);
	}
	
	public @After void tearDown() {
		oClient = oServer = null;
		iClient = iServer = null;
	}
	
	protected long timedOut = 2000l;
	
	protected void runWithBomb(Runnable runnable) {
		Thread real = new Thread(runnable);
		real.start();
		runWhile(real::isAlive);
	}
	
	protected void runWhile(Supplier<Boolean> condition) {
		runUntil(() -> !condition.get());
	}
	
	@SuppressWarnings("deprecation")
	protected void runUntil(Supplier<Boolean> condition) {
		Thread timedBomb = new Thread(() -> {
			try {
				Thread.sleep(timedOut);
			} catch (Exception e) {
			}
		});
		timedBomb.start();
		
		while(!condition.get()) {
			if(timedBomb.isAlive()) Thread.yield();
			else throw new AssertionError("Time bomb!");
		}
		
		timedBomb.interrupt();
		timedBomb.stop();
	}
}
