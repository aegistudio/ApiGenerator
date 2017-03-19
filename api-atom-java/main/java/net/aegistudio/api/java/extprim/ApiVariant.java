package net.aegistudio.api.java.extprim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;

public class ApiVariant {
	public interface ReadInterface<E> {
		public E read(DataInputStream in) throws IOException;
	}
	
	public static <E> E[] read(DataInputStream dataInputStream, 
			Function<Integer, E[]> factory, ReadInterface<E> provider) throws IOException {
		E[] buffer = factory.apply(dataInputStream.readInt());
		for(int i = 0; i < buffer.length; i ++) 
			buffer[i] = provider.read(dataInputStream);
		return buffer;
	}
	
	public interface WriteInterface<E> {
		public void write(DataOutputStream out, E e) throws IOException;
	}
	
	public static <E> void write(DataOutputStream dataOutputStream, 
			E[] data, WriteInterface<E> consumer) throws IOException {
		dataOutputStream.writeInt(data.length);
		for(int i = 0; i < data.length; i ++)
			consumer.write(dataOutputStream, data[i]);
	}
}
