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
 * Filter implementation for random access.
 * 
 */
public class RandomAccessFilter extends AbstractRandomAccess {
	protected IRandomAccess random;

	/**
	 * 
	 */
	public RandomAccessFilter(IRandomAccess random) {
		super();
		this.random = random;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#seek(long)
	 */
	public void seek(long offset) throws IOException {
		random.seek(offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#seekBy(long)
	 */
	public void seekBy(long delta) throws IOException {
		random.seekBy(delta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#read()
	 */
	public int read() throws IOException {
		return random.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#getOffset()
	 */
	public long getOffset() throws IOException {
		return random.getOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#getLength()
	 */
	public long getLength() throws IOException {
		return random.getLength();
	}

	protected IRandomAccess getRandom() {
		return random;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#setLength(long)
	 */
	public void setLength(long newLength) throws IOException {
		random.setLength(newLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#read(byte[])
	 */
	public int read(byte[] buffer) throws IOException {
		return random.read(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#read(byte[], int, int)
	 */
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		return random.read(buffer, start, numBytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#close()
	 */
	public void close() throws IOException {
		random.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#flush()
	 */
	public void flush() throws IOException {
		random.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#isReadOnly()
	 */
	public boolean isReadOnly() {
		return random.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#write(int)
	 */
	public void write(int b) throws IOException {
		random.write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#write(byte[])
	 */
	public void write(byte[] buffer) throws IOException {
		random.write(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.randomaccess.IRandomAccess#write(byte[], int, int)
	 */
	public void write(byte[] buffer, int start, int numBytes)
			throws IOException {
		random.write(buffer, start, numBytes);
	}
}
