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

import javax.crypto.Cipher;

import de.intarsys.tools.stream.StreamTools;

/**
 * A default {@link ICryptdec} implementation based on the JCA.
 */
public class StaticCryptdec implements ICryptdec {

	private final String id;

	private final Cipher ecipher;

	private final Cipher dcipher;

	private byte[] inBuffer;

	public StaticCryptdec(String id, Cipher eCipher, Cipher dCipher) {
		this.id = id;
		this.ecipher = eCipher;
		this.dcipher = dCipher;
	}

	@Override
	public byte[] decrypt(byte[] bytes) throws GeneralSecurityException {
		try {
			return dcipher.doFinal(bytes);
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public void decryptFinal(OutputStream os) throws GeneralSecurityException {
		try {
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
			byte[] outBuffer;
			int i = is.read(inBuffer);
			while (i != -1) {
				outBuffer = dcipher.update(inBuffer, 0, i);
				if (outBuffer != null) {
					os.write(outBuffer);
				}
				i = is.read(inBuffer);
			}
			outBuffer = dcipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] encrypt(byte[] bytes) throws GeneralSecurityException {
		try {
			return ecipher.doFinal(bytes);
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public void encryptFinal(OutputStream os) throws GeneralSecurityException {
		try {
			byte[] outBuffer = ecipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		}
	}

	@Override
	public void encryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException {
		try {
			ensureInBuffer(is);
			byte[] outBuffer;
			int i = is.read(inBuffer);
			while (i != -1) {
				outBuffer = ecipher.update(inBuffer, 0, i);
				if (outBuffer != null) {
					os.write(outBuffer);
				}
				i = is.read(inBuffer);
			}
			outBuffer = ecipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
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

}
