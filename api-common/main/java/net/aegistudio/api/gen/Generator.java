package net.aegistudio.api.gen;

import java.io.IOException;

import net.aegistudio.api.Document;

/**
 * <p>Abstraction for API generators.</p>
 * 
 * <p>There're many two sort of generators: the stub generator 
 * for caller side, and the skeleton generator for skeleton 
 * side. The generated code should be based on atom 
 * interfaces.</p>
 * 
 * <p>On stub side, the main atom interface is the Transaction.
 * The transaction will block the execution thread until 
 * a result or an error comes back.</p>
 * 
 * @author aegistudio
 */

public interface Generator {
	public void generate(Context context, Document dom)
		throws IOException;
}
