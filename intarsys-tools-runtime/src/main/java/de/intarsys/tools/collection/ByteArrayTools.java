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
package de.intarsys.tools.collection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.crypto.CryptoTools;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

public final class ByteArrayTools {

	/**
	 * Concatenate the byte array arguments.
	 * 
	 * @param arrays
	 * @return
	 */
	public static byte[] concat(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] concat = new byte[length];
		int index = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, concat, index, array.length);
			index += array.length;
		}
		return concat;
	}

	/**
	 * Make a copy of <code>bytes</code>.
	 * 
	 * @param bytes
	 *            byte[] to be copied
	 * 
	 * @return A copy of <code>bytes</code>
	 */
	public static byte[] copy(byte[] bytes) {
		byte[] newbuf = new byte[bytes.length];
		System.arraycopy(bytes, 0, newbuf, 0, bytes.length);
		return newbuf;
	}

	/**
	 * Copy <code>length</code> bytes from <code>bytes</code> starting at
	 * <code>from</code>.
	 * 
	 * @param bytes
	 *            byte[] to be copied
	 * @param offset
	 *            starting position to copy from
	 * @param length
	 *            number of bytes
	 * 
	 * @return A copy of <code>bytes</code>
	 */
	public static byte[] copy(byte[] bytes, int offset, int length) {
		byte[] newbuf = new byte[length];
		System.arraycopy(bytes, offset, newbuf, 0, length);
		return newbuf;
	}

	/**
	 * A byte array with the defined length, filled with val.
	 * 
	 * @param length
	 * @param val
	 * @return A byte array with the defined length, filled with val.
	 */
	public static byte[] createBytes(int length, byte val) {
		byte[] bytes = new byte[length];
		Arrays.fill(bytes, val);
		return bytes;
	}

	/**
	 * Basic byte[] conversion.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("java:S1168")
	public static byte[] createBytes(Object value) throws IOException {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return null;
			}
			value = Base64.decode((String) value);
		}
		if (value instanceof ILocator) {
			value = LocatorTools.getBytes((ILocator) value);
		}
		if (value instanceof ILocatorSupport) {
			value = LocatorTools.getBytes(((ILocatorSupport) value).getLocator());
		}
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		try {
			return ConverterRegistry.get().convert(value, byte[].class);
		} catch (ConversionException e) {
			throw new IOException("can't convert " + value + " to byte[]");
		}
	}

	/**
	 * A random byte sequence of length <code>length</code>.
	 * 
	 * @param length
	 * @return A random byte sequence of length <code>length</code>.
	 */
	public static byte[] createRandomBytes(int length) {
		SecureRandom rand = CryptoTools.createSecureRandom();
		byte[] bytes = new byte[length];
		rand.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Checks two arrays or sections thereof for equality. If an array is
	 * <code>null</code> or it's section is shorter than the compared length,
	 * <code>false</code> is returned.
	 * 
	 * @param array1
	 * @param offset1
	 * @param array2
	 * @param offset2
	 * @param length
	 *            must have a value greater than 0. A value of 0 always returns
	 *            <code>false</code>.
	 * @return <code>true</code>, if the compared array sections match
	 */
	public static boolean equals(byte[] array1, int offset1, byte[] array2, int offset2, int length) {
		if (array1 == null || array2 == null) {
			return false;
		}
		if (array1.length - offset1 < length || array2.length - offset2 < length) {
			return false;
		}
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (array1[offset1 + i] != array2[offset2 + i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Search index of pattern in source. Algorithm from java.lang.String
	 * 
	 * @param source
	 * @param sourceOffset
	 * @param sourceLen
	 * @param pattern
	 * @param patternOffset
	 * @param patternLen
	 * @param fromIndex
	 * @return The index of the first occurrence of pattern or -1.
	 */
	public static int indexOf(byte[] source, int sourceOffset, int sourceLen, byte[] pattern, int patternOffset,
			int patternLen, int fromIndex) {
		if (fromIndex >= sourceLen) {
			return (patternLen == 0 ? sourceLen : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (patternLen == 0) {
			return fromIndex;
		}

		byte first = pattern[patternOffset];
		int max = sourceOffset + (sourceLen - patternLen);
		int i = sourceOffset + fromIndex;
		while (i <= max) {
			/* Look for first byte . */
			if (source[i] != first) {
				while (++i <= max && source[i] != first) {
					// empty
				}
			}

			/* Found first byte, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + patternLen - 1;
				for (int k = patternOffset + 1; j < end && source[j] == pattern[k]; j++, k++) {
					// empty
				}

				if (j == end) {
					/* Found whole pattern. */
					return i - sourceOffset;
				}
			}
			i++;
		}
		return -1;
	}

	/**
	 * <code>true</code> if <code>bytes</code> starts with the byte sequence
	 * defined in <code>pattern</code>.
	 * 
	 * @param bytes
	 * @param pattern
	 * @return <code>true</code> if <code>bytes</code> starts with the byte
	 *         sequence defined in <code>pattern</code>.
	 * 
	 */
	public static boolean startsWith(byte[] bytes, byte[] pattern) {
		if (bytes == null) {
			return false;
		}
		if (pattern == null) {
			return true;
		}
		if (pattern.length > bytes.length) {
			return false;
		}
		int length = pattern.length;
		for (int i = 0; i < length; i++) {
			if (bytes[i] != pattern[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Create a signed int from buffer, starting at offset and using size bytes,
	 * most significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static int toBigEndianInt(byte[] buffer, int offset, int size) {
		size = size < 4 ? size : 4;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		int i = offset;
		if (i < offset + size) {
			// signed part
			result = buffer[i++];
		}
		while (i < offset + size) {
			result <<= 8;
			result |= (buffer[i++] & 0xFF);
		}
		return result;
	}

	/**
	 * Create an unsigned int from buffer, starting at offset and using size
	 * bytes, most significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static int toBigEndianIntUnsigned(byte[] buffer, int offset, int size) {
		size = size < 4 ? size : 4;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		for (int i = offset; i < offset + size; i++) {
			result <<= 8;
			result |= (buffer[i] & 0xFF);
		}
		return result;
	}

	/**
	 * Create a signed long from buffer, starting at offset and using size
	 * bytes, most significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static long toBigEndianLong(byte[] buffer, int offset, int size) {
		size = size < 8 ? size : 8;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		int i = offset;
		if (i < offset + size) {
			// signed part
			result = buffer[i++];
		}
		while (i < offset + size) {
			result <<= 8;
			result |= (buffer[i++] & 0xFF);
		}
		return result;
	}

	/**
	 * Create an unsigned long from buffer, starting at offset and using size
	 * bytes, most significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static long toBigEndianLongUnsigned(byte[] buffer, int offset, int size) {
		size = size < 8 ? size : 8;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		for (int i = offset; i < offset + size; i++) {
			result <<= 8;
			result |= (buffer[i] & 0xFF);
		}
		return result;
	}

	/**
	 * Return r as an byte array without leading zero bytes.
	 * 
	 * @param r
	 * @return
	 */
	public static byte[] toBytes(BigInteger r) {
		byte[] tmp = r.toByteArray();
		if (tmp[0] == 0) {
			byte[] ntmp = new byte[tmp.length - 1];
			System.arraycopy(tmp, 1, ntmp, 0, ntmp.length);
			return ntmp;
		}
		return tmp;
	}

	/**
	 * Return r as an byte array.
	 * 
	 * If the size of the resulting byte array is smaller than size, the result
	 * is padded signed (in the least significant bytes). If the byte array is
	 * larger or equal to size it is returned directly.
	 * 
	 * @param r
	 * @param size
	 * @return
	 */
	public static byte[] toBytes(BigInteger r, int size) {
		byte[] tmp = r.toByteArray();
		if (tmp.length == size) {
			return tmp;
		}
		byte[] result = new byte[size];
		int offset = size - tmp.length;
		if (tmp.length > size) {
			System.arraycopy(tmp, -offset, result, 0, size);
			return result;
		}
		byte pad = r.signum() < 0 ? (byte) 0xff : (byte) 0x00;
		for (int i = 0; i < offset; i++) {
			result[i] = pad;
		}
		System.arraycopy(tmp, 0, result, offset, tmp.length);
		return result;
	}

	/**
	 * Create bytes from value, most significant byte first, using fewest
	 * possible bytes
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toBytesBigEndian(int value) {
		List<Byte> byteList = new ArrayList<>();
		while (true) {
			byte byteVal = (byte) (value & 0xFF);
			byteList.add(byteVal);
			value = value >> 8;
			if (value == 0) {
				break;
			}
		}
		int size = byteList.size();
		byte[] result = new byte[size];
		for (int i = size - 1; i >= 0; i--) {
			result[size - 1 - i] = byteList.get(i);
		}
		return result;
	}

	/**
	 * Create bytes from value, most significant byte first, using size bytes
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
	public static byte[] toBytesBigEndian(int value, int size) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			writeBigEndian(os, value, size);
			return os.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create bytes from value, most significant byte first, using size bytes
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
	public static byte[] toBytesBigEndian(long value, int size) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			writeBigEndian(os, value, size);
			return os.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create byte array from value, least significant byte first, using size
	 * bytes
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
	public static byte[] toBytesLittleEndian(int value, int size) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			writeLittleEndian(os, value, size);
			return os.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create byte array from value, least significant byte first, using size
	 * bytes
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
	public static byte[] toBytesLittleEndian(long value, int size) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			writeLittleEndian(os, value, size);
			return os.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create a signed int from buffer, starting at offset and using size bytes,
	 * least significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static int toLittleEndianInt(byte[] buffer, int offset, int size) {
		size = size < 4 ? size : 4;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		int i = offset + size - 1;
		if (i >= offset) {
			// signed part
			result = buffer[i--];
		}
		while (i >= offset) {
			result <<= 8;
			result |= (buffer[i--] & 0xFF);
		}
		return result;
	}

	/**
	 * Create an unsigned int from buffer, starting at offset and using size
	 * bytes, least significant byte first.
	 * <p>
	 * This implementation does not check against integer overflow
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static int toLittleEndianIntUnsigned(byte[] buffer, int offset, int size) {
		size = size < 4 ? size : 4;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		int result = 0;
		for (int i = offset + size - 1; i >= offset; i--) {
			result <<= 8;
			result |= (buffer[i] & 0xFF);
		}
		return result;
	}

	/**
	 * Create a signed long from buffer, starting at offset and using size
	 * bytes, least significant byte first.
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static long toLittleEndianLong(byte[] buffer, int offset, int size) {
		size = size < 8 ? size : 8;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		long result = 0;
		int i = offset + size - 1;
		if (i >= offset) {
			// signed part
			result = buffer[i--];
		}
		while (i >= offset) {
			result <<= 8;
			result |= (buffer[i--] & 0xFF);
		}
		return result;
	}

	/**
	 * Create an unsigned long from buffer, starting at offset and using size
	 * bytes, least significant byte first.
	 * <p>
	 * This implementation does not check against integer overflow
	 * 
	 * @param buffer
	 * @param offset
	 * @param size
	 * @return
	 */
	public static long toLittleEndianLongUnsigned(byte[] buffer, int offset, int size) {
		size = size < 8 ? size : 8;
		size = size <= (buffer.length - offset) ? size : buffer.length - offset;
		long result = 0;
		for (int i = offset + size - 1; i >= offset; i--) {
			result <<= 8;
			result |= (buffer[i] & 0xFF);
		}
		return result;
	}

	/**
	 * Write value into bytes, starting at start, most significant byte first,
	 * using size bytes
	 * 
	 * @param bytes
	 * @param start
	 * @param value
	 * @param size
	 */
	public static void writeBigEndian(byte[] bytes, int start, int value, int size) throws IOException {
		int bitShift = 0;
		for (int i = start + size - 1; i >= start; i--) {
			bytes[i] = (byte) ((value >>> bitShift) & 0xFF);
			bitShift += 8;
		}
	}

	/**
	 * Write value into bytes, starting at offset, most significant byte first,
	 * using size bytes
	 * 
	 * @param random
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeBigEndian(IRandomAccess random, int value, int size) throws IOException {
		for (int i = size - 1; i >= 0; i--) {
			if (i >= 4) {
				random.write(0x00);
			} else {
				random.write((byte) ((value >>> (8 * i)) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, most significant byte first,
	 * using size bytes
	 * 
	 * @param random
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeBigEndian(IRandomAccess random, long value, int size) throws IOException {
		for (int i = size - 1; i >= 0; i--) {
			if (i >= 8) {
				random.write(0x00);
			} else {
				random.write((byte) ((value >>> (8 * i)) & 0xFF));
			}
		}
	}

	/**
	 * Write value into the output stream, most significant byte first, using
	 * size bytes
	 * 
	 * @param os
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeBigEndian(OutputStream os, int value, int size) throws IOException {
		for (int i = size - 1; i >= 0; i--) {
			if (i >= 4) {
				os.write(0x00);
			} else {
				os.write((byte) ((value >>> (8 * i)) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, most significant byte first,
	 * using size bytes
	 * 
	 * @param os
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeBigEndian(OutputStream os, long value, int size) throws IOException {
		for (int i = size - 1; i >= 0; i--) {
			if (i >= 8) {
				os.write(0x00);
			} else {
				os.write((byte) ((value >>> (8 * i)) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, least significant byte first,
	 * using size bytes
	 * 
	 * @param random
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeLittleEndian(IRandomAccess random, int value, int size) throws IOException {
		for (int i = 0; i < size; i++) {
			int bitShift = 8 * i;
			if (bitShift > 24) {
				random.write(0x00);
			} else {
				random.write((byte) ((value >>> bitShift) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, least significant byte first,
	 * using size bytes
	 * 
	 * @param random
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeLittleEndian(IRandomAccess random, long value, int size) throws IOException {
		for (int i = 0; i < size; i++) {
			int bitShift = 8 * i;
			if (bitShift > 56) {
				random.write(0x00);
			} else {
				random.write((byte) ((value >>> bitShift) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, least significant byte first,
	 * using size bytes
	 * 
	 * @param os
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeLittleEndian(OutputStream os, int value, int size) throws IOException {
		for (int i = 0; i < size; i++) {
			int bitShift = 8 * i;
			if (bitShift > 24) {
				os.write(0x00);
			} else {
				os.write((byte) ((value >>> bitShift) & 0xFF));
			}
		}
	}

	/**
	 * Write value into bytes, starting at offset, least significant byte first,
	 * using size bytes
	 * 
	 * @param os
	 * @param value
	 * @param size
	 * @throws IOException
	 */
	public static void writeLittleEndian(OutputStream os, long value, int size) throws IOException {
		for (int i = 0; i < size; i++) {
			int bitShift = 8 * i;
			if (bitShift > 56) {
				os.write(0x00);
			} else {
				os.write((byte) ((value >>> bitShift) & 0xFF));
			}
		}
	}

	private ByteArrayTools() {
	}
}
