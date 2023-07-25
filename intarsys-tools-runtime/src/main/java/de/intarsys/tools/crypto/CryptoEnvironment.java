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
package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * The crypto environment is the facade to the {@link ICryptdec} use.
 * 
 */
@SingletonClass
public class CryptoEnvironment {

	private static final CryptoEnvironment ACTIVE = new CryptoEnvironment();

	private static final ILogger Log = PACKAGE.Log;

	public static CryptoEnvironment get() {
		return ACTIVE;
	}

	private Map<String, ICryptdecFactory> factories = new HashMap<>();

	private ICryptdecFactory defaultCryptdecFactoryEncrypt;

	private ICryptdecFactory defaultCryptdecFactoryDecrypt;

	private CryptoEnvironment() {
		reset();
	}

	/**
	 * Create an {@link ICryptdec} for id.
	 * 
	 * @param id
	 * @return
	 * @throws GeneralSecurityException
	 */
	public ICryptdec createCryptdec(String id) throws GeneralSecurityException {
		if (StringTools.isEmpty(id)) {
			return createDefaultCryptdecDecrypt();
		} else {
			ICryptdecFactory factory = lookupCryptdecFactory(id);
			if (factory == null) {
				throw new GeneralSecurityException("unsupported cryptdec " + id);
			}
			return factory.createCryptdec();
		}
	}

	protected ICryptdec createDefaultCryptdecDecrypt() throws GeneralSecurityException {
		if (defaultCryptdecFactoryDecrypt == null) {
			throw new GeneralSecurityException("default cryptdec not defined");
		}
		return defaultCryptdecFactoryDecrypt.createCryptdec();
	}

	protected ICryptdec createDefaultCryptdecEncrypt() throws GeneralSecurityException {
		if (defaultCryptdecFactoryEncrypt == null) {
			throw new GeneralSecurityException("default cryptdec not defined");
		}
		return defaultCryptdecFactoryEncrypt.createCryptdec();
	}

	/**
	 * Decrypt a plain byte array which was previously encrypted using
	 * <code>encryptBytes</code>. For decryption the default {@link ICryptdec}
	 * is used.
	 * 
	 * @param bytes
	 * @return The decrypted representation of <code>bytes</code>
	 * @throws GeneralSecurityException
	 */
	public byte[] decryptBytes(byte[] bytes) throws GeneralSecurityException {
		return decryptBytes(bytes, null);
	}

	/**
	 * Decrypt a plain byte array which was previously encrypted using
	 * <code>encryptBytes</code> using cryptdec.
	 * 
	 * @param bytes
	 * @return The decrypted representation of <code>bytes</code>
	 * @throws GeneralSecurityException
	 */
	public byte[] decryptBytes(byte[] bytes, ICryptdec cryptdec) throws GeneralSecurityException {
		if (bytes == null) {
			return null; // NOSONAR
		}
		if (cryptdec == null) {
			cryptdec = createDefaultCryptdecDecrypt();
		}
		return cryptdec.decrypt(bytes);
	}

	/**
	 * Decrypt a string which was previously encrypted using
	 * <code>encryptString</code>, using the default {@link ICryptdec}.
	 * 
	 * @param value
	 * @return The decrypted representation of <code>value</code>
	 * @throws GeneralSecurityException
	 */
	public char[] decryptChars(byte[] value) throws GeneralSecurityException {
		return decryptChars(value, null);
	}

	/**
	 * Decrypt a string which was previously encrypted using
	 * <code>encryptString</code>, using cryptdec.
	 * 
	 * @param value
	 * @return The decrypted representation of <code>value</code>
	 * @throws GeneralSecurityException
	 */
	public char[] decryptChars(byte[] value, ICryptdec cryptdec) throws GeneralSecurityException {
		if (value == null) {
			return null; // NOSONAR
		}
		byte[] decrypted = decryptBytes(value, cryptdec);
		return CharacterTools.toCharArrayUTF8(decrypted);
	}

	/**
	 * Encrypt bytes using the default {@link ICryptdec}. If bytes are null,
	 * null is returned.
	 * 
	 * The result is the encrypted byte array.
	 * 
	 * @param bytes
	 * @return The encrypted representation of <code>bytes</code>
	 * @throws GeneralSecurityException
	 */
	public byte[] encryptBytes(byte[] bytes) throws GeneralSecurityException {
		return encryptBytes(bytes, null);
	}

	/**
	 * Encrypt bytes using cryptdec. If bytes are null, null is returned.
	 * 
	 * The result is the encrypted byte array.
	 * 
	 * @param bytes
	 * @return The encrypted representation of <code>bytes</code>
	 * @throws GeneralSecurityException
	 */
	public byte[] encryptBytes(byte[] bytes, ICryptdec cryptdec) throws GeneralSecurityException {
		if (bytes == null) {
			return null; // NOSONAR
		}
		if (cryptdec == null) {
			cryptdec = createDefaultCryptdecEncrypt();
		}
		return cryptdec.encrypt(bytes);
	}

	/**
	 * Encrypt value using the default {@link ICryptdec}. If value is null, null
	 * is returned. value is converted to a byte array using the UTF-8 encoding.
	 * 
	 * The result is a base 64 representation of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws GeneralSecurityException
	 */
	public byte[] encryptChars(char[] value) throws GeneralSecurityException {
		return encryptChars(value, null);
	}

	/**
	 * Encrypt value using the cryptdec. if value is null, null is returned.
	 * value is converted to a byte array using the UTF-8 encoding.
	 * 
	 * The result is a base 64 representation of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws GeneralSecurityException
	 */
	public byte[] encryptChars(char[] value, ICryptdec cryptdec) throws GeneralSecurityException {
		if (value == null) {
			return null; // NOSONAR
		}
		return encryptBytes(CharacterTools.toByteArrayUTF8(value), cryptdec);
	}

	public Collection<ICryptdecFactory> getCryptdecFactories() {
		return factories.values();
	}

	public ICryptdecFactory getDefaultCryptdecFactoryDecrypt() {
		return defaultCryptdecFactoryDecrypt;
	}

	public ICryptdecFactory getDefaultCryptdecFactoryEncrypt() {
		return defaultCryptdecFactoryEncrypt;
	}

	public ICryptdecFactory lookupCryptdecFactory(String id) {
		return factories.get(id);
	}

	public void registerCryptdecFactory(ICryptdecFactory factory) {
		if (factories.containsKey(factory.getId())) {
			Log.warn("{} can't redefine cryptdec '{}'", this, factory.getId());
			return;
		}
		factories.put(factory.getId(), factory);
	}

	protected void reset() {
		defaultCryptdecFactoryDecrypt = null;
		defaultCryptdecFactoryEncrypt = null;
		factories.clear();
		NullCryptdecFactory plain = new NullCryptdecFactory("plain");
		registerCryptdecFactory(plain);
		setDefaultCryptdecFactoryDecrypt(plain);
		setDefaultCryptdecFactoryEncrypt(plain);
	}

	public void setDefaultCryptdecFactoryDecrypt(ICryptdecFactory factory) {
		if (defaultCryptdecFactoryDecrypt != null && !(defaultCryptdecFactoryDecrypt instanceof NullCryptdecFactory)) {
			Log.warn("{} can't redefine cryptdec '{}'", this, factory.getId());
			return;
		}
		defaultCryptdecFactoryDecrypt = factory;
	}

	public void setDefaultCryptdecFactoryEncrypt(ICryptdecFactory factory) {
		if (defaultCryptdecFactoryEncrypt != null && !(defaultCryptdecFactoryEncrypt instanceof NullCryptdecFactory)) {
			Log.warn("{} can't redefine cryptdec '{}'", this, factory.getId());
			return;
		}
		defaultCryptdecFactoryEncrypt = factory;
	}
}
