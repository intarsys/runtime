package de.intarsys.tools.tlv.common;

import de.intarsys.tools.hex.HexTools;

/**
 * A TLV element is a "tag, length, value" data structure. First, an identifier
 * is expected, followed by a length, followed by the data itself.
 * <p>
 * This abstract implementation does not imply anything about the encoding of
 * the identifier, length or value field. This is deferred to a concrete
 * implementation, e.g. a ASN.1 DER implementation.
 * <p>
 * The TLV element is parsed from an associated {@link TlvInputStream}.
 * 
 */
abstract public class TlvElement {

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

	abstract public TlvInputStream createTlvInputStream(byte[] pBytes,
			int pOffset, int pLength);

	/**
	 * The data part within the TLV element.
	 * 
	 * @return The data part within the TLV element.
	 */
	public byte[] getContent() {
		byte[] data = new byte[length];
		System.arraycopy(buffer, offset, data, 0, length);
		return data;
	}

	/**
	 * The whole, encoded TLV element.
	 * 
	 * @return The whole, encoded TLV element.
	 */
	abstract public byte[] getEncoded();

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
	 * The length of the TLV elements data
	 * 
	 * @return The length of the TLV elements data
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
	 * <code>true</code> if this element contains TLV data itself.
	 * 
	 * @return <code>true</code> if this element contains TLV data itself.
	 */
	abstract public boolean isComposite();

	@Override
	public String toString() {
		return "0x" + Integer.toHexString(getIdentifier()) + ":"
				+ HexTools.bytesToHexString(buffer, offset, length, true);
	}
}
