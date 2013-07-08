package de.intarsys.tools.tlv.common;

import de.intarsys.tools.collection.ByteArrayTools;

/**
 * Tool methods for handling the TLV implementation.
 * 
 */
public class TlvTools {

	public static int asInt(TlvElement element) {
		return ByteArrayTools.toBigEndianInt(element.buffer, element.offset,
				element.length);
	}

	public static int asUnsignedInt(TlvElement element) {
		return ByteArrayTools.toBigEndianIntUnsigned(element.buffer,
				element.offset, element.length);
	}

	private TlvTools() {
		//
	}

}
