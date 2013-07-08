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
package de.intarsys.tools.randomaccess;

import java.io.IOException;

/**
 * A viewport in an existing {@link IRandomAccess}.
 * 
 */
public class RandomAccessViewport extends RandomAccessFilter {

	private final long viewOffset;

	private long viewLength;

	private long viewEnd;

	/**
	 * Create a viewport to the existing {@link IRandomAccess} <code>ra</code>.
	 * <p>
	 * ATTENTION: This will not seek to <code>offset</code> in <code>ra</code>.
	 * This means after creation before initial use you have to position the
	 * {@link IRandomAccess} yourself using <code>seek</code>.
	 * 
	 * @param ra
	 * @param offset
	 * @param length
	 * @throws IOException
	 */
	public RandomAccessViewport(IRandomAccess ra, long offset, long length)
			throws IOException {
		super(ra);
		this.viewLength = length;
		this.viewOffset = offset;
		this.viewEnd = viewOffset + viewLength;
	}

	@Override
	public long getLength() throws IOException {
		long realLength = getRandom().getLength();
		if (getViewLength() == -1) {
			return realLength - getViewOffset();
		}
		return Math.min(getViewLength(), realLength - getViewOffset());
	}

	@Override
	public long getOffset() throws IOException {
		return getRandom().getOffset() - getViewOffset();
	}

	protected long getViewEnd() {
		return viewEnd;
	}

	protected long getViewLength() {
		return viewLength;
	}

	protected long getViewOffset() {
		return viewOffset;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int read() throws IOException {
		if (getViewLength() != -1 && getOffset() >= getViewEnd()) {
			return -1;
		}
		return super.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		if (getViewLength() != -1 && getOffset() >= getViewEnd()) {
			return -1;
		}
		if (getViewLength() != -1) {
			numBytes = Math.min(numBytes, (int) (getViewEnd() - getOffset()));
		}
		return super.read(buffer, start, numBytes);
	}

	@Override
	public void seek(long offset) throws IOException {
		super.seek(getViewOffset() + offset);
	}

	@Override
	public void seekBy(long delta) throws IOException {
		super.seekBy(delta);
	}

	@Override
	public void setLength(long newLength) throws IOException {
		throw new IOException("not yet supported");
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		super.write(buffer);
	}

	@Override
	public void write(byte[] buffer, int start, int numBytes)
			throws IOException {
		super.write(buffer, start, numBytes);
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
	}

}
