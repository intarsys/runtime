/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.stream;

import java.io.OutputStream;

/**
 * 
 */
public class FastByteArrayOutputStream extends OutputStream {
	/** The buffer where data is stored. */
	protected byte[] buf;

	/** The number of valid bytes in the buffer. */
	protected int count;

	/**
	 * 
	 */
	public FastByteArrayOutputStream() {
		this(32);
	}

	public FastByteArrayOutputStream(int size) {
		buf = new byte[size];
	}

	public byte[] getBytes() {
		return buf;
	}

	public void reset() {
		count = 0;
	}

	public int size() {
		return count;
	}

	public byte[] toByteArray() {
		byte[] result = new byte[count];
		System.arraycopy(buf, 0, result, 0, count);
		return result;
	}

	@Override
	public void write(byte[] b, int off, int len) {
		int newcount = count + len;
		if (newcount > buf.length) {
			byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, buf.length);
			buf = newbuf;
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}

	@Override
	public void write(int b) {
		if (count >= buf.length) {
			byte[] newbuf = new byte[Math.max(buf.length << 1, 1)];
			System.arraycopy(buf, 0, newbuf, 0, buf.length);
			buf = newbuf;
		}
		buf[count++] = (byte) b;
	}
}
