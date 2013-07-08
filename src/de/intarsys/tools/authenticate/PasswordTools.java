/*
 * Copyright (c) 2012, intarsys consulting GmbH
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for handling passwords and authentication
 * 
 */
public class PasswordTools {

	final private static String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDREFGHIJKLMNOPQRSTUVWXYZ!$%&/=?*+#-_";

	/**
	 * Create a random password of length <code>length</code>.
	 * 
	 * @param length
	 * @return A new random password.
	 */
	static public char[] createPassword(int length) {
		Random rand = new Random(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= length; i++) {
			int pos = rand.nextInt(CHARS.length());
			sb.append(CHARS.charAt(pos));
		}
		return sb.toString().toCharArray();
	}

	/**
	 * Create a random salt of length <code>length</code>. The result is a
	 * String representing the Base64 encoded bytes.
	 * 
	 * @param length
	 * @return A new random salt String, base 64 encoded.
	 */
	static public String createSalt(int length) {
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
	static public String hash(String salt, char[] password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("missing SHA-1 hash");
		}
		if (!StringTools.isEmpty(salt)) {
			md.update(Base64.decode(salt));
		}
		try {
			md.update(new String(password).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("missing UTF-8 encoding");
		}
		byte raw[] = md.digest();
		return new String(Base64.encode(raw));
	}
}
