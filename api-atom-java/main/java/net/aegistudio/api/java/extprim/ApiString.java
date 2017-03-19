package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ApiString {
	public static String read(DataInputStream input) throws IOException {
		int length = input.readInt();
		byte[] stringBuffer = new byte[length];
		ApiBytes.read(input, stringBuffer);
		return new String(stringBuffer);
	}
	
	public static void write(DataOutputStream output, String string) throws IOException {
		byte[] bytes = string.getBytes();
		output.writeInt(bytes.length);
		ApiBytes.write(output, bytes);
	}
}
