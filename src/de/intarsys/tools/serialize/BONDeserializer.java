package de.intarsys.tools.serialize;

import java.io.IOException;
import java.io.InputStream;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.stream.FastByteArrayOutputStream;

/**
 * A deserializer implementation for the "BON", basic object notation.
 * <p>
 * 
 * @see BONSerializationFactory
 */
public class BONDeserializer implements IDeserializer {

	final private InputStream is;

	final private FastByteArrayOutputStream os = new FastByteArrayOutputStream();

	private boolean isfloat;

	private String charset = "UTF-8";

	public BONDeserializer(InputStream is) {
		this.is = is;
	}

	public BONDeserializer(InputStream is, String charset) {
		this.is = is;
		this.charset = charset;
	}

	@Override
	public Object deserialize() throws IOException {
		os.reset();
		int i = is.read();
		while (isWhitespace(i)) {
			i = is.read();
		}
		if (i == -1) {
			return null;
		}
		if ((i >= '0' && i <= '9') || i == '+' || i == '.' || i == '-') {
			isfloat = i == '.';
			os.write(i);
			return deserializeNumber();
		}
		if (isLetter(i)) {
			os.write(i);
			String token = deserializeToken();
			if ("null".equals(token)) {
				return null;
			} else if ("true".equals(token)) {
				return true;
			} else if ("false".equals(token)) {
				return false;
			} else {
				throw new IOException("unexpected token '" + token + "'");
			}
		}
		if (i == '"') {
			return deserializeString();
		}
		if (i == '@') {
			return deserializeBase64();
		}
		if (i == '#') {
			return deserializeBinary();
		}
		throw new IOException("unexpected character " + (char) i + " (" + i
				+ ")");
	}

	protected Object deserializeBase64() throws IOException {
		int i = is.read();
		while (i != -1) {
			if (i == '@') {
				break;
			}
			os.write(i);
			i = is.read();
		}
		return Base64.decode(os.toByteArray(), 0, os.size());
	}

	protected Object deserializeBinary() throws IOException {
		int i = is.read();
		while (i != -1) {
			if (i == '#') {
				i = is.read();
				if (i != '#') {
					break;
				}
			}
			os.write(i);
			i = is.read();
		}
		return os.getBytes();
	}

	protected Object deserializeNumber() throws IOException {
		int i = is.read();
		while (i != -1) {
			if (!isNumber(i) && i != '.') {
				break;
			}
			isfloat = isfloat || i == '.';
			os.write(i);
			i = is.read();
		}
		String strValue = new String(os.getBytes(), 0, os.size());
		if (isfloat) {
			return Float.parseFloat(strValue);
		} else {
			try {
				return Integer.parseInt(strValue);
			} catch (NumberFormatException e) {
				return Long.parseLong(strValue);
			}
		}
	}

	protected String deserializeString() throws IOException {
		int i = is.read();
		while (i != -1) {
			if (i == '\\') {
				i = is.read();
				if (i == -1) {
					break;
				}
				os.write(i);
				i = is.read();
				continue;
			}
			if (i == '"') {
				break;
			}
			os.write(i);
			i = is.read();
		}
		return new String(os.getBytes(), 0, os.size(), getCharset());
	}

	protected String deserializeToken() throws IOException {
		int i = is.read();
		while (i != -1) {
			if (!isLetter(i)) {
				break;
			}
			os.write(i);
			i = is.read();
		}
		return new String(os.getBytes(), 0, os.size());
	}

	public String getCharset() {
		return charset;
	}

	protected boolean isLetter(int i) {
		return (i >= 'a' && i <= 'z') || (i >= 'A' && i <= 'Z');
	}

	protected boolean isNumber(int i) {
		return (i >= '0' && i <= '9');
	}

	protected boolean isWhitespace(int i) {
		return i == ' ' || i == '\t' || i == '\n';
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
