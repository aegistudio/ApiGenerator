package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ApiFloat {
	public void writeFloat(DataOutputStream output, 
			float value) throws IOException {	
		output.writeInt(Float.floatToRawIntBits(value));
	}
	
	public float readFloat(DataInputStream input) throws IOException {
		int value = input.readInt();
		return Float.intBitsToFloat(value);
	}
	
	public void writeDouble(DataOutputStream output, 
			double value) throws IOException {
		output.writeLong(Double.doubleToRawLongBits(value));
	}
	
	public double readDouble(DataInputStream input) throws IOException {
		long value = input.readLong();
		return Double.longBitsToDouble(value);
	}
}
