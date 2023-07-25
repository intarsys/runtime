package de.intarsys.tools.tlv.common;

/**
 * A model object represented in TLV format.
 * 
 */
public abstract class TlvElementBasedObject extends TlvBasedObject {

	private final TlvElement element;

	protected TlvElementBasedObject(TlvElement element) {
		this.element = element;
	}

	public byte getByteAt(int index) {
		return getElement().getValueAt(index);
	}

	public byte[] getBytes() {
		return getElement().getValue();
	}

	public TlvElement getElement() {
		return element;
	}

	public byte[] getEncoded() {
		return getElement().getEncoded();
	}

	public byte getFirstByte() {
		return getElement().getValueAt(0);
	}

	public int getLength() {
		return getElement().getLength();
	}

	@Override
	protected void toStringPrimitive(StringBuilder sb, int level) {
		sb.append(getTagName());
		sb.append(" "); //$NON-NLS-1$
		toStringValue(sb, level, getBytes());
	}
}
