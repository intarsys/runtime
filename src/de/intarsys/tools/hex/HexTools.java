/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.hex;

import java.util.Arrays;

/**
 * Helper class for faster mapping of bytes to their hex equivalent
 */
public class HexTools {
	/** ASCII byte values for the hex strings. */
	public static final byte[][] ByteToHex = { "00".getBytes(),
			"01".getBytes(), "02".getBytes(), "03".getBytes(), "04".getBytes(),
			"05".getBytes(), "06".getBytes(), "07".getBytes(), "08".getBytes(),
			"09".getBytes(), "0A".getBytes(), "0B".getBytes(), "0C".getBytes(),
			"0D".getBytes(), "0E".getBytes(), "0F".getBytes(), "10".getBytes(),
			"11".getBytes(), "12".getBytes(), "13".getBytes(), "14".getBytes(),
			"15".getBytes(), "16".getBytes(), "17".getBytes(), "18".getBytes(),
			"19".getBytes(), "1A".getBytes(), "1B".getBytes(), "1C".getBytes(),
			"1D".getBytes(), "1E".getBytes(), "1F".getBytes(), "20".getBytes(),
			"21".getBytes(), "22".getBytes(), "23".getBytes(), "24".getBytes(),
			"25".getBytes(), "26".getBytes(), "27".getBytes(), "28".getBytes(),
			"29".getBytes(), "2A".getBytes(), "2B".getBytes(), "2C".getBytes(),
			"2D".getBytes(), "2E".getBytes(), "2F".getBytes(), "30".getBytes(),
			"31".getBytes(), "32".getBytes(), "33".getBytes(), "34".getBytes(),
			"35".getBytes(), "36".getBytes(), "37".getBytes(), "38".getBytes(),
			"39".getBytes(), "3A".getBytes(), "3B".getBytes(), "3C".getBytes(),
			"3D".getBytes(), "3E".getBytes(), "3F".getBytes(), "40".getBytes(),
			"41".getBytes(), "42".getBytes(), "43".getBytes(), "44".getBytes(),
			"45".getBytes(), "46".getBytes(), "47".getBytes(), "48".getBytes(),
			"49".getBytes(), "4A".getBytes(), "4B".getBytes(), "4C".getBytes(),
			"4D".getBytes(), "4E".getBytes(), "4F".getBytes(), "50".getBytes(),
			"51".getBytes(), "52".getBytes(), "53".getBytes(), "54".getBytes(),
			"55".getBytes(), "56".getBytes(), "57".getBytes(), "58".getBytes(),
			"59".getBytes(), "5A".getBytes(), "5B".getBytes(), "5C".getBytes(),
			"5D".getBytes(), "5E".getBytes(), "5F".getBytes(), "60".getBytes(),
			"61".getBytes(), "62".getBytes(), "63".getBytes(), "64".getBytes(),
			"65".getBytes(), "66".getBytes(), "67".getBytes(), "68".getBytes(),
			"69".getBytes(), "6A".getBytes(), "6B".getBytes(), "6C".getBytes(),
			"6D".getBytes(), "6E".getBytes(), "6F".getBytes(), "70".getBytes(),
			"71".getBytes(), "72".getBytes(), "73".getBytes(), "74".getBytes(),
			"75".getBytes(), "76".getBytes(), "77".getBytes(), "78".getBytes(),
			"79".getBytes(), "7A".getBytes(), "7B".getBytes(), "7C".getBytes(),
			"7D".getBytes(), "7E".getBytes(), "7F".getBytes(), "80".getBytes(),
			"81".getBytes(), "82".getBytes(), "83".getBytes(), "84".getBytes(),
			"85".getBytes(), "86".getBytes(), "87".getBytes(), "88".getBytes(),
			"89".getBytes(), "8A".getBytes(), "8B".getBytes(), "8C".getBytes(),
			"8D".getBytes(), "8E".getBytes(), "8F".getBytes(), "90".getBytes(),
			"91".getBytes(), "92".getBytes(), "93".getBytes(), "94".getBytes(),
			"95".getBytes(), "96".getBytes(), "97".getBytes(), "98".getBytes(),
			"99".getBytes(), "9A".getBytes(), "9B".getBytes(), "9C".getBytes(),
			"9D".getBytes(), "9E".getBytes(), "9F".getBytes(), "A0".getBytes(),
			"A1".getBytes(), "A2".getBytes(), "A3".getBytes(), "A4".getBytes(),
			"A5".getBytes(), "A6".getBytes(), "A7".getBytes(), "A8".getBytes(),
			"A9".getBytes(), "AA".getBytes(), "AB".getBytes(), "AC".getBytes(),
			"AD".getBytes(), "AE".getBytes(), "AF".getBytes(), "B0".getBytes(),
			"B1".getBytes(), "B2".getBytes(), "B3".getBytes(), "B4".getBytes(),
			"B5".getBytes(), "B6".getBytes(), "B7".getBytes(), "B8".getBytes(),
			"B9".getBytes(), "BA".getBytes(), "BB".getBytes(), "BC".getBytes(),
			"BD".getBytes(), "BE".getBytes(), "BF".getBytes(), "C0".getBytes(),
			"C1".getBytes(), "C2".getBytes(), "C3".getBytes(), "C4".getBytes(),
			"C5".getBytes(), "C6".getBytes(), "C7".getBytes(), "C8".getBytes(),
			"C9".getBytes(), "CA".getBytes(), "CB".getBytes(), "CC".getBytes(),
			"CD".getBytes(), "CE".getBytes(), "CF".getBytes(), "D0".getBytes(),
			"D1".getBytes(), "D2".getBytes(), "D3".getBytes(), "D4".getBytes(),
			"D5".getBytes(), "D6".getBytes(), "D7".getBytes(), "D8".getBytes(),
			"D9".getBytes(), "DA".getBytes(), "DB".getBytes(), "DC".getBytes(),
			"DD".getBytes(), "DE".getBytes(), "DF".getBytes(), "E0".getBytes(),
			"E1".getBytes(), "E2".getBytes(), "E3".getBytes(), "E4".getBytes(),
			"E5".getBytes(), "E6".getBytes(), "E7".getBytes(), "E8".getBytes(),
			"E9".getBytes(), "EA".getBytes(), "EB".getBytes(), "EC".getBytes(),
			"ED".getBytes(), "EE".getBytes(), "EF".getBytes(), "F0".getBytes(),
			"F1".getBytes(), "F2".getBytes(), "F3".getBytes(), "F4".getBytes(),
			"F5".getBytes(), "F6".getBytes(), "F7".getBytes(), "F8".getBytes(),
			"F9".getBytes(), "FA".getBytes(), "FB".getBytes(), "FC".getBytes(),
			"FD".getBytes(), "FE".getBytes(), "FF".getBytes(), };

