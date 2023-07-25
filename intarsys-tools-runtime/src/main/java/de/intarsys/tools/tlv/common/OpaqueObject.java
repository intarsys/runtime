package de.intarsys.tools.tlv.common;

public class OpaqueObject extends TlvElementBasedObject {

	public OpaqueObject(TlvElement element) {
		super(element);
	}

	@Override
	protected String getTagName() {
		return "{0x" + Integer.toHexString(getElement().getIdentifier()) + "}";
	}

}
