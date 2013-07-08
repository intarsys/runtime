package de.intarsys.tools.tlv.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} returning {@link SimpleElement} TLV objects.
 * 
 */
public class SimpleInputStream extends TlvInputStream {

	public SimpleInputStream(byte[] data, int offset, int length) {
		this(new ByteArrayInputStream(data, offset, length));
	}

	public SimpleInputStream(InputStream in) {
		super(in);
	}

	@Override
	public TlvElement readElement() throws IOException {
		int i;
		i = read();
		// 0x00 bytes are ignored
		while (i == 0) {
			i = read();
		}
		if (i == -1) {
			return null;
		}
		int tag = i;
		i = read();
		if (i == -1) {
			throw new IOException("unexpected end of input");
		}
		int length = i;
		if (length == 255) {
			i = read();
			if (i == -1) {
				throw new IOException("unexpected end of input");
			}
			length = i;
			i = read();
			if (i == -1) {
				throw new IOException("unexpected end of input");
			}
			length = (length << 8) + i;
		}
		byte[] data = new byte[length];
		int offset = 0;
		while (length > 0) {
			i = read(data, offset, length);
			if (i == -1) {
				break;
			}
			offset += i;
			length -= i;
		}
		if (length != 0) {
			throw new IOException("unexpected end of input (data missing)");
		}
		return new SimpleElement(tag, data);
	}
}
