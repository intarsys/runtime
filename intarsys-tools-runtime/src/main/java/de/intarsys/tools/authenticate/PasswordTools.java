/*
 * Copyright (c) 2012, intarsys GmbH
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
package de.intarsys.tools.authenticate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.crypto.CryptoTools;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for handling passwords and authentication
 */
public final class PasswordTools {

	public static final String CHARS_ADDITION = "!$%&/=?*+#-_"; //$NON-NLS-1$

	public static final String CHARS_DIGITS = "0123456879"; //$NON-NLS-1$

	public static final String CHARS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

	public static final String CHARS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

	public static final String CHARS = CHARS_DIGITS + CHARS_LOWERCASE + CHARS_UPPERCASE + CHARS_ADDITION;

	/**
	 * Create a random password of length <code>length</code>.
	 * 
	 * @param length
	 * @return A new random password.
	 */
	public static char[] createPassword(int length) {
		return createPassword(length, CHARS);
	}

	/**
	 * Create a secure random password of length <code>length</code> using a array
	 * of chars to chose from.
	 * <p>
	 * This implementation uses {@link java.security.SecureRandom}, which should be
	 * more secure (FIPS 140-2) than {@link java.util.Random}.
	 * 
	 * @param length the length of the password to generate
	 * @param valid  the char array to choose from
	 * @return A new random password.
	 */
	public static char[] createPassword(int length, String valid) {
		SecureRandom srand = CryptoTools.createSecureRandom();
		char[] buff = new char[length];
		int size = valid.length();
		for (int i = 0; i < length; ++i) {
			buff[i] = valid.charAt(srand.nextInt(size));
		}
		return buff;
	}

	/**
	 * Create a random salt of length <code>length</code>. The result is a
	 * String representing the Base64 encoded bytes.
	 * 
	 * @param length
	 * @return A new random salt String, base 64 encoded.
	 */
	public static String createSalt(int length) {
		byte[] saltBytes = ByteArrayTools.createRandomBytes(length);
		return new String(Base64.encode(saltBytes));
	}

	/**
	 * A one way hash for a clear text password.
	 * 
	 * @param salt
	 *            A Base64 encoded salt value.
	 * @param password
	 *            The clear text password
	 * @return A one way hash for a clear text password.
	 */
	public static String hash(String salt, char[] password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("missing SHA-1 hash");
		}
		if (!StringTools.isEmpty(salt)) {
			try {
				md.update(Base64.decode(salt));
			} catch (IOException e) {
				throw new IllegalArgumentException("invalid salt");
			}
		}
		md.update(new String(password).getBytes(StandardCharsets.UTF_8));
		byte[] raw = md.digest();
		return new String(Base64.encode(raw));
	}

	private PasswordTools() {
	}
}
