package de.intarsys.tools.serialize;

import java.io.IOException;
import java.io.InputStream;

import de.intarsys.tools.stream.StreamTools;

/**
 * A deserializer implementation for plain strings
 * <p>
 * 
 * @see StringSerializationFactory
 */
public class StringDeserializer implements IDeserializer {

	private final InputStream is;

	private String charset = "UTF-8";

	public StringDeserializer(InputStream is) {
		this.is = is;
	}

	public StringDeserializer(InputStream is, String charset) {
		this.is = is;
		this.charset = charset;
	}

	@Override
	public Object deserialize() throws IOException {
		return StreamTools.getString(is, charset);
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
