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
package de.intarsys.tools.reader;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An implementation of ILocationProvider. This class can be "piped" in a reader
 * stream to access the current "pointer" into the data.
 * 
 * <p>
 * A common pitfall is to read the ILocationProvider via a BufferedReader - this
 * will certainly not provide with a correct character location!
 * </p>
 */
public class LocationAwareReader extends FilterReader implements
		ILocationProvider {
	private boolean open = true;

	private int line = 1;

	private int column = 1;

	private int position = 0;

	public LocationAwareReader(Reader in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		open = false;
		in.close();
	}

	public void ensureOpen() throws IOException {
		if (!open) {
			throw new IOException("Stream closed");
		}
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			int i = in.read();
			if (i != -1) {
				position++;
				if (i == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
			}
			return i;
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		ensureOpen();
		if ((off < 0) || (off > cbuf.length) || (len < 0)
				|| ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else {
			if (len == 0) {
				return 0;
			}
		}
		int pos = off;
		int i;
		for (i = read(); (pos < (off + len)) && (i != -1);) {
			cbuf[pos] = (char) i;
			pos++;
			i = read();
		}
		if ((i == -1) && (pos == off)) {
			return -1;
		}
		return (pos - off);
	}

	/**
	 * @param column
	 *            The column to set.
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @param line
	 *            The line to set.
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Offset the current location to a user defined line and column.
	 * 
	 * @param line
	 *            The line we want the reader to accept as new location
	 * @param column
	 *            The column we want the reader to accept as new location
	 */
	public void setLocation(int line, int column) {
		this.line = line;
		this.column = column;
	}

	/**
	 * @param position
	 *            The position to set.
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public long skip(long n) throws IOException {
		if (n < 0) {
			throw new IOException("parameter is negative");
		}
		long current = 0;
		int ch = 0;
		for (; (current < n) && (ch != -1); ch = read(), ++current) {
			// do nothing
		}
		return current;
	}
}
