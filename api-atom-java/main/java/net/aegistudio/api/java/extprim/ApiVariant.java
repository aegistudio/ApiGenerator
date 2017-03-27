package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntFunction;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.ApiHost;
import net.aegistudio.api.java.ApiHost.ResponderBlock;

public class ApiVariant {
	public interface ReadInterface<E> {
		public E read(DataInputStream in, ApiHost apiHost) throws IOException, ApiException;
	}
	
	public interface ReadGenericInterface<E> {
		public void read(DataInputStream in, E e, int index) throws IOException, ApiException;		
	}
	
	public static <E> E arrayRead(DataInputStream dataInputStream, 
			IntFunction<E> array, ReadGenericInterface<E> consumer) throws IOException, ApiException {
		int length = dataInputStream.readInt();
		E arrayObj = array.apply(length);
		for(int i = 0; i < length; i ++) 
			consumer.read(dataInputStream, arrayObj, i);
		return arrayObj;
	}
	
	public static <E> E[] read(DataInputStream dataInputStream, ApiHost apiHost,
			IntFunction<E[]> factory, ReadInterface<E> provider) throws IOException, ApiException {
		return arrayRead(dataInputStream, factory, 
				(in, a, i) -> a[i] = provider.read(in, apiHost));
	}
	
	public static <E> ResponderBlock<E[]> readObject(ApiHost apiHost,
			IntFunction<E[]> factory, ReadInterface<E> provider) {
		return (dataInputStream) -> read(dataInputStream, 
				apiHost, factory, provider);
	}
	
	public static byte[] readByte(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, byte[]::new, 
				(in, a, i) -> a[i] = in.readByte());
	}
	
	public static short[] readShort(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, short[]::new, 
				(in, a, i) -> a[i] = in.readShort());
	}
	
	public static int[] readInt(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, int[]::new, 
				(in, a, i) -> a[i] = in.readInt());
	}
	
	public static long[] readLong(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, long[]::new, 
				(in, a, i) -> a[i] = in.readLong());
	}
	
	public static float[] readFloat(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, float[]::new, 
				(in, a, i) -> a[i] = ApiFloat.readFloat(in));
	}
	
	public static double[] readDouble(DataInputStream dataInputStream) 
			throws IOException, ApiException {
		return arrayRead(dataInputStream, double[]::new, 
				(in, a, i) -> a[i] = ApiFloat.readDouble(in));
	}
	
	public interface WriteGenericInterface<E> {
		public void write(DataOutputStream out, E e, int index) throws IOException, ApiException;
	}
	
	public interface WriteInterface<E> {
		public void write(E e, DataOutputStream out, ApiHost apiHost) throws IOException, ApiException;
	}
	
	public static <E> void arrayWrite(DataOutputStream dataOutputStream, E array, 
			int length, WriteGenericInterface<E> genericWrite) throws IOException, ApiException {
		dataOutputStream.writeInt(length);
		for(int i = 0; i < length; i ++)
			genericWrite.write(dataOutputStream, array, i);
	}
	
	public static <E> void write(DataOutputStream dataOutputStream, ApiHost apiHost,
			E[] data, WriteInterface<E> consumer) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length, 
				(out, a, i) -> consumer.write(a[i], out, apiHost));
	}
	
	public static void writeFloat(DataOutputStream dataOutputStream, 
			float[] data) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length,
				(out, a, i) -> ApiFloat.writeFloat(dataOutputStream, a[i]));
	}
	
	public static void writeInt(DataOutputStream dataOutputStream, 
			int[] data) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length,
				(out, a, i) -> dataOutputStream.writeInt(a[i]));
	}
	
	public static void writeByte(DataOutputStream dataOutputStream, 
			byte[] data) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length,
				(out, a, i) -> dataOutputStream.writeByte(a[i]));
	}
	
	public static void writeDouble(DataOutputStream dataOutputStream, 
			double[] data) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length,
				(out, a, i) -> ApiFloat.writeDouble(dataOutputStream, a[i]));
	}
	
	public static void writeShort(DataOutputStream dataOutputStream, 
			short[] data) throws IOException, ApiException {
		arrayWrite(dataOutputStream, data, data.length,
				(out, a, i) -> dataOutputStream.writeShort(i));
	}
}
