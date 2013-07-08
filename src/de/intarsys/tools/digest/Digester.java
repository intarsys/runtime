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
package de.intarsys.tools.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.logging.Level;

import de.intarsys.tools.monitor.Trace;
import de.intarsys.tools.stream.StreamTools;

/**
 * A JCA based {@link IDigester} implementation.
 */
public class Digester implements IDigester {

	private MessageDigest messageDigest;

	private String algorithmName;

	protected Digester(String algorithmName, MessageDigest messageDigest) {
		this.messageDigest = messageDigest;
		this.algorithmName = algorithmName;
	}

	protected IDigest basicDigest() {
		byte[] digest = getMessageDigest().digest();
		return new Digest(getAlgorithmName(), digest);
	}

	public IDigest digest(byte[] input) {
		try {
			Trace.get().sample(Level.FINE, "digest enter");
			getMessageDigest().update(input, 0, input.length);
			return basicDigest();
		} finally {
			Trace.get().sample(Level.FINE, "digest leave");
		}
	}

	public IDigest digestFinal() {
		try {
			Trace.get().sample(Level.FINE, "digest enter");
			return basicDigest();
		} finally {
			Trace.get().sample(Level.FINE, "digest leave");
		}
	}

	@Override
	public void digestUpdate(InputStream is) throws IOException {
		int bufferSize = StreamTools.suggestBufferSize(is.available());
		byte[] buffer = new byte[bufferSize];
		int i = is.read(buffer);
		while (i != -1) {
			getMessageDigest().update(buffer, 0, i);
			i = is.read(buffer);
		}
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	protected MessageDigest getMessageDigest() {
		return messageDigest;
	}

	public void reset() {
		getMessageDigest().reset();
	}

	@Override
	public String toString() {
		return getAlgorithmName();
	}

}
