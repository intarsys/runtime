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
package de.intarsys.tools.xml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A reader that encodes literal line breaks to html tags.
 * <p>
 * "\r" are ignored, "\n" are encoded.
 * 
 */
public class HTMLEncodeLineBreaks extends FilterReader {
	/**
	 * A local buffer for reading escaped character sequences
	 */
	private StringReader buffer = null;

	public HTMLEncodeLineBreaks(Reader in) {
		super(in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#read()
	 */
	@Override
	public int read() throws IOException {
		int c;
		if (buffer != null) {
			c = buffer.read();
			if (c == -1) {
				buffer = null;
			} else {
				return c;
			}
		}
		c = super.read();
		if (c == '\n') {
			buffer = new StringReader("<br>");
		} else if (c == '\r') {
			return read();
		} else {
			return c;
		}
		return buffer.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int result = -1;
		for (int i = off; i < (off + len);) {
			int c = read();
			if (c == -1) {
				return result;
			}
			cbuf[i++] = (char) c;
			result = i - off;
		}
		return len;
	}
}
