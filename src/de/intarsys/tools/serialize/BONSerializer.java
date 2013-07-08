package de.intarsys.tools.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

/**
 * A serializer implementation for the "BON", basic object notation.
 * <p>
 * 
 * @see BONSerializationFactory
 */
public class BONSerializer implements ISerializer {

	final private OutputStream os;

	private String charset = "UTF-8";

	private boolean first = true;

	public BONSerializer(OutputStream os) {
		this.os = os;
	}

	public BONSerializer(OutputStream os, String charset) {
		this.os = os;
		this.charset = charset;
	}

	protected void basicSerialize(Object object) throws IOException {
		if (object == null) {
			os.write(StringTools.toByteArray("null"));
		} else if (object instanceof Number) {
			os.write(StringTools.toByteArray(String.valueOf(object)));
		} else if (object instanceof Boolean) {
			os.write(StringTools.toByteArray(String.valueOf(object)));
		} else if (object instanceof String) {
			serializeString((String) object);
		} else if (object instanceof byte[]) {
			serializeBase64((byte[]) object);
		} else if (object instanceof List) {
			serializeList((List) object);
		} else if (object instanceof Map) {
			serializeMap((Map) object);
		} else {
			throw new IOException("unknown object type " + object.getClass());
		}
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public void serialize(Object object) throws IOException {
		if (!first) {
			os.write(' ');
		}
		first = false;
		basicSerialize(object);
	}

	protected void serializeBase64(byte[] object) throws IOException {
		os.write('@');
		os.write(Base64.encode(object));
		os.write('@');
	}

	protected void serializeList(List object) throws IOException {
		boolean addSeparator = false;
		os.write('[');
		for (Object element : object) {
			if (addSeparator) {
				os.write(',');
			}
			addSeparator = true;
			basicSerialize(element);
		}
		os.write(']');
	}

	protected void serializeMap(Map<Object, Object> object) throws IOException {
		boolean addSeparator = false;
		os.write('{');
		for (Map.Entry entry : object.entrySet()) {
			if (addSeparator) {
				os.write(',');
			}
			addSeparator = true;
			basicSerialize(entry.getKey());
			os.write(':');
			basicSerialize(entry.getValue());
		}
		os.write('}');
	}

	protected void serializeString(String object) throws IOException {
		os.write('"');
		byte[] bytes = object.getBytes(getCharset());
		for (int i = 0; i < bytes.length; i++) {
			int c = bytes[i];
			if (c == '\\') {
				os.write('\\');
			} else if (c == '"') {
				os.write('\\');
			}
			os.write(c);
		}
		os.write('"');
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
