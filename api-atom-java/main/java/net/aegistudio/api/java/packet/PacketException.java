package net.aegistudio.api.java.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aegistudio.api.java.ApiException;
import net.aegistudio.api.java.extprim.ApiString;

/**
 * <p>When a client side call generates an 
 * exception. The packet format would be:</p>
 * 
 * <table>
 * 		<tr>
 * 			<th>PacketId</th>
 * 			<th>CallerId</th>
 * 			<th>DataLength</th>
 * 			<th>Message</th>
 * 		</tr>
 * 		<tr>
 * 			<td>0x04</td>
 * 			<td>Provided</td>
 * 			<td>LengthOfMessage</td>
 * 			<td>Provided</td>
 * 		</tr>
 * </table>
 * 
 * @author aegistudio
 */

public class PacketException implements Packet {
	public int caller;
	public ApiException exception;

	@Override
	public void read(DataInputStream input) throws IOException {
		caller = input.readInt();
		exception = new ApiException(ApiString.read(input));
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.write(caller);
		ApiString.write(output, exception.getMessage());
	}

	@Override
	public int id() {
		return 0x04;
	}
}
