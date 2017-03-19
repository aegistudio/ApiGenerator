package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class ApiBytes {
	public static void write(DataOutputStream output, 
			byte[] value) throws IOException {
		output.write(value);
		output.flush();
	}
	
	public static void read(DataInputStream input, 
			byte[] value) throws IOException {
		for(int i = 0; i < value.length; ) {
			int read = input.read(value, i, value.length - i);
			if(read < 0) throw new EOFException();
			i += read;
		}
	}
}
