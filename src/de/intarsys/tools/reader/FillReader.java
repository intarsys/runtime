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

import java.io.IOException;
import java.io.Reader;

/**
 * A Helper reader that repeatedly returns the characters in a predefined
 * pattern until it is closed.
 * 
 */
public class FillReader extends Reader {
	/**
	 * the pattern that is "read" all over.
	 */
	private String pattern;

	/**
	 * Flag if reader is closed.
	 */
	private boolean closed = false;

	/**
	 * The position within the string
	 */
	private int pos = 0;

	/**
	 * Create a FillReader on string.
	 * 
	 * @param pattern
	 *            The string that is read all over again.
	 */
	public FillReader(String pattern) {
		super();
		if (pattern == null) {
			throw new NullPointerException("pattern may not be null");
		}
		this.pattern = pattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#close()
	 */
	public void close() throws IOException {
		closed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < (off + len); i++) {
			int c = read();
			if (c == -1) {
				return off - i;
			} else {
				cbuf[i] = (char) c;
			}
		}
		return len;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#read()
	 */
	public int read() throws IOException {
		if (closed) {
			return -1;
		} else {
			if (pos >= pattern.length()) {
				if (pattern.length() == 0) {
					return -1;
				}
				pos = 0;
			}
			return pattern.charAt(pos++);
		}
	}
}
