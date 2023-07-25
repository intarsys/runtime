package de.intarsys.tools.tlv.common;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} returning {@link TlvElement} instances.
 * 
 */
public abstract class TlvInputStream extends FilterInputStream {

	protected TlvInputStream(InputStream in) {
		super(in);
	}

	/**
	 * The next available {@link TlvElement} or null if none available.
	 * 
	 * @return The next available {@link TlvElement} or null if none available.
	 * @throws IOException
	 */
	public abstract TlvElement readElement() throws IOException;

}
