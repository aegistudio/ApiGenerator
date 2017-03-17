package net.aegistudio.api.gen;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The generation context to which a code
 * generator could write.
 * 
 * @author aegistudio
 */

public interface Context {
	/**
	 * Please notice that the path must be 
	 * separated by "/" character.
	 */
	public OutputStream file(String path) 
			throws IOException;
}
