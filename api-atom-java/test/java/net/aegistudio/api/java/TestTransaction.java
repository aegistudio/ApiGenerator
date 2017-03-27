package net.aegistudio.api.java;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.junit.Test;

import net.aegistudio.api.java.extprim.ApiString;

public class TestTransaction {
	public @Test void testBlockFeed() throws ApiException {
		Transaction<String> transaction = new Transaction<>(ApiString::read);
		new Thread(() -> {
			try {
				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
				DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);
				ApiString.write("TestBlockFeed", dataOutputStream);
				Thread.sleep((long)(Math.random() * 10l));
				transaction.supply(byteOutputStream.toByteArray());
			}
			catch(Exception e) {
				transaction.encounter(e);
			}
		}).start();
		
		assertEquals(transaction.call(), "TestBlockFeed");
	}
	
	public @Test void testExceptionFeed() throws ApiException {
		Transaction<String> transaction = new Transaction<>(ApiString::read);
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
