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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * A default {@link ICryptdec} implementation based on the JCA.
 */
public class DynamicCryptdec implements ICryptdec {

	private static final int IV_LENGTH = 16;

	private final String id;

	private final String cipherId;

	private Cipher ecipher;

	private Cipher dcipher;

	private byte[] inBuffer;

	private final SecretKey key;

	public DynamicCryptdec(String id, SecretKey key, String cipherId)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.id = id;
		this.key = key;
		this.cipherId = cipherId;
	}

	@Override
	public byte[] decrypt(byte[] bytes) throws GeneralSecurityException {
		try {
			byte[] iv = Arrays.copyOfRange(bytes, 0, IV_LENGTH);
			@SuppressWarnings("java:S3329")
			IvParameterSpec ips = new IvParameterSpec(iv);
			dcipher = Cipher.getInstance(getCipherId());
			dcipher.init(Cipher.DECRYPT_MODE, getKey(), ips);
			return dcipher.doFinal(bytes, IV_LENGTH, bytes.length - IV_LENGTH);
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} finally {
			dcipher = null;
		}
	}

	@Override
	public void decryptFinal(OutputStream os) throws GeneralSecurityException {
		try {
			if (dcipher == null) {
				throw new GeneralSecurityException("invalid cipher state");
			}
			byte[] outBuffer = dcipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public void decryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException {
		try {
			ensureInBuffer(is);
			if (dcipher == null) {
				dcipher = Cipher.getInstance(getCipherId());
				byte[] iv = new byte[IV_LENGTH];
				int i = is.read(iv);
				if (i != IV_LENGTH) {
					throw new GeneralSecurityException("input too short");
				}
				@SuppressWarnings("java:S3329")
				IvParameterSpec ips = new IvParameterSpec(iv);
				dcipher.init(Cipher.DECRYPT_MODE, getKey(), ips);
			}
			byte[] outBuffer;
			int i = is.read(inBuffer);
			while (i != -1) {
				outBuffer = dcipher.update(inBuffer, 0, i);
				if (outBuffer != null) {
					os.write(outBuffer);
				}
				i = is.read(inBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] encrypt(byte[] bytes) throws GeneralSecurityException {
		try {
			SecureRandom rand = CryptoTools.createSecureRandom();
			byte[] iv = new byte[IV_LENGTH];
			rand.nextBytes(iv);
			IvParameterSpec ips = new IvParameterSpec(iv);
			ecipher = Cipher.getInstance(getCipherId());
			ecipher.init(Cipher.ENCRYPT_MODE, getKey(), ips);
			return ByteArrayTools.concat(iv, ecipher.doFinal(bytes));
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} finally {
			ecipher = null;
		}
	}

	@Override
	public void encryptFinal(OutputStream os) throws GeneralSecurityException {
		try {
			if (ecipher == null) {
				throw new GeneralSecurityException("invalid cipher state");
			}
			byte[] outBuffer = ecipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} finally {
			ecipher = null;
		}
	}

	@Override
	public void encryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException {
		try {
			ensureInBuffer(is);
			if (ecipher == null) {
				ecipher = Cipher.getInstance(getCipherId());
				SecureRandom rand = CryptoTools.createSecureRandom();
				byte[] iv = new byte[IV_LENGTH];
				rand.nextBytes(iv);
				IvParameterSpec ips = new IvParameterSpec(iv);
				ecipher.init(Cipher.ENCRYPT_MODE, getKey(), ips);
				os.write(iv);
			}
			byte[] outBuffer;
			int i = is.read(inBuffer);
			while (i != -1) {
				outBuffer = ecipher.update(inBuffer, 0, i);
				if (outBuffer != null) {
					os.write(outBuffer);
				}
				i = is.read(inBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	protected void ensureInBuffer(InputStream is) {
		if (inBuffer == null) {
			inBuffer = new byte[StreamTools.suggestBufferSize(is)];
		}
	}

	public String getCipherId() {
		return cipherId;
	}

	protected Cipher getDcipher() {
		return dcipher;
	}

	protected Cipher getEcipher() {
		return ecipher;
	}

	@Override
	public String getId() {
		return id;
	}

	private SecretKey getKey() {
		return key;
	}

}
