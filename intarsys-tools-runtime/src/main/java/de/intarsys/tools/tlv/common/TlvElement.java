package de.intarsys.tools.tlv.common;

import de.intarsys.tools.hex.HexTools;

/**
 * A TLV element is a "tag, length, value" data structure. First, an identifier
 * is expected, followed by a length, followed by the value itself.
 * <p>
 * This abstract implementation does not imply anything about the encoding of
 * the identifier, length or value field. This is deferred to a concrete
 * implementation, e.g. a ASN.1 DER implementation.
 * <p>
 * The TLV element is parsed from an associated {@link TlvInputStream}.
 * 
 */
public abstract class TlvElement {

	protected final int identifier;

	protected final byte[] buffer;

	protected final int offset;

	protected final int length;

	protected TlvElement(int identifier, byte[] buffer) {
		this(identifier, buffer, 0, buffer.length);
	}

	protected TlvElement(int identifier, byte[] buffer, int offset, int length) {
		assert (buffer != null);
		assert (buffer.length >= offset + length);
		this.identifier = identifier;
		this.buffer = buffer;
		this.offset = offset;
		this.length = length;
	}

	public abstract TlvInputStream createTlvInputStream(byte[] pBytes, int pOffset, int pLength);

	/**
	 * The whole, encoded TLV element.
	 * 
	 * @return The whole, encoded TLV element.
	 */
	public abstract byte[] getEncoded();

	/**
	 * The identifier for this TLV element.
	 * <p>
	 * An identifier may be made up of different components, see ASN.1.
	 * 
	 * @return The identifier for this TLV element.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * The length of the TLV elements value
	 * 
	 * @return The length of the TLV elements value
	 */
	public int getLength() {
		return length;
	}

	/**
	 * A composite {@link TlvElement} contains other {@link TlvElement}
	 * instances in form of a {@link TlvTemplate}.
	 * 
	 * @return The contained {@link TlvTemplate} if available.
	 * @throws TlvFormatException
	 */
	public TlvTemplate getTemplate() throws TlvFormatException {
		return new TlvTemplate(createTlvInputStream(buffer, offset, length));
	}

	/**
	 * The value part within the TLV element.
	 * 
	 * @return The value part within the TLV element.
	 */
	public byte[] getValue() {
		byte[] value = new byte[length];
		System.arraycopy(buffer, offset, value, 0, length);
		return value;
	}

	/**
	 * A byte at index from the value part within the TLV element.
	 * 
	 * @return A byte at index from the value part within the TLV element.
	 */
	public byte getValueAt(int index) {
		return buffer[offset + index];
	}

	/**
	 * <code>true</code> if this element contains TLV objects itself.
	 * 
	 * @return <code>true</code> if this element contains TLV objects itself.
	 */
	public abstract boolean isComposite();

	@Override
	public String toString() {
		return "0x" + Integer.toHexString(getIdentifier()) + ":"
				+ HexTools.bytesToHexString(buffer, offset, length, true);
	}
}
