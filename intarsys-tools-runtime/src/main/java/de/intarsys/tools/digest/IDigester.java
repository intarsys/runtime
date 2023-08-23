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
package de.intarsys.tools.digest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Computes an {@link IDigest} (hash value) for the provided data.
 * 
 */
public interface IDigester {

	/**
	 * The digester to be used for creating the digest
	 */
	String ARG_DIGESTER = "digester"; //$NON-NLS-1$

	/**
	 * Digest bytes and return the resulting {@link IDigest}.
	 * 
	 * @param bytes
	 * @return
	 */
	public IDigest digest(byte[] bytes);

	/**
	 * Finalize a pending digest process with the data in the internal buffer.
	 * 
	 * @return
	 */
	public IDigest digestFinal();

	/**
	 * Append all bytes from input stream is to the internal buffer for
	 * digesting.
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void digestUpdate(InputStream is) throws IOException;

	/**
	 * The JCA digest algorithm name for this {@link IDigester}.
	 * 
	 * @return
	 */
	public String getAlgorithmName();

	/**
	 * The byte length of digests created by this function
	 * 
	 * @return
	 */
	public int getDigestLength();

	/**
	 * Reset the internal state.
	 */
	public void reset();

}
