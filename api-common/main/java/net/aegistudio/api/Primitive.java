package net.aegistudio.api;

/**
 * The only-allowed primitive types that 
 * could be defined in the api dom.
 * 
 * @author aegistudio
 *
 */

public enum Primitive {
	/** Always one byte long, smallest. */	BYTE,
	/** Always 2 bytes long. */				SHORT,
	/** Always 4 bytes long. */				INT,
	/** Always 8 bytes long. */				LONG,
	/** Always 4 bytes long. IEEE754. */	FLOAT,
	/** Always 8 bytes long. IEEE754. */	DOUBLE,
	
	/** 
	 * Depends on locale and encoding, 
	 * variant but synonym to byte[].
	 */
	STRING;
}