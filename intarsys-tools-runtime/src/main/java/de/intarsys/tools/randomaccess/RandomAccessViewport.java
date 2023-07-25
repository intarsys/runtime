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
 * A viewport in an existing {@link IRandomAccess}.
 */
public class RandomAccessViewport extends AbstractRandomAccess {
	private final IRandomAccess randomAccess;
	private final long start;
	private final long length;

	/**
	 * Create a viewport to the existing {@link IRandomAccess} {@code ra}.
	 * <p>
	 * ATTENTION: This will not seek to {@code start} in {@code randomAccess}. This means after creation before initial
	 * use you have to position the {@link IRandomAccess} yourself using {@link #seek(long)}.
	 */
	public RandomAccessViewport(IRandomAccess randomAccess, long start, long length) {
		this.randomAccess = randomAccess;
		this.start = start;
		this.length = length == -1L
				? Long.MAX_VALUE
				: length;
	}

	@Override
	public void close() throws IOException {
		randomAccess.close();
	}

	@Override
	public void flush() throws IOException {
		randomAccess.flush();
	}

	@Override
	public long getLength() throws IOException {
		return Math.min(length, randomAccess.getLength() - start);
	}

	@Override
	public long getOffset() throws IOException {
		return randomAccess.getOffset() - start;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int read() throws IOException {
		long offset = getOffset();
		if (offset < 0L) {
			throw new IOException("Negative offset");
		}

		return offset < length
				? randomAccess.read()
				: -1;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		long offset = getOffset();
		if (offset < 0) {
			throw new IOException("Negative offset");
		}

		return offset < length
				? randomAccess.read(buffer, start, Math.min(numBytes, (int) (length - offset)))
				: -1;
	}

	@Override
	public void seek(long offset) throws IOException {
		if (offset < 0) {
			throw new IOException("Negative seek offset");
		}

		randomAccess.seek(start + offset);
	}

	@Override
	public void seekBy(long delta) throws IOException {
		long offset = getOffset();
		if (offset + delta < 0) {
			throw new IOException("Negative seek offset");
		}

		randomAccess.seekBy(delta);
	}

	@Override
	public void setLength(long length) throws IOException {
		throw new IOException("Length cannot be changed");
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		throw new IOException("Read-only random access");
	}

	@Override
	public void write(byte[] buffer, int start, int numBytes) throws IOException {
		throw new IOException("Read-only random access");
	}

	@Override
	public void write(int b) throws IOException {
		throw new IOException("Read-only random access");
	}
}