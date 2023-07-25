/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.encoding;

import java.io.IOException;

import de.intarsys.tools.string.StringTools;

/**
 * A tool class for the BASE 64 code.
 * 
 */
public final class Base64 {

	private static final byte PAD = (byte) '=';

	private static final char[] BASE64CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' };

	private static final int[] BASE64POSITIONS = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62,
			-1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7,
			8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28,
			29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	/**
	 * Decodes Base64 data into octets
	 * 
	 * @param bytes
	 *            Byte array containing Base64 data
	 * 
	 * @return byte array containing decoded data.
	 * @throws IOException
	 */
	public static byte[] decode(byte[] bytes) throws IOException {
		return decode(bytes, 0, bytes.length);
	}

	/**
	 * Decode from BASE64 encoded data. If bytes is null, the result is null. If
	 * bytes contain invalid BASE64 chars, an exception is thrown. Whitespace
	 * characters are considered legal in an BASE64 string. If bytes do not
	 * conform to BASE64 alignments, an exception is thrown. Characters after
	 * the padding character are ignored.
	 * 
	 * @param bytes
	 * @param offset
	 * @param len
	 * @return
	 */
	@SuppressWarnings("java:S1168")
	public static byte[] decode(byte[] bytes, int offset, int len) throws IOException {
		try {
			if (bytes == null) {
				return null;
			}
			int newSize = 0;
			int padSize = 0;
			for (int i = offset; i < len; i++) {
				byte octet = bytes[i];
				if (((octet == 0x20) || (octet == 0xd) || (octet == 0xa) || (octet == 0x9))) {
					continue;
				}
				if ((BASE64POSITIONS[octet] != -1)) {
					if (padSize > 0) {
						throw new IOException("Base64 invalid input data");
					}
					if (newSize + offset != i) {
						bytes[newSize + offset] = octet;
					}
					newSize++;
					continue;
				}
				if ((octet == PAD)) {
					padSize++;
					continue;
				}
				if (padSize > 0) {
					// ignore error in case there is data after PAD
					break;
				}
				// not a BASE64 character
				throw new IOException("Base64 invalid input data");
			}
			if (newSize == 0) {
				return new byte[0];
			}
			if (((newSize) % 4) == 1) {
				// invalid size
				throw new IOException("Base64 invalid input data");
			}
			if (((newSize + padSize) % 4) != 0) {
				// invalid size
				throw new IOException("Base64 invalid input data");
			}
			byte[] result = new byte[((newSize - 1) / 4 * 3) + ((newSize - 1) % 4)];
			int threeI = 0;
			int fourI = 0;
			for (; threeI < result.length; threeI += 3, fourI += 4) {
				int bits;
				boolean padded3;
				boolean padded4;

				padded3 = padded4 = false;
				bits = (BASE64POSITIONS[bytes[fourI]] << 18) + (BASE64POSITIONS[bytes[fourI + 1]] << 12);
				if (newSize > (fourI + 2)) {
					bits += (BASE64POSITIONS[bytes[fourI + 2]] << 6);
					if (newSize > (fourI + 3)) {
						bits += BASE64POSITIONS[bytes[fourI + 3]];
					} else {
						padded4 = true;
					}
				} else {
					// 3rd and 4th result bytes are padded
					padded3 = padded4 = true;
				}
				result[threeI] = (byte) (bits >> 16);
				if (!padded3) {
					result[threeI + 1] = (byte) ((bits >> 8) & 0xff);
				}
				if (!padded4) {
					result[threeI + 2] = (byte) (bits & 0xff);
				}
			}
			return result;
		} catch (Exception e) {
			throw new IOException("Base64 invalid byte sequence", e);
		}
	}

	/**
	 * Decodes Base64 data into octets
	 * 
	 * @param inputString
	 *            String containing Base64 data
	 * 
	 * @return byte array containing decoded data.
	 * @throws IOException
	 */
	@SuppressWarnings("java:S1168")
	public static byte[] decode(String inputString) throws IOException {
		if (inputString == null) {
			return null;
		}
		return decode(StringTools.toByteArray(inputString));
	}

	/**
	 * Encodes hex octets into Base64
	 * 
	 * @param bytes
	 *            Array containing binaryData
	 * 
	 * @return Encoded Base64 array
	 */
	public static byte[] encode(byte[] bytes) {
		return encode(bytes, 0, bytes.length);
	}

	@SuppressWarnings("java:S1168")
	public static byte[] encode(byte[] bytes, int offset, int length) {
		if (bytes == null) {
			return null;
		}
		byte[] result;

		result = new byte[(length + 2) / 3 * 4];
		int iTimesFour = 0;
		int iTimesThree = offset;
		while (iTimesFour < result.length) {
			boolean pad3 = false;
			boolean pad4 = false;
			int bits = (bytes[iTimesThree] << 16) & 0xff0000;
			if (offset + length > ((iTimesThree) + 1)) {
				bits |= ((bytes[(iTimesThree) + 1] << 8) & 0xff00);
				if (offset + length > ((iTimesThree) + 2)) {
					bits |= (bytes[(iTimesThree) + 2] & 0xff);
				} else {
					pad4 = true;
				}
			} else {
				// have to pad 3rd and 4th result byte
				pad3 = pad4 = true;
			}
			result[iTimesFour] = (byte) BASE64CHARS[(bits >> 18) & 0x3f];
			result[(iTimesFour) + 1] = (byte) BASE64CHARS[(bits >> 12) & 0x3f];
			result[(iTimesFour) + 2] = (byte) (pad3 ? PAD : BASE64CHARS[(bits >> 6) & 0x3f]);
			result[(iTimesFour) + 3] = (byte) (pad4 ? PAD : BASE64CHARS[(bits) & 0x3f]);
			iTimesFour += 4;
			iTimesThree += 3;
		}
		return result;
	}

	public static boolean isBase64(byte octect) {
		return (((octect == 0x20) || (octect == 0xd) || (octect == 0xa) || (octect == 0x9)) || (octect == PAD)
				|| (BASE64POSITIONS[octect] != -1));
	}

	private Base64() {
	}
}
