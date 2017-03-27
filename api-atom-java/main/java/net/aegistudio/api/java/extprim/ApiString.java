package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.ApiHost;

public class ApiString {
	public static String read(DataInputStream input) throws IOException {
		int length = input.readInt();
		byte[] stringBuffer = new byte[length];
		ApiBytes.read(input, stringBuffer);
		return new String(stringBuffer);
	}
	
	public static String read(DataInputStream input, ApiHost apiHost) throws IOException {
		return read(input);
	}
	
	public static void write(String string, DataOutputStream output) throws IOException {
		byte[] bytes = string.getBytes();
		output.writeInt(bytes.length);
		ApiBytes.write(output, bytes);
	}
	
	public static void write(String string, DataOutputStream output, 
			ApiHost apiHost) throws IOException {
		write(string, output);
	}
}
