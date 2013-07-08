package de.intarsys.tools.tlv.common;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} returning {@link TlvElement} instances.
 * 
 */
abstract public class TlvInputStream extends FilterInputStream {

	public TlvInputStream(InputStream in) {
		super(in);
	}

	/**
	 * The next available {@link TlvElement} or null if none available.
	 * 
	 * @return The next available {@link TlvElement} or null if none available.
	 * @throws IOException
	 */
	abstract public TlvElement readElement() throws IOException;

}
