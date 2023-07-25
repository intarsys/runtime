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
 * Provide buffering for a wrapped {@link IRandomAccess}.
 * 
 */
public class BufferedRandomAccess extends AbstractRandomAccess {

	private static final int HIGH_BYTE = 0xff;

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

	/**
	 * The buffer for the delegates data
	 */
	protected final byte[] bytes;

	/**
	 * The relative index of the buffer's first byte in the wrapped
	 * {@link IRandomAccess}
	 */
	protected long bytesOffset;

	/**
	 * The number of valid bytes in the buffer
	 */
	protected int localLength;

	/**
	 * The index into the bytes buffer indicating the current byte position.
	 * 
	 * {@link #totalOffset} and {@link #localOffset} are always kept in sync
	 */
	protected int localOffset;

	/**
	 * The buffered random access delegate
	 */
	protected final IRandomAccess randomAccess;

	/**
	 * The total offset in the wrapped {@link IRandomAccess}
	 * 
	 * {@link #totalOffset} and {@link #localOffset} are always kept in sync
	 * 
	 */
	protected long totalOffset;

	/**
	 * Flag if this {@link IRandomAccess} is closed.
	 */
	private boolean closed;

	/**
	 * The length of the random access data
	 */
	protected long totalLength;

	/**
	 * Flag if data in the buffer is currently changed.
	 */
	protected boolean bufferChanged;

	public BufferedRandomAccess(IRandomAccess randomAccess) throws IOException {
		this(randomAccess, DEFAULT_BUFFER_SIZE);
	}

	public BufferedRandomAccess(IRandomAccess randomAccess, int bufferSize) throws IOException {
		this.randomAccess = randomAccess;
		this.bytes = new byte[bufferSize];
		computeTotalLengthBuffered();
	}

	protected boolean bufferCannotRead() {
		return localOffset < 0 || localOffset >= localLength;
	}

	protected boolean bufferCannotWrite() {
		return localOffset < 0 || localOffset > localLength || localOffset >= bytes.length;
	}

	protected void bufferRead() throws IOException {
		bufferWrite();
		bufferReset();
		randomAccess.seek(bytesOffset);
		localLength = randomAccess.read(bytes, 0, bytes.length);
	}

	protected void bufferReset() {
		bytesOffset = computeBytesOffset(totalOffset);
		localOffset = (int) (totalOffset - bytesOffset);
		localLength = 0;
	}

	protected void bufferWrite() throws IOException {
		if (bufferChanged && (localLength > 0)) {
			randomAccess.seek(bytesOffset);
			randomAccess.write(bytes, 0, localLength);
		}
		bufferChanged = false;
	}

	protected void checkOpen() throws IOException {
		if (isClosed()) {
			throw new IOException("random access closed");
		}
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) {
			return;
		}
		bufferWrite();
		randomAccess.close();
		this.closed = true;
	}

	protected long computeBytesOffset(long pOffset) {
		return pOffset;
	}

	protected void computeTotalLengthBuffered() throws IOException {
		this.totalLength = randomAccess.getLength();
	}

	protected void computeTotalLengthWrapped() throws IOException {
		randomAccess.setLength(totalLength);
	}

	@Override
	public void flush() throws IOException {
		checkOpen();
		bufferWrite();
		randomAccess.flush();
	}

	protected byte[] getBytes() {
		return bytes;
	}

	protected long getBytesOffset() {
		return bytesOffset;
	}

	@Override
	public long getLength() throws IOException {
		checkOpen();
		return totalLength;
	}

	protected int getLocalLength() {
		return localLength;
	}

	protected int getLocalOffset() {
		return localOffset;
	}

	@Override
	public long getOffset() throws IOException {
		checkOpen();
		return totalOffset;
	}

	public IRandomAccess getRandomAccess() {
		return randomAccess;
	}

	protected long getTotalOffset() {
		return totalOffset;
	}

	protected boolean isClosed() {
		return closed;
	}

	@Override
	public boolean isReadOnly() {
		return randomAccess.isReadOnly();
	}

	@Override
	public int read() throws IOException {
		checkOpen();
		if (bufferCannotRead()) {
			if (totalOffset >= totalLength) {
				return -1;
			}
			bufferRead();
			if (bufferCannotRead()) {
				return -1;
			}
		}
		totalOffset++;
		return bytes[localOffset++] & HIGH_BYTE;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		checkOpen();
		if (numBytes == 0) {
			return 0;
		}
		int bytesRead = 0;
		int bytesToRead = numBytes;
		while (bytesRead < numBytes) {
			if (bufferCannotRead()) {
				if (totalOffset >= totalLength) {
					break;
				}
				bufferRead();
				if (bufferCannotRead()) {
					break;
				}
			}
			int tempCount = Math.min(localLength - localOffset, bytesToRead);
			System.arraycopy(bytes, localOffset, buffer, start + bytesRead, tempCount);
			localOffset += tempCount;
			totalOffset += tempCount;
			bytesRead += tempCount;
			bytesToRead -= tempCount;
		}
		return (bytesRead == 0) ? -1 : bytesRead;
	}

	@Override
	public void seek(long offset) throws IOException {
		checkOpen();
		if (offset < 0) {
			throw new IOException("negative offset");
		}
		totalOffset = offset;
		localOffset = (int) (totalOffset - bytesOffset);
	}

	@Override
	public void seekBy(long delta) throws IOException {
		seek(totalOffset + delta);
	}

	@Override
	public void setLength(long newLength) throws IOException {
		checkOpen();
		if (newLength < totalOffset) {
			totalOffset = newLength;
			if (newLength < bytesOffset) {
				bytesOffset = computeBytesOffset(totalOffset);
				localLength = 0;
			} else {
				localLength = (int) Math.min(localLength, newLength - bytesOffset);
			}
			localOffset = (int) (totalOffset - bytesOffset);
		}
		totalLength = newLength;
		computeTotalLengthWrapped();
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int start, int numBytes) throws IOException {
		checkOpen();
		int bytesWritten = 0;
		int bytesToWrite = numBytes;
		while (bytesWritten < numBytes) {
			if (bufferCannotWrite()) {
				bufferWrite();
				bufferReset();
			}
			int tempCount = Math.min(bytesToWrite, bytes.length - localOffset);
			System.arraycopy(buffer, start + bytesWritten, bytes, localOffset, tempCount);
			writeMarkChanged(tempCount);
			bytesWritten += tempCount;
			bytesToWrite -= tempCount;
		}
	}

	@Override
	public void write(int b) throws IOException {
		checkOpen();
		if (bufferCannotWrite()) {
			bufferWrite();
			bufferReset();
		}
		bytes[localOffset] = (byte) b;
		writeMarkChanged(1);
	}

	protected void writeMarkChanged(int i) {
		bufferChanged = true;
		totalOffset += i;
		localOffset += i;
		if (totalOffset > totalLength) {
			totalLength = totalOffset;
		}
		if (localOffset >= localLength) {
			localLength = localOffset;
		}
	}
}
