package de.intarsys.tools.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import de.intarsys.tools.string.StringTools;

/**
 * A {@link SerializationContext} based on {@link InputStream} and
 * {@link OutputStream}.
 * 
 */
public class StreamSerializationContext extends SerializationContext {

	private final InputStream inputStream;

	private final OutputStream outputStream;

	public StreamSerializationContext() {
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
	}

	public StreamSerializationContext(byte[] input) {
		inputStream = new ByteArrayInputStream(input);
		outputStream = null;
	}

	public StreamSerializationContext(InputStream inputStream, OutputStream outputStream) {
		super();
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public StreamSerializationContext(String input) {
		byte[] data;
		if (input == null) {
			input = StringTools.EMPTY;
		}
		data = input.getBytes(StandardCharsets.UTF_8); // $NON-NLS-1$
		inputStream = new ByteArrayInputStream(data);
		outputStream = null;
	}

	public byte[] getBytes() {
		if (outputStream instanceof ByteArrayOutputStream) {
			return ((ByteArrayOutputStream) outputStream).toByteArray();
		}
		return new byte[0];
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}
