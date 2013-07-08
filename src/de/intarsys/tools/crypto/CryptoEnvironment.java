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
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for handling en/decryption
 * 
 */
public class CryptoEnvironment {

	final private static CryptoEnvironment ACTIVE = new CryptoEnvironment();

	static public CryptoEnvironment get() {
		return ACTIVE;
	}

	private Map<String, ICryptdecFactory> factories = new HashMap<String, ICryptdecFactory>();

	private ICryptdecFactory defaultCryptdecFactoryEncrypt;

	private ICryptdecFactory defaultCryptdecFactoryDecrypt;

	private CryptoEnvironment() {
	}

	/**
	 * Create an {@link ICryptdec} for id.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public ICryptdec createCryptdec(String id) throws IOException {
		try {
			ICryptdecFactory factory = factories.get(id);
			if (factory == null) {
				throw new IOException("unsupported cryptdec " + id);
			}
			return factories.get(id).createCryptdec();
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	protected ICryptdec createDefaultCryptdecDecrypt() throws IOException {
		if (defaultCryptdecFactoryDecrypt == null) {
			throw new IOException("default cryptdec not defined");
		}
		try {
			return defaultCryptdecFactoryDecrypt.createCryptdec();
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	protected ICryptdec createDefaultCryptdecEncrypt() throws IOException {
		if (defaultCryptdecFactoryEncrypt == null) {
			throw new IOException("default cryptdec not defined");
		}
		try {
			return defaultCryptdecFactoryEncrypt.createCryptdec();
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Decrypt a plain byte array which was previously encrypted using
	 * <code>encryptBytes</code>. For decryption the default {@link ICryptdec}
	 * is used.
	 * 
	 * @param bytes
	 * @return The decrypted representation of <code>bytes</code>
	 * @throws IOException
	 */
	public byte[] decryptBytes(byte[] bytes) throws IOException {
		return decryptBytes(bytes, null);
	}

	/**
	 * Decrypt a plain byte array which was previously encrypted using
	 * <code>encryptBytes</code> using cryptdec.
	 * 
	 * @param bytes
	 * @return The decrypted representation of <code>bytes</code>
	 * @throws IOException
	 */
	public byte[] decryptBytes(byte[] bytes, ICryptdec cryptdec)
			throws IOException {
		if (cryptdec == null) {
			cryptdec = createDefaultCryptdecDecrypt();
		}
		return cryptdec.decrypt(bytes);
	}

