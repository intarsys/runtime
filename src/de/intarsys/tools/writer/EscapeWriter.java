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
package de.intarsys.tools.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * A writer that escapes predefined character sequences.
 * 
 */
public class EscapeWriter extends Writer {
	// the underlying writer object
	private Writer out;

	// the code to use for replacement
	private String destinationChars = "\\nrt";

	// the chars to replace
	private String sourceChars = "\\\n\r\t";

	/**
	 * EscapeWriter constructor comment.
	 * 
	 * @param o
	 *            java.io.Writer
	 */
	public EscapeWriter(Writer o) {
		super(o);
		setOut(o);
	}

	/**
	 * Close the stream, flushing it first. Once a stream has been closed,
	 * further write() or flush() invocations will cause an IOException to be
	 * thrown. Closing a previously-closed stream, however, has no effect.
	 * 
	 * @exception java.io.IOException
	 *                If an I/O error occurs
	 */
	@Override
	public void close() throws java.io.IOException {
		getOut().close();
	}

	/**
	 * Flush the stream. If the stream has saved any characters from the various
	 * write() methods in a buffer, write them immediately to their intended
	 * destination. Then, if that destination is another character or byte
	 * stream, flush it. Thus one flush() invocation will flush all the buffers
	 * in a chain of Writers and OutputStreams.
	 * 
	 * @exception java.io.IOException
	 *                If an I/O error occurs
	 */
	@Override
	public void flush() throws java.io.IOException {
		getOut().flush();
	}

	public java.lang.String getDestinationChars() {
		return destinationChars;
	}

	public java.io.Writer getOut() {
		return out;
	}

	public java.lang.String getSourceChars() {
		return sourceChars;
	}

	public void setDestinationChars(java.lang.String newDestinationChars) {
		destinationChars = newDestinationChars;
	}

	private void setOut(java.io.Writer newOut) {
		out = newOut;
	}

	public void setSourceChars(java.lang.String newSourceChars) {
		sourceChars = newSourceChars;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < (off + len); i++) {
			int index = getSourceChars().indexOf(cbuf[i]);
			if (index >= 0) {
				out.write('\\');
				out.write(getDestinationChars().charAt(index));
			} else {
				out.write(cbuf[i]);
			}
		}
	}
}
