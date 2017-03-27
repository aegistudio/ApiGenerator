package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//import net.aegistudio.api.java.extprim.ApiVariant;

/**
 * <p>Transfer the call table as connect response.
 * The packet format would be:</p>
 * 
 * <table>
 * 		<tr>
 * 			<th>PacketId</th>
 * 			<th>ItemCount</th>
 * 			<th>Entries</th>
 * 		</tr>
 * 		<tr>
 * 			<td>0x01</td>
 * 			<td>Count Of Entries</td>
 * 			<td>Provided</td>
 * 		</tr>
 * </table>
 * 
 * @author aegistudio
 */

public class PacketServerHello implements Packet {
	//public String[] entries;
	
	@Override
	public void read(DataInputStream input) throws IOException {
		//entries = ApiVariant.readString(input);
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		//ApiVariant.writeString(output, entries);
	}

	@Override
	public int id() {
		return 0x01;
	}
}
