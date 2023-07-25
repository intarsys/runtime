package de.intarsys.tools.encoding;

import java.io.ByteArrayOutputStream;

/* (PD) 2001 The Bitzi Corporation
 * Please see http://bitzi.com/publicdomain for more info.
 *
 * Base32.java
 *
 */

/**
 * Base32 - encodes and decodes 'Canonical' Base32
 * 
 * @author Robert Kaye &amp; Gordon Mohr
 */
public final class Base32 {

	private static final String BASE32CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

	private static final int[] BASE32LOOKUP = { 0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, // '0',
																								// '1',
																								// '2',
																								// '3',
																								// '4',
																								// '5',
																								// '6',
																								// '7'
			0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // '8', '9', ':',
			// ';', '<', '=',
			// '>', '?'
			0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '@', 'A', 'B',
			// 'C', 'D', 'E',
			// 'F', 'G'
			0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'H', 'I', 'J',
			// 'K', 'L', 'M',
			// 'N', 'O'
			0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'P', 'Q', 'R',
			// 'S', 'T', 'U',
			// 'V', 'W'
			0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // 'X', 'Y', 'Z',
			// '[', '\', ']',
			// '^', '_'
			0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '`', 'a', 'b',
			// 'c', 'd', 'e',
			// 'f', 'g'
			0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'h', 'i', 'j',
			// 'k', 'l', 'm',
			// 'n', 'o'
			0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'p', 'q', 'r',
			// 's', 't', 'u',
			// 'v', 'w'
			0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF // 'x', 'y', 'z',
			// '{', '|', '}',
			// '~', 'DEL'
	};

	public static byte[] decode(String value) {
		int lookup;
		int digit;
		char[] chars = value.toCharArray();
		int current = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(chars.length * 5 / 8);
		int i = 0;
		int index = 0;
		for (; i < chars.length; i++) {
			lookup = chars[i] - '0';

			/* Skip chars outside the lookup table */
			if (lookup < 0 || lookup >= BASE32LOOKUP.length)
				continue;

			digit = BASE32LOOKUP[lookup];

			/* If this digit is not in the table, ignore it */
			if (digit == 0xFF)
				continue;

			// fits in remaining byte?
			if (index <= 3) {
				index = (index + 5) % 8;
				if (index == 0) {
					current |= digit;
					bos.write(current);
					current = 0;
				} else
					current |= digit << (8 - index);
			} else {
				index = (index + 5) % 8;
				current |= (digit >>> index);
				bos.write(current);
				current = digit << (8 - index);
			}
		}
		return bos.toByteArray();
	}

	public static String encode(final byte[] bytes) {
		return encode(bytes, 0, bytes.length);
	}

	public static String encode(final byte[] bytes, int offset, int len) {
		int index = 0;
		int digit = 0;
		int currByte;
		int nextByte;

		// begin fix
		// added by jonelo@jonelo.de, Feb 13, 2005
		// according to RFC 3548, the encoding must also contain paddings in
		// some cases
		int add = 0;
		switch (len) {
		case 1:
			add = 6;
			break;
		case 2:
			add = 4;
			break;
		case 3:
			add = 3;
			break;
		case 4:
			add = 1;
			break;
		default:
			add = 0;
		}
		// end fix

		StringBuilder base32 = new StringBuilder(((len + 7) * 8 / 5) + add);
		int i = offset;
		int stop = offset + len;
		while (i < stop) {
			currByte = (bytes[i] >= 0) ? bytes[i] : (bytes[i] + 256); // unsign

			/* Is the current digit going to span a byte boundary? */
			if (index > 3) {
				if ((i + 1) < stop)
					nextByte = (bytes[i + 1] >= 0) ? bytes[i + 1] : (bytes[i + 1] + 256);
				else
					nextByte = 0;

				digit = currByte & (0xFF >> index);
				index = (index + 5) % 8;
				digit <<= index;
				digit |= nextByte >> (8 - index);
				i++;
			} else {
				digit = (currByte >> (8 - (index + 5))) & 0x1F;
				index = (index + 5) % 8;
				if (index == 0)
					i++;
			}
			base32.append(BASE32CHARS.charAt(digit));
		}

		// begin fix
		// added by jonelo@jonelo.de, Feb 13, 2005
		// according to RFC 3548, the encoding must also contain paddings in
		// some cases
		switch (len) {
		case 1:
			base32.append("======");
			break;
		case 2:
			base32.append("====");
			break;
		case 3:
			base32.append("===");
			break;
		case 4:
			base32.append("=");
			break;
		default:
			// do nothing
		}
		// end fix

		return base32.toString();
	}

	private Base32() {
	}

}