	/**
	 * ASCII byte values for the hex strings.
	 */
	public static final byte[][] byteToHexLower = { "00".getBytes(),
			"01".getBytes(), "02".getBytes(), "03".getBytes(), "04".getBytes(),
			"05".getBytes(), "06".getBytes(), "07".getBytes(), "08".getBytes(),
			"09".getBytes(), "0a".getBytes(), "0b".getBytes(), "0c".getBytes(),
			"0d".getBytes(), "0e".getBytes(), "0f".getBytes(), "10".getBytes(),
			"11".getBytes(), "12".getBytes(), "13".getBytes(), "14".getBytes(),
			"15".getBytes(), "16".getBytes(), "17".getBytes(), "18".getBytes(),
			"19".getBytes(), "1a".getBytes(), "1b".getBytes(), "1c".getBytes(),
			"1d".getBytes(), "1e".getBytes(), "1f".getBytes(), "20".getBytes(),
			"21".getBytes(), "22".getBytes(), "23".getBytes(), "24".getBytes(),
			"25".getBytes(), "26".getBytes(), "27".getBytes(), "28".getBytes(),
			"29".getBytes(), "2a".getBytes(), "2b".getBytes(), "2c".getBytes(),
			"2d".getBytes(), "2e".getBytes(), "2f".getBytes(), "30".getBytes(),
			"31".getBytes(), "32".getBytes(), "33".getBytes(), "34".getBytes(),
			"35".getBytes(), "36".getBytes(), "37".getBytes(), "38".getBytes(),
			"39".getBytes(), "3a".getBytes(), "3b".getBytes(), "3c".getBytes(),
			"3d".getBytes(), "3e".getBytes(), "3f".getBytes(), "40".getBytes(),
			"41".getBytes(), "42".getBytes(), "43".getBytes(), "44".getBytes(),
			"45".getBytes(), "46".getBytes(), "47".getBytes(), "48".getBytes(),
			"49".getBytes(), "4a".getBytes(), "4b".getBytes(), "4c".getBytes(),
			"4d".getBytes(), "4e".getBytes(), "4f".getBytes(), "50".getBytes(),
			"51".getBytes(), "52".getBytes(), "53".getBytes(), "54".getBytes(),
			"55".getBytes(), "56".getBytes(), "57".getBytes(), "58".getBytes(),
			"59".getBytes(), "5a".getBytes(), "5b".getBytes(), "5c".getBytes(),
			"5d".getBytes(), "5e".getBytes(), "5f".getBytes(), "60".getBytes(),
			"61".getBytes(), "62".getBytes(), "63".getBytes(), "64".getBytes(),
			"65".getBytes(), "66".getBytes(), "67".getBytes(), "68".getBytes(),
			"69".getBytes(), "6a".getBytes(), "6b".getBytes(), "6c".getBytes(),
			"6d".getBytes(), "6e".getBytes(), "6f".getBytes(), "70".getBytes(),
			"71".getBytes(), "72".getBytes(), "73".getBytes(), "74".getBytes(),
			"75".getBytes(), "76".getBytes(), "77".getBytes(), "78".getBytes(),
			"79".getBytes(), "7a".getBytes(), "7b".getBytes(), "7c".getBytes(),
			"7d".getBytes(), "7e".getBytes(), "7f".getBytes(), "80".getBytes(),
			"81".getBytes(), "82".getBytes(), "83".getBytes(), "84".getBytes(),
			"85".getBytes(), "86".getBytes(), "87".getBytes(), "88".getBytes(),
			"89".getBytes(), "8a".getBytes(), "8b".getBytes(), "8c".getBytes(),
			"8d".getBytes(), "8e".getBytes(), "8f".getBytes(), "90".getBytes(),
			"91".getBytes(), "92".getBytes(), "93".getBytes(), "94".getBytes(),
			"95".getBytes(), "96".getBytes(), "97".getBytes(), "98".getBytes(),
			"99".getBytes(), "9a".getBytes(), "9b".getBytes(), "9c".getBytes(),
			"9d".getBytes(), "9e".getBytes(), "9f".getBytes(), "a0".getBytes(),
			"a1".getBytes(), "a2".getBytes(), "a3".getBytes(), "a4".getBytes(),
			"a5".getBytes(), "a6".getBytes(), "a7".getBytes(), "a8".getBytes(),
			"a9".getBytes(), "aa".getBytes(), "ab".getBytes(), "ac".getBytes(),
			"ad".getBytes(), "ae".getBytes(), "af".getBytes(), "b0".getBytes(),
			"b1".getBytes(), "b2".getBytes(), "b3".getBytes(), "b4".getBytes(),
			"b5".getBytes(), "b6".getBytes(), "b7".getBytes(), "b8".getBytes(),
			"b9".getBytes(), "ba".getBytes(), "bb".getBytes(), "bc".getBytes(),
			"bd".getBytes(), "be".getBytes(), "bf".getBytes(), "c0".getBytes(),
			"c1".getBytes(), "c2".getBytes(), "c3".getBytes(), "c4".getBytes(),
			"c5".getBytes(), "c6".getBytes(), "c7".getBytes(), "c8".getBytes(),
			"c9".getBytes(), "ca".getBytes(), "cb".getBytes(), "cc".getBytes(),
			"cd".getBytes(), "ce".getBytes(), "cf".getBytes(), "d0".getBytes(),
			"d1".getBytes(), "d2".getBytes(), "d3".getBytes(), "d4".getBytes(),
			"d5".getBytes(), "d6".getBytes(), "d7".getBytes(), "d8".getBytes(),
			"d9".getBytes(), "da".getBytes(), "db".getBytes(), "dc".getBytes(),
			"dd".getBytes(), "de".getBytes(), "df".getBytes(), "e0".getBytes(),
			"e1".getBytes(), "e2".getBytes(), "e3".getBytes(), "e4".getBytes(),
			"e5".getBytes(), "e6".getBytes(), "e7".getBytes(), "e8".getBytes(),
			"e9".getBytes(), "ea".getBytes(), "eb".getBytes(), "ec".getBytes(),
			"ed".getBytes(), "ee".getBytes(), "ef".getBytes(), "f0".getBytes(),
			"f1".getBytes(), "f2".getBytes(), "f3".getBytes(), "f4".getBytes(),
			"f5".getBytes(), "f6".getBytes(), "f7".getBytes(), "f8".getBytes(),
			"f9".getBytes(), "fa".getBytes(), "fb".getBytes(), "fc".getBytes(),
			"fd".getBytes(), "fe".getBytes(), "ff".getBytes() };

