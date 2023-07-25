package de.intarsys.tools.jaxb;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.intarsys.tools.hex.HexTools;

/**
 * Convert a hex string notation to a String (UTF-8)
 * 
 */
public class HexStringAdapter extends XmlAdapter<String, String> {

	@Override
	public String marshal(String v) throws Exception {
		return HexTools.bytesToHexString(v.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String unmarshal(String v) throws Exception {
		return new String(HexTools.hexStringToBytes(v), StandardCharsets.UTF_8);
	}

}