	/**
	 * Decrypt the encoded, encrypted value created using "encryptBytesEncoded"
	 * using the default {@link ICryptdec}.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public byte[] decryptBytesEncoded(String value) throws IOException {
		return decryptBytesEncoded(value, null);
	}

	/**
	 * Decrypt the encoded, encrypted value created using "encryptBytesEncoded"
	 * with cryptdec.
	 * 
	 * @param value
	 * @param cryptdec
	 * @return
	 * @throws IOException
	 */
	public byte[] decryptBytesEncoded(String value, ICryptdec cryptdec)
			throws IOException {
		try {
			String[] parts = value.split("#");
			String tempValue;
			ICryptdec tempCryptdec;
			if (parts.length == 1) {
				tempCryptdec = cryptdec;
				tempValue = parts[0];
			} else {
				tempCryptdec = createCryptdec(parts[0]);
				tempValue = parts[1];
			}
			if (StringTools.isEmpty(tempValue)) {
				return new byte[0];
			}
			byte[] bytes = Base64.decode(StringTools.toByteArray(tempValue));
			byte[] decrypted = decryptBytes(bytes, tempCryptdec);
			return decrypted;
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Decrypt a string which was previously encrypted using
	 * <code>encryptString</code>, using the default {@link ICryptdec}.
	 * 
	 * @param value
	 * @return The decrypted representation of <code>value</code>
	 * @throws IOException
	 */
	public String decryptString(String value) throws IOException {
		return decryptString(value, null);
	}

	/**
	 * Decrypt a string which was previously encrypted using
	 * <code>encryptString</code>, using cryptdec.
	 * 
	 * @param value
	 * @return The decrypted representation of <code>value</code>
	 * @throws IOException
	 */
	public String decryptString(String value, ICryptdec cryptdec)
			throws IOException {
		try {
			if (StringTools.isEmpty(value)) {
				return value;
			}
			byte[] bytes = Base64.decode(StringTools.toByteArray(value));
			byte[] decrypted = decryptBytes(bytes, cryptdec);
			return new String(decrypted, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Decrypt the encoded, encrypted value created using "encryptStringEncoded"
	 * using the default {@link ICryptdec}. The decrypted data is converted to a
	 * String using the UTF-8 encoding.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String decryptStringEncoded(String value) throws IOException {
		return decryptStringEncoded(value, null);
	}

	/**
	 * Decrypt the encoded, encrypted value created using "encryptStringEncoded"
	 * with cryptdec. The decrypted data is converted to a String using the
	 * UTF-8 encoding.
	 * 
	 * @param value
	 * @param cryptdec
	 * @return
	 * @throws IOException
	 */
	public String decryptStringEncoded(String value, ICryptdec cryptdec)
			throws IOException {
		try {
			byte[] decrypted = decryptBytesEncoded(value, cryptdec);
			return new String(decrypted, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Encrypt a byte array using the default {@link ICryptdec}.
	 * 
	 * The result is the encrypted byte array.
	 * 
	 * @param bytes
	 * @return The encrypted representation of <code>bytes</code>
	 * @throws IOException
	 */
	public byte[] encryptBytes(byte[] bytes) throws IOException {
		return encryptBytes(bytes, null);
	}

	/**
	 * Encrypt a byte array using the cryptdec.
	 * 
	 * The result is the encrypted byte array.
	 * 
	 * @param bytes
	 * @return The encrypted representation of <code>bytes</code>
	 * @throws IOException
	 */
	public byte[] encryptBytes(byte[] bytes, ICryptdec cryptdec)
			throws IOException {
		if (cryptdec == null) {
			cryptdec = createDefaultCryptdecEncrypt();
		}
		return cryptdec.encrypt(bytes);
	}

	/**
	 * Encrypt the byte array value using the default {@link ICryptdec}.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String encryptBytesEncoded(byte[] value) throws IOException {
		if (defaultCryptdecFactoryEncrypt == null) {
			throw new IllegalStateException("default cryptdec not defined");
		}
		try {
			ICryptdec cryptdec = defaultCryptdecFactoryEncrypt.createCryptdec();
			return encryptBytesEncoded(value, cryptdec);
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Encrypt a byte array using the cryptdec.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param bytes
	 * @param cryptdec
	 * @return
	 * @throws IOException
	 */
	public String encryptBytesEncoded(byte[] bytes, ICryptdec cryptdec)
			throws IOException {
		try {
			if (cryptdec == null) {
				cryptdec = createDefaultCryptdecEncrypt();
			}
			byte[] encrypted = encryptBytes(bytes, cryptdec);
			return cryptdec.getId() + "#"
					+ new String(Base64.encode(encrypted));
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Encrypt the char array value using the default {@link ICryptdec}. The
	 * char array is converted to a byte array using the UTF-8 encoding.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String encryptCharsEncoded(char[] value) throws IOException {
		return encryptCharsEncoded(value, null);
	}

	/**
	 * Encrypt the char array value using the cryptdec. The char array is
	 * converted to a byte array using the UTF-8 encoding.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String encryptCharsEncoded(char[] value, ICryptdec cryptdec)
			throws IOException {
		if (cryptdec == null) {
			cryptdec = createDefaultCryptdecEncrypt();
		}
		byte[] bytes = new String(value).getBytes("UTF8");
		return encryptBytesEncoded(bytes, cryptdec);
	}

	/**
	 * Encrypt a String using the default {@link ICryptdec}. The result is a
	 * Base64 encoded version of the encrypted UTF-8 encoded input bytes.
	 * 
	 * @param value
	 * @return An encrypted, invertible representation of <code>value</code>
	 * @throws IOException
	 */
	public String encryptString(String value) throws IOException {
		return encryptString(value, null);
	}

	/**
	 * Encrypt a String using cryptdec. The result is a Base64 encoded version
	 * of the encrypted UTF-8 encoded input bytes.
	 * 
	 * @param value
	 * @return An encrypted, invertible representation of <code>value</code>
	 * @throws IOException
	 */
	public String encryptString(String value, ICryptdec cryptdec)
			throws IOException {
		try {
			if (cryptdec == null) {
				cryptdec = createDefaultCryptdecEncrypt();
			}
			byte[] bytes = value.getBytes("UTF8");
			byte[] encrypted = encryptBytes(bytes, cryptdec);
			return new String(Base64.encode(encrypted));
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Encrypt the String value using the default {@link ICryptdec}. The value
	 * converted to a byte array using the UTF-8 encoding.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String encryptStringEncoded(String value) throws IOException {
		return encryptStringEncoded(value, null);
	}

	/**
	 * Encrypt the String value using the cryptdec. The value converted to a
	 * byte array using the UTF-8 encoding.
	 * 
	 * The result is an encoded representation (cryptdec id + "#" +
	 * base64(encryption)) of the encrypted data.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public String encryptStringEncoded(String value, ICryptdec cryptdec)
			throws IOException {
		try {
			if (cryptdec == null) {
				cryptdec = createDefaultCryptdecEncrypt();
			}
			byte[] bytes = value.getBytes("UTF8");
			byte[] encrypted = encryptBytes(bytes, cryptdec);
			return cryptdec.getId() + "#"
					+ new String(Base64.encode(encrypted));
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public ICryptdecFactory getDefaultCryptdecFactoryDecrypt() {
		return defaultCryptdecFactoryDecrypt;
	}

	public ICryptdecFactory getDefaultCryptdecFactoryEncrypt() {
		return defaultCryptdecFactoryEncrypt;
	}

	public void registerCryptdecFactory(ICryptdecFactory factory) {
		if (factories.containsKey(factory.getId())) {
			throw new IllegalStateException("can't redefine cryptdecs");
		}
		factories.put(factory.getId(), factory);
	}

	public void setDefaultCryptdecFactoryDecrypt(ICryptdecFactory factory) {
		if (defaultCryptdecFactoryDecrypt != null) {
			throw new IllegalStateException("can't redefine cryptdecs");
		}
		defaultCryptdecFactoryDecrypt = factory;
	}

	public void setDefaultCryptdecFactoryEncrypt(ICryptdecFactory factory) {
		if (defaultCryptdecFactoryEncrypt != null) {
			throw new IllegalStateException("can't redefine cryptdecs");
		}
		defaultCryptdecFactoryEncrypt = factory;
	}
}