	public static String bytesToHexDump(byte[] data, int offset, int length,
			int groupSize, int groups, boolean space) {
		length = data.length - offset < length ? data.length - offset : length;
		StringBuilder buffer = new StringBuilder();
		int byteCounter = 0;
		int groupCounter = 0;
		for (int i = 0; i < length; i++) {
			byte[] bytes = HexTools.ByteToHex[data[offset + i] & 0xFF];
			buffer.append((char) bytes[0]);
			buffer.append((char) bytes[1]);
			if (space) {
				buffer.append(" ");
			}
			byteCounter++;
			if (byteCounter >= groupSize) {
				byteCounter = 0;
				groupCounter++;
				buffer.append("  ");
			}
			if (groupCounter >= groups) {
				buffer.append("\n");
				groupCounter = 0;
			}
		}
		return buffer.toString();
	}

	public static String bytesToHexString(byte[] data) {
		return bytesToHexString(data, 0, data.length);
	}

	public static String bytesToHexString(byte[] data, int offset, int length) {
		return bytesToHexString(data, offset, length, false);
	}

	public static String bytesToHexString(byte[] data, int offset, int length,
			boolean space) {
		length = data.length - offset < length ? data.length - offset : length;
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			byte[] bytes = HexTools.ByteToHex[data[offset + i] & 0xFF];
			buffer.append((char) bytes[0]);
			buffer.append((char) bytes[1]);
			if (space) {
				buffer.append(" ");
			}
		}
		return buffer.toString();
	}

	/**
	 * The numeric value for the hex digit, return -1 if not valid digit
	 * 
	 * @param c
	 *            A char representing a hex digit.
	 * 
	 * @return The numeric value of the hex digit
	 */
	public static int hexDigitToInt(char c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return c - '0';
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
			return (10 + c) - 'a';
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
			return (10 + c) - 'A';
		default:
			return -1;
		}
	}

	public static byte[] hexStringToBytes(String hexString) {
		byte[] data = new byte[hexString.length() + 1 / 2];
		int index = 0;
		char[] chars = hexString.toCharArray();
		int i = 0;
		int c;
		while (i < chars.length) {
			int b = 0;
			c = -1;
			while (i < chars.length && (c = hexDigitToInt(chars[i++])) == -1) {
			}
			if (c != -1) {
				b = c;
				c = -1;
				while (i < chars.length
						&& (c = hexDigitToInt(chars[i++])) == -1) {
				}
				if (c != -1) {
					b = ((b << 4) + c);
				}
				data[index++] = (byte) (b & 0xFF);
			}
		}
		return Arrays.copyOfRange(data, 0, index);
	}

	public static int hexStringToInt(String hexString) {
		int result = 0;
		for (int i = 0; i < hexString.length(); i++) {
			result = (result << 4) + hexDigitToInt(hexString.charAt(i));
		}
		return result;
	}

	/**
	 * Evaluate to <code>true</code> if <code>i</code> is a valid hex digit
	 * 
	 * @param i
	 *            A char representing a hex digit.
	 * 
	 * @return <code>true</code> if <code>i</code> is a valid hex digit.
	 */
	public static boolean isHexDigit(char i) {
		return ((i >= '0') && (i <= '9')) || ((i >= 'a') && (i <= 'f'))
				|| ((i >= 'A') && (i <= 'F'));
	}
}
