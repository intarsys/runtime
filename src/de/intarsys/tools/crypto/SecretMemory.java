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
package de.intarsys.tools.crypto;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.authenticate.PasswordTools;
import de.intarsys.tools.collection.ByteArrayTools;

/**
 * A memory holding "secret" values. Goal is to have "visible" secrets only at
 * the API of the store. Within the store the data is encrypted to doom memory
 * inspection..
 * <p>
 * For this strategy to be successful, its very important for clients to zero
 * out secrets that are no longer used.
 * <p>
 * The memory itself is based on anonymous handles - this way no client can
 * enumerate or guess content of this memory - as long as the owner keeps its
 * handle private.
 * 
 */
public class SecretMemory {

	private static ICryptdec createSessionCryptdec() {
		char[] passPhrase = null;
		byte[] salt = null;
		byte[] iv = null;
		passPhrase = PasswordTools.createPassword(16);
		salt = ByteArrayTools.createRandomBytes(8);
		iv = ByteArrayTools.createRandomBytes(16);
		try {
			return new PBAES128CryptdecFactory("session", iv, passPhrase, salt,
					19).createCryptdec();
		} catch (Exception e) {
			throw new IllegalStateException("cryptec initialization error", e);
		} finally {
			Arrays.fill(passPhrase, ' ');
			Arrays.fill(salt, (byte) 0x00);
			Arrays.fill(iv, (byte) 0x00);
		}
	}

	final transient private ICryptdec cryptdec;

	final transient private Map<Object, byte[]> memory = new HashMap<Object, byte[]>();

	public SecretMemory() {
		this(createSessionCryptdec());
	}

	public SecretMemory(ICryptdec cryptdec) {
		super();
		this.cryptdec = cryptdec;
	}

	public void clear() {
		memory.clear();
	}

	public byte[] getBytes(Object key) throws IOException {
		byte[] crypted = memory.get(key);
		if (crypted == null) {
			return null;
		}
		byte[] decrypted = cryptdec.decrypt(crypted);
		return decrypted;
	}

	public char[] getCharacters(Object key) throws IOException {
		byte[] crypted = memory.get(key);
		if (crypted == null) {
			return null;
		}
		byte[] decrypted = cryptdec.decrypt(crypted);
		char[] secret = new char[decrypted.length / 2];
		int k = 0;
		for (int i = 0; i < secret.length; i++) {
			char c = (char) (((decrypted[k++] & 0xff) << 8) + (decrypted[k++] & 0xff));
			secret[i] = c;
		}
		return secret;
	}

	public Object put(byte[] secret) throws IOException {
		byte[] crypted = cryptdec.encrypt(secret);
		Object key = new Object();
		memory.put(key, crypted);
		return key;
	}

	public Object put(char[] secret) throws IOException {
		byte[] bytes = new byte[secret.length * 2];
		int k = 0;
		for (int i = 0; i < secret.length; i++) {
			char c = secret[i];
			bytes[k++] = (byte) (c >> 8);
			bytes[k++] = (byte) c;
		}
		byte[] crypted = cryptdec.encrypt(bytes);
		Object key = new Object();
		memory.put(key, crypted);
		return key;
	}

	public void remove(Object key) {
		memory.remove(key);
	}
}
