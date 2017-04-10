package net.aegistudio.api.java.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.ApiException;

public interface Protocol<T> {
	public T parse(DataInputStream input) 
			throws IOException, ApiException;
	
	public void transfer(DataOutputStream output, 
			T something) throws IOException;
}
