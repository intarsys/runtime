package de.intarsys.tools.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import de.intarsys.tools.string.StringTools;

/**
 * A {@link SerializationContext} based on {@link InputStream} and
 * {@link OutputStream}.
 * 
 */
public class StreamSerializationContext extends SerializationContext {

	final private InputStream inputStream;

	final private OutputStream outputStream;

	public StreamSerializationContext() {
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
	}

	public StreamSerializationContext(byte[] input) {
		inputStream = new ByteArrayInputStream(input);
		outputStream = null;
	}

	public StreamSerializationContext(InputStream inputStream,
			OutputStream outputStream) {
		super();
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public StreamSerializationContext(String input) {
		byte[] data;
		try {
			if (input == null) {
				input = StringTools.EMPTY;
			}
			data = input.getBytes("UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 not supported"); //$NON-NLS-1$
		}
		inputStream = new ByteArrayInputStream(data);
		outputStream = null;
	}

	public byte[] getBytes() {
		if (outputStream instanceof ByteArrayOutputStream) {
			return ((ByteArrayOutputStream) outputStream).toByteArray();
		}
		return null;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}
