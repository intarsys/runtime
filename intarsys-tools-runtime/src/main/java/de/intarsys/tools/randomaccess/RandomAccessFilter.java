/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.randomaccess;

import java.io.IOException;

/**
 * Filter implementation for random access.
 * 
 * By default all methods are delegated to the underlying {@link IRandomAccess}.
 * 
 */
public class RandomAccessFilter extends AbstractRandomAccess {

	protected final IRandomAccess randomAccess;

	private final byte[] oneByte = new byte[1];

	public RandomAccessFilter(IRandomAccess randomAccess) {
		super();
		this.randomAccess = randomAccess;
	}

	@Override
	public void close() throws IOException {
		this.flush();
		randomAccess.close();
	}

	@Override
	public void flush() throws IOException {
		randomAccess.flush();
	}

	@Override
	public long getLength() throws IOException {
		return randomAccess.getLength();
	}

	@Override
	public long getOffset() throws IOException {
		return randomAccess.getOffset();
	}

	@Override
	public boolean isReadOnly() {
		return randomAccess.isReadOnly();
	}

	@Override
	public int read() throws IOException {
		int length = read(oneByte, 0, 1);
		if (length == -1) {
			return -1;
		}
		return oneByte[0] & 0xff;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		return randomAccess.read(buffer, start, numBytes);
	}

	@Override
	public void seek(long offset) throws IOException {
		randomAccess.seek(offset);
	}

	@Override
	public void seekBy(long delta) throws IOException {
		randomAccess.seekBy(delta);
	}

	@Override
	public void setLength(long newLength) throws IOException {
		randomAccess.setLength(newLength);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int start, int numBytes) throws IOException {
		randomAccess.write(buffer, start, numBytes);
	}

	@Override
	public void write(int b) throws IOException {
		oneByte[0] = (byte) b;
		write(oneByte, 0, 1);
	}
}
