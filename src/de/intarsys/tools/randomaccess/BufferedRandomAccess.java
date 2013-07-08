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
 * todo 1 length reply may be invalid
 */
public class BufferedRandomAccess extends AbstractRandomAccess {
	private static int DEFAULT_BUFFER_SIZE = 4096;

	/**
	 * The buffer for the delegates data
	 */
	private byte[] bytes;

	/**
	 * The relative index of the buffer's first byte in the delegates data
	 */
	private long bytesOffset = 0;

	/**
	 * The number of valid bytes in the buffer
	 */
	private int count = 0;

	/**
	 * The index into the buffer indicating the current byte
	 */
	private int localOffset = 0;

	/**
	 * The buffered random access delegate
	 */
	private IRandomAccess randomAccess;

	/**
	 * The total offset in the delegates data.
	 * <p>
	 * totalOffset = bufferOffset + localOffset; or count == 0 (invalid/empty
	 * buffer);
	 * 
	 */
	private long totalOffset = 0;

	private boolean closed = false;

	/**
	 * The length of the random access data
	 */
	private long length;

	/**
	 * Flag if data in the buffer is currently changed.
	 */
	private boolean bufferChanged = false;

	public BufferedRandomAccess(IRandomAccess randomAccess) throws IOException {
		this(randomAccess, DEFAULT_BUFFER_SIZE);
	}

	public BufferedRandomAccess(IRandomAccess randomAccess, int bufferSize)
			throws IOException {
		this.randomAccess = randomAccess;
		this.bytes = new byte[bufferSize];
		this.length = randomAccess.getLength();
	}

	protected int basicRead(byte[] buffer, int start, int numBytes)
			throws IOException {
		if (localOffset >= count) {
			if (numBytes >= bytes.length) {
				flushBuffer();
				randomAccess.seek(totalOffset);
				int readBytes = randomAccess.read(buffer, start, numBytes);
				if (readBytes > 0) {
					totalOffset += readBytes;
					localOffset = count;
				}
				return readBytes;
			} else {
				fillBuffer();
				if (localOffset >= count) {
					return -1;
				}
			}
		}
		int avail = count - localOffset;
		int cnt = (avail < numBytes) ? avail : numBytes;
		System.arraycopy(bytes, localOffset, buffer, start, cnt);
		localOffset += cnt;
		totalOffset += cnt;
		return cnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#close()
	 */
	public void close() throws IOException {
		if (isClosed()) {
			return;
		}
		flushBuffer();
		randomAccess.close();
		setClosed(true);
	}

	protected void fillBuffer() throws IOException {
		flushBuffer();
		randomAccess.seek(totalOffset);
		count = randomAccess.read(bytes, 0, bytes.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#flush()
	 */
	public void flush() throws IOException {
		if (isClosed()) {
			throw new IOException("random access closed");
		}
		flushBuffer();
		randomAccess.flush();
	}

	protected void flushBuffer() throws IOException {
		if (bufferChanged && (count > 0)) {
			randomAccess.seek(bytesOffset);
			randomAccess.write(bytes, 0, count);
		}
		bytesOffset = totalOffset;
		localOffset = 0;
		count = 0;
		bufferChanged = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#getLength()
	 */
	public long getLength() throws IOException {
		if (isClosed()) {
			throw new IOException("random access closed");
		}
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#getOffset()
	 */
	public long getOffset() throws IOException {
		if (isClosed()) {
			throw new IOException("random access closed");
		}
		return totalOffset;
	}

	protected boolean isClosed() {
		return closed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#isReadOnly()
	 */
	public boolean isReadOnly() {
		return randomAccess.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#read()
	 */
	public int read() throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		if (localOffset >= count) {
			fillBuffer();
			if (localOffset >= count) {
				return -1;
			}
		}
		totalOffset++;
		return bytes[localOffset++] & 0xff;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#read(byte[])
	 */
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#read(byte[], int,
	 * int)
	 */
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		if (numBytes == 0) {
			return 0;
		}
		int totalByteCount = 0;
		while (totalByteCount < numBytes) {
			int byteCount = basicRead(buffer, start + totalByteCount, numBytes
					- totalByteCount);
			if (byteCount <= 0) {
				break;
			}
			totalByteCount += byteCount;
		}
		if (totalByteCount == 0) {
			return -1;
		}
		return totalByteCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#seek(long)
	 */
	public void seek(long offset) throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		totalOffset = offset;
		long newLocalOffset = totalOffset - bytesOffset;
		if ((newLocalOffset < 0) || (newLocalOffset >= count)) {
			flushBuffer();
		} else {
			localOffset = (int) newLocalOffset;
		}
	}

	public void seekBy(long delta) throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		totalOffset += delta;
		long newLocalOffset = localOffset + delta;
		if ((newLocalOffset < 0) || (newLocalOffset >= count)) {
			flushBuffer();
		} else {
			localOffset = (int) newLocalOffset;
		}
	}

	protected void setClosed(boolean closed) {
		this.closed = closed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#setLength(int)
	 */
	public void setLength(long newLength) throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		if (newLength < (bytesOffset + bytes.length)) {
			flushBuffer();
		}
		if (newLength < totalOffset) {
			totalOffset = newLength;
			bytesOffset = newLength;
		}
		length = newLength;
		randomAccess.setLength(newLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#write(byte[])
	 */
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#write(byte[], int,
	 * int)
	 */
	public void write(byte[] buffer, int start, int numBytes)
			throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		if (numBytes >= bytes.length) {
			// greater than local buffer size -> write directly
			flushBuffer();
			randomAccess.seek(totalOffset);
			randomAccess.write(buffer, start, numBytes);
			totalOffset += numBytes;
			bytesOffset = totalOffset;
			if (totalOffset > length) {
				length = totalOffset;
			}
			return;
		}
		if (numBytes > (bytes.length - localOffset)) {
			// greater than rest of buffer -> flush and fill new one
			flushBuffer();
		}
		System.arraycopy(buffer, start, bytes, localOffset, numBytes);
		bufferChanged = true;
		totalOffset += numBytes;
		localOffset += numBytes;
		if (totalOffset > length) {
			length = totalOffset;
		}
		if (localOffset >= count) {
			count = localOffset;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccessData#write(int)
	 */
	public void write(int b) throws IOException {
		if (closed) {
			throw new IOException("random access closed");
		}
		if (localOffset >= bytes.length) {
			flushBuffer();
		}
		bufferChanged = true;
		if (localOffset == count) {
			count++;
		}
		totalOffset++;
		if (totalOffset > length) {
			length = totalOffset;
		}
		bytes[localOffset++] = (byte) b;
	}
}
