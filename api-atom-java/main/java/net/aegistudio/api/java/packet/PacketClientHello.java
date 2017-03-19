package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>Try connecting with the API server.
 * The packet format would be:</p>
 * 
 * <table>
 * 		<tr>
 * 			<th>PacketId</th>
 * 		</tr>
 * 		<tr>
 * 			<td>0x00</td>
 * 		</tr>
 * </table>
 * 
 * @author aegistudio
 */

public class PacketClientHello implements Packet {
	@Override
	public void read(DataInputStream input) throws IOException {
		
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		
	}

	@Override
	public int id() {
		return 0x00;
	}
}
