package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.extprim.ApiBytes;

/**
 * <p>Return the result from a certain object
 * call. The packet format would be:</p>
 * 
 * <table>
 * 		<tr>
 * 			<th>PacketId</th>
 * 			<th>CallerId</th>
 * 			<th>DataLength</th>
 * 			<th>Result</th>
 * 		</tr>
 * 		<tr>
 * 			<td>0x03</td>
 * 			<td>Provided</td>
 * 			<td>LengthOfResult</td>
 * 			<td>Provided</td>
 * 		</tr>
 * </table>
 * 
 * @author aegistudio
 */
public class PacketReturn implements Packet {
	public int caller;
	public byte[] result;
	
	@Override
	public void read(DataInputStream input) throws IOException {
		caller = input.readInt();
		int length = input.readInt();
		ApiBytes.read(input, result = new byte[length]);
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.write(caller);
		output.write(result.length);
		ApiBytes.write(output, result);
	}

	@Override
	public int id() {
		return 0x03;
	}
}
