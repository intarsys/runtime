package de.intarsys.tools.tlv.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Implementation of a simple TLV object.
 * <p>
 * Each SIMPLE-TLV data object shall consist of two or three consecutive fields:
 * a mandatory tag field, a mandatory length field and a conditional value
 * field.
 * <p>
 * The tag field consists of a single byte encoding a tag number from 1 to 254.
 * The values '00' and 'FF' are invalid for tag fields.
 * <p>
 * The length field consists of one or three consecutive bytes. - If the first
 * byte is not set to 'FF', then the length field consists of a single byte
 * encoding a number from zero to 254 and denoted N. - If the first byte is set
 * to 'FF', then the length field continues on the subsequent two bytes with any
 * value encoding a number from zero to 65 535 and denoted N.
 * <p>
 * If N is zero, there is no value field, i.e., the data object is empty.
 * Otherwise (N > 0), the value field consists of N consecutive bytes.
 * 
 */
public class SimpleElement extends TlvElement {

	static public SimpleElement parseElement(byte[] encoded, int offset,
			int length) throws IOException {
		SimpleInputStream is = new SimpleInputStream(encoded, offset, length);
		return (SimpleElement) is.readElement();
	}

	static public TlvTemplate parseTemplate(byte[] encoded, int offset,
			int length) throws IOException {
		SimpleInputStream is = new SimpleInputStream(encoded, offset, length);
		return new TlvTemplate(is);
	}

	public SimpleElement(int identifier, byte[] buffer) {
		super(identifier, buffer);
		if (identifier <= 0 || identifier >= 255) {
			throw new IllegalArgumentException("illegal identifier value");
		}
	}

	public SimpleElement(int identifier, byte[] buffer, int offset, int length) {
		super(identifier, buffer, offset, length);
		if (identifier <= 0 || identifier >= 255) {
			throw new IllegalArgumentException("illegal identifier value");
		}
	}

	@Override
	public TlvInputStream createTlvInputStream(byte[] pBytes, int pOffset,
			int pLength) throws TlvFormatException {
		return new SimpleInputStream(pBytes, pOffset, pLength);
	}

	@Override
	public byte[] getEncoded() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(getIdentifier());
		if (getLength() < 255) {
			out.write(getLength());
		} else if (getLength() <= 0xFFFF) {
			out.write(0xff);
			out.write((getLength() >> 8) & 0xFF);
			out.write(getLength() & 0xFF);
		} else {
			throw new IllegalArgumentException("size > 0xFFFF not supported"); //$NON-NLS-1$
		}
		out.write(buffer, offset, length);
		return out.toByteArray();
	}

	@Override
	public boolean isComposite() {
		return false;
	}
}
