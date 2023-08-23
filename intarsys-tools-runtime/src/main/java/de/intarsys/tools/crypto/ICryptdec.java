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

/**
 * En/Decrypt data. The {@link ICryptdec} provides a ready to use interface for
 * encryption purposes. The implementation algorithm and parameters are hidden.
 * 
 * Input and output of the {@link ICryptdec} is "self contained", i.e if we create an IV for encryption,
 * the value is written into the encoded output for later retrieval.
 * 
 * An {@link ICryptdec} can not be used by multiple threads concurrently, it
 * holds the current crypto state.
 */
public interface ICryptdec {

	/**
	 * Decrypt bytes and return the decrypted data.
	 * 
	 * The {@link ICryptdec} may be reused after this operation finished
	 * successful.
	 * 
	 * @param bytes
	 * @return
	 * @throws GeneralSecurityException
	 */
	public byte[] decrypt(byte[] bytes) throws GeneralSecurityException;

	/**
	 * Finalize a pending decryption process with the data in the internal
	 * buffer.
	 * 
	 * The buffered data is optionally padded and decrypted, the output is
	 * written to os.
	 * 
	 * The {@link ICryptdec} may be reused after this operation finished
	 * successful.
	 * 
	 * @param os
	 * @throws GeneralSecurityException
	 */
	public void decryptFinal(OutputStream os) throws GeneralSecurityException;

	/**
	 * Append all bytes from input stream is to the internal buffer for
	 * decryption.
	 * 
	 * If any output is generated (e.g. with block ciphers) it is written to os.
	 * 
	 * @param is
	 * @param os
	 * @throws GeneralSecurityException
	 */
	public void decryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException;

	/**
	 * Encrypt bytes and return the encrypted data.
	 * 
	 * The {@link ICryptdec} may be reused after this operation finished
	 * successful.
	 * 
	 * @param bytes
	 * @return
	 * @throws GeneralSecurityException
	 */
	public byte[] encrypt(byte[] bytes) throws GeneralSecurityException;

	/**
	 * Finalize a pending encryption process with the data in the internal
	 * buffer.
	 * 
	 * The buffered data is optionally padded and encrypted, the output is
	 * written to os.
	 * 
	 * The {@link ICryptdec} may be reused after this operation finished
	 * successful.
	 * 
	 * @param os
	 * @throws GeneralSecurityException
	 */
	public void encryptFinal(OutputStream os) throws GeneralSecurityException;

	/**
	 * Append all bytes from input stream is to the internal buffer for
	 * encryption.
	 * 
	 * If any output is generated (e.g. with block ciphers) it is written to os.
	 * 
	 * @param is
	 * @param os
	 * @throws GeneralSecurityException
	 */
	public void encryptUpdate(InputStream is, OutputStream os) throws GeneralSecurityException;

	/**
	 * A unique id for the {@link ICryptdec}. This may be used to tag encrypted
	 * data for later decryption.
	 * 
	 * @return A unique id for the {@link ICryptdec}
	 */
	public String getId();
}
