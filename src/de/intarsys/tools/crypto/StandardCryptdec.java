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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * A default {@link ICryptdec} implementation based on the JCA.
 */
public class StandardCryptdec implements ICryptdec {

	final private String id;

	final private byte[] initializationVector;

	final private Cipher ecipher;

	final private Cipher dcipher;

	private byte[] inBuffer;

	public StandardCryptdec(String id, Cipher eCipher, Cipher dCipher, byte[] iv) {
		this.id = id;
		this.ecipher = eCipher;
		this.dcipher = dCipher;
		this.initializationVector = Arrays.copyOf(iv, iv.length);
	}

	public byte[] decrypt(byte[] bytes) throws IOException {
		try {
			return dcipher.doFinal(bytes);
		} catch (Exception e) {
			throw ExceptionTools.createIOException(e.getMessage(), e);
		}
	}

	@Override
	public void decryptFinal(OutputStream os) throws IOException {
		try {
			byte[] outBuffer = dcipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw ExceptionTools.createIOException(e.getMessage(), e);
		}
	}

	@Override
	public void decryptUpdate(InputStream is, OutputStream os)
			throws IOException {
		try {
			ensureInBuffer(is);
			byte[] outBuffer;
			for (int i = is.read(inBuffer); i != -1;) {
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
			throw ExceptionTools.createIOException(e.getMessage(), e);
		}
	}

	public byte[] encrypt(byte[] bytes) throws IOException {
		try {
			return ecipher.doFinal(bytes);
		} catch (Exception e) {
			throw ExceptionTools.createIOException(e.getMessage(), e);
		}
	}

	@Override
	public void encryptFinal(OutputStream os) throws IOException {
		try {
			byte[] outBuffer = ecipher.doFinal();
			if (outBuffer != null) {
				os.write(outBuffer);
			}
		} catch (Exception e) {
			throw ExceptionTools.createIOException(e.getMessage(), e);
		}
	}

	@Override
	public void encryptUpdate(InputStream is, OutputStream os)
			throws IOException {
		try {
			ensureInBuffer(is);
			byte[] outBuffer;
			for (int i = is.read(inBuffer); i != -1;) {
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
			throw ExceptionTools.createIOException(e.getMessage(), e);
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

	public String getId() {
		return id;
	}

	public byte[] getInitializationVector() {
		return initializationVector;
	}

}
