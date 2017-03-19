package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.extprim.ApiBytes;

/**
 * <p>An invocation to a side object (either call or return). 
 * The packet format would be:</p>
 * 
 * <table>
 * 		<tr>
 * 			<th>PacketId</th>
 * 			<th>CallerId</th>
 *  		<th>CalleeId</th>
 * 			<th>CallId</th>
 * 			<th>DataLength</th>
 * 			<th>Parameter</th>
 * 		</tr>
 * 		<tr>
 * 			<td>0x02</td>
 * 			<td>Provided</td>
 * 			<td>Provided</td>
 * 			<td>Provided</td>
 * 			<td>Byte Length</td>
 * 			<td>Provided</td>
 * 		</tr>
 * </table>
 * 
 * @author aegistudio
 */

public class PacketCall implements Packet {
	public int caller;
	public int callee;
	public int call;
	public byte[] parameter;

	@Override
	public void read(DataInputStream input) throws IOException {
		caller = input.readInt();
		callee = input.readInt();
		call = input.readInt();
		int length = input.readInt();
		ApiBytes.read(input, parameter = new byte[length]);
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(caller);
		output.writeInt(callee);
		output.writeInt(call);
		output.writeInt(parameter.length);
		ApiBytes.write(output, parameter);
	}

	@Override
	public int id() {
		return 0x02;
	}
}
