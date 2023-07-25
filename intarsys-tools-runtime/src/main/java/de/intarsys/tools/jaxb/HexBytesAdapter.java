package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.intarsys.tools.hex.HexTools;

/**
 * Convert a hex string notation to a char[]
 * 
 */
public class HexBytesAdapter extends XmlAdapter<String, byte[]> {

	@Override
	public String marshal(byte[] v) throws Exception {
		return HexTools.bytesToHexString(v);
	}

	@Override
	public byte[] unmarshal(String v) throws Exception {
		return HexTools.hexStringToBytes(v);
	}

}
