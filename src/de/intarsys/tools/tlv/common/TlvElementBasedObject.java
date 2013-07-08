package de.intarsys.tools.tlv.common;

/**
 * A model object represented in TLV format.
 * 
 */
public abstract class TlvElementBasedObject {

	final private TlvElement element;

	public TlvElementBasedObject(TlvElement element) {
		this.element = element;
	}

	public byte[] asBytes() {
		return getElement().getContent();
	}

	public TlvElement getElement() {
		return element;
	}

	public byte getFirstByte() {
		return getElement().getContent()[0];
	}

}
