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
package de.intarsys.tools.string;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A tool class for the handling of characters and char[].
 * 
 */
public final class CharacterTools {

	/**
	 * clear character array content.
	 * 
	 * @param value
	 *            The value to be cleared
	 */
	public static void clear(char[] value) {
		if (value == null) {
			return;
		}
		Arrays.fill(value, '0');
	}

	/**
	 * <code>true</code> if <code>value</code> is "empty" in any respect.
	 * <p>
	 * This is the case when value == null, value has no characters or only
	 * whitespace.
	 * 
	 * @param value
	 *            The value to be inspected for emptyness.
	 * @return <code>true</code> if <code>value</code> is "empty" in any
	 *         respect.
	 */
	public static boolean isEmpty(char[] value) {
		if ((value == null) || (value.length == 0)) {
			return true;
		}
		for (int i = 0; i < value.length; i++) {
			if (!Character.isWhitespace(value[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <code>true</code> if <code>c</code> is a vowel.
	 * 
	 * @param c
	 *            The character to check.
	 * @return <code>true</code> if <code>c</code> is a vowel.
	 */
	public static boolean isVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O'
				|| c == 'U';
	}

	/**
	 * Create a byte array from the char array ignoring any encoding.
	 * 
	 * @param value
	 * @return A byte array created from the char array in value.
	 */
	@SuppressWarnings("java:S1168")
	public static byte[] toByteArray(char[] value) {
		if (value == null) {
			return null;
		}
		byte[] bytes = new byte[value.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) value[i];
		}
		return bytes;
	}

	@SuppressWarnings("java:S1168")
	public static byte[] toByteArray(char[] chars, Charset charset) {
		if (chars == null) {
			return null;
		}
		ByteBuffer bb = charset.encode(CharBuffer.wrap(chars));
		byte[] bytes = new byte[bb.remaining()];
		bb.get(bytes, 0, bytes.length);
		return bytes;
	}

	public static byte[] toByteArrayUTF8(char[] chars) {
		Charset charset = StandardCharsets.UTF_8;
		return toByteArray(chars, charset);
	}

	/**
	 * Create a char array from the byte array ignoring any encoding.
	 * 
	 * @param value
	 * @return A char array created from the byte array in value.
	 */
	@SuppressWarnings("java:S1168")
	public static char[] toCharArray(byte[] value) {
		if (value == null) {
			return null;
		}
		char[] chars = new char[value.length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) value[i];
		}
		return chars;
	}

	public static char[] toCharArray(byte[] bytes, Charset charset) {
		if (bytes == null) {
			return null; // NOSONAR
		}
		CharBuffer cb = charset.decode(ByteBuffer.wrap(bytes));
		char[] chars = new char[cb.remaining()];
		cb.get(chars, 0, chars.length);
		return chars;
	}

	public static char[] toCharArrayUTF8(byte[] bytes) {
		Charset charset = StandardCharsets.UTF_8;
		return toCharArray(bytes, charset);
	}

	/**
	 * Create a new char[] from <code>value</code> without whitespace.
	 * 
	 * This code is copied from {@link String}.
	 * 
	 * @param value
	 *            The char[] to be stripped, may be null
	 * 
	 * @return A copy of <code>value</code> with whitespace stripped.
	 */
	public static char[] trim(char[] value) {
		if (value == null) {
			return null; // NOSONAR
		}
		int len = value.length;
		int st = 0;
		char[] val = value; /* avoid getfield opcode */

		while ((st < len) && (val[st] <= ' ')) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ')) {
			len--;
		}
		return ((st > 0) || (len < value.length)) ? Arrays.copyOfRange(value, st, len) : value;
	}

	private CharacterTools() {
	}
}
