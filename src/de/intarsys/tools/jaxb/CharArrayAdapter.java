package de.intarsys.tools.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Provide char[]
 * 
 */
public class CharArrayAdapter extends XmlAdapter<String, char[]> {

	@Override
	public String marshal(char[] v) throws Exception {
		return new String(v);
	}

	@Override
	public char[] unmarshal(String v) throws Exception {
		return v.toCharArray();
	}

}
