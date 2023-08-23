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
import java.util.Arrays;

import de.intarsys.tools.hex.HexTools;

/**
 * The default {@link IDigest} implementation.
 * 
 */
public class Digest implements IDigest {

	private final String algorithmName;

	private final byte[] digest;

	protected Digest(String name, byte[] digest) {
		this.algorithmName = name;
		this.digest = digest;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Digest)) {
			return false;
		}
		Digest otherDigest = (Digest) obj;
		return otherDigest.algorithmName.equals(algorithmName) && Arrays.equals(otherDigest.digest, digest);
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public byte[] getBytes() {
		return digest;
	}

	@Override
	public byte[] getEncoded() throws IOException {
		return DigestTools.encode(this);
	}

	public int getSize() {
		return digest.length * 8;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(digest);
	}

	@Override
	public String toString() {
		return getAlgorithmName() + ":" //$NON-NLS-1$
				+ HexTools.bytesToHexString(getBytes());
	}
}
