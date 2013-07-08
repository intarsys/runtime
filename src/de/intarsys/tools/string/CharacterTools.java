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
package de.intarsys.tools.string;

/**
 * A tool class for the handling of strings.
 * 
 */
public class CharacterTools {

	/**
	 * <code>true</code> if <code>c</code> is a vowel.
	 * 
	 * @param c
	 *            The character to check.
	 * @return <code>true</code> if <code>c</code> is a vowel.
	 */
	static public boolean isVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'
				|| c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U';
	}

	/**
	 * Create a byte array from the char array ignoring any encoding.
	 * 
	 * @param value
	 * @return A byte array created from the char array in value.
	 */
	static public byte[] toByteArray(char[] value) {
		if (value == null) {
			return null;
		}
		byte[] bytes = new byte[value.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) value[i];
		}
		return bytes;
	}

	/**
	 * Create a char array from the byte array ignoring any encoding.
	 * 
	 * @param value
	 * @return A char array created from the byte array in value.
	 */
	static public char[] toCharArray(byte[] value) {
		if (value == null) {
			return null;
		}
		char[] chars = new char[value.length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) value[i];
		}
		return chars;
	}
}
