package de.intarsys.tools.hex;

import junit.framework.TestCase;

public class TestHexTools extends TestCase {

	public void testByteFromString() {
		String in;
		byte[] out;
		//
		in = "";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out != null);
		assertTrue(out.length == 0);
		//
		in = "yx";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out != null);
		assertTrue(out.length == 0);
		//
		in = "0";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 0);
		//
		in = "1";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 1);
		//
		in = "a";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 10);
		//
		in = "A";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 10);
		//
		in = "00";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 0);
		//
		in = "01";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 1);
		//
		in = "0a";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 10);
		//
		in = "0A";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 10);
		//
		in = "11";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == 17);
		//
		in = "aa";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 170);
		//
		in = "FF";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 255);
		//
		in = "1 1";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 17);
		//
		in = "11 11 11";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 17);
		assertTrue(out[1] == (byte) 17);
		assertTrue(out[2] == (byte) 17);
		//
		in = "111111";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 17);
		assertTrue(out[1] == (byte) 17);
		assertTrue(out[2] == (byte) 17);
		//
		in = "ffffff";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 255);
		assertTrue(out[1] == (byte) 255);
		assertTrue(out[2] == (byte) 255);
		//
		in = "1111111";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 17);
		assertTrue(out[1] == (byte) 17);
		assertTrue(out[2] == (byte) 17);
		assertTrue(out[3] == (byte) 1);
		//
		in = "1111111   ";
		out = HexTools.hexStringToBytes(in);
		assertTrue(out[0] == (byte) 17);
		assertTrue(out[1] == (byte) 17);
		assertTrue(out[2] == (byte) 17);
		assertTrue(out[3] == (byte) 1);
	}

	public void testToInt() {
		String in;
		int value;
		//
		in = "";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 0);
		//
		in = "00";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 0);
		//
		in = "01";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 1);
		//
		in = "80";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 128);
		//
		in = "FF";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 255);
		//
		in = "FFFF";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == 256 * 256 - 1);
		//
		in = "FFFFFFFF";
		value = HexTools.hexStringToInt(in);
		assertTrue(value == -1);
	}
}
