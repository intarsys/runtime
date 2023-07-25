package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Convert a string to an integer, supporting decimal, hex, octal and binary
 * formats.
 * 
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {

	@Override
	public String marshal(Integer v) throws Exception {
		return "0x" + Integer.toHexString(v);
	}

	@Override
	public Integer unmarshal(String v) throws Exception {
		if (v.startsWith("0x")) {
			return Integer.parseInt(v.substring(2), 16);
		}
		if (v.startsWith("0o")) {
			return Integer.parseInt(v.substring(2), 8);
		}
		if (v.startsWith("0b")) {
			return Integer.parseInt(v.substring(2), 2);
		}
		return Integer.parseInt(v);
	}

}
