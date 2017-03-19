package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
	public int id();
	
	public void read(DataInputStream input) throws IOException;
	
	public void write(DataOutputStream output) throws IOException;
}
