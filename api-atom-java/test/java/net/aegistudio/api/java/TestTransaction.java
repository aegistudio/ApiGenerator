package net.aegistudio.api.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTransaction {
	public @Test void testBlockFeed() throws ApiException {
		Transaction<String> transaction = new Transaction<>(String::new);
		new Thread(() -> {
			try {
				Thread.sleep((long)(Math.random() * 10l));
				transaction.supply("TestBlockFeed".getBytes());
			}
			catch(Exception e) {
				transaction.encounter(e);
			}
		}).start();
		
		assertEquals(transaction.call(), "TestBlockFeed");
	}
	
	public @Test void testExceptionFeed() throws ApiException {
		Transaction<String> transaction = new Transaction<>(String::new);
		new Thread(() -> {
			try {
				Thread.sleep((long)(Math.random() * 10l));
				transaction.encounter(new Exception("TestExceptionFeed"));
			}
			catch(Exception e) {
				transaction.encounter(e);
			}
		}).start();
		
		try {
			transaction.call();
			assertTrue(false);
		}
		catch(ApiException apie) {
			assertEquals(apie.getCause().getMessage(), "TestExceptionFeed");
		}
	}
}
