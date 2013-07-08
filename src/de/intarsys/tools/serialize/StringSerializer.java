package de.intarsys.tools.serialize;

import java.io.IOException;
import java.io.OutputStream;

import de.intarsys.tools.string.StringTools;

/**
 * A serializer implementation for plain strings
 * <p>
 * 
 * @see StringSerializationFactory
 */
public class StringSerializer implements ISerializer {

	final private OutputStream os;

	private String charset = "UTF-8";

	public StringSerializer(OutputStream os) {
		this.os = os;
	}

	public StringSerializer(OutputStream os, String charset) {
		this.os = os;
		this.charset = charset;
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public void serialize(Object object) throws IOException {
		os.write(StringTools.safeString(object).getBytes(getCharset()));
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
