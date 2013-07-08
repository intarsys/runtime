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
 * The console writer wraps System.out in a writer that is never closed
 * accidently.
 * 
 */
public class ConsoleWriter extends Writer {
	/**
	 * ConsoleWriter constructor comment.
	 */
	public ConsoleWriter() {
		super();
	}

	/**
	 * ConsoleWriter constructor comment.
	 * 
	 * @param lock
	 *            java.lang.Object
	 */
	protected ConsoleWriter(Object lock) {
		super(lock);
	}

	/**
	 * Close the stream, flushing it first. Once a stream has been closed,
	 * further write() or flush() invocations will cause an IOException to be
	 * thrown. Closing a previously-closed stream, however, has no effect.
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public void basicClose() throws IOException {
		System.out.close();
	}

	/**
	 * Close the stream, flushing it first. Once a stream has been closed,
	 * further write() or flush() invocations will cause an IOException to be
	 * thrown. Closing a previously-closed stream, however, has no effect.
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public void close() throws IOException {
		// you don't want to close the console
	}

	/**
	 * Flush the stream. If the stream has saved any characters from the various
	 * write() methods in a buffer, write them immediately to their intended
	 * destination. Then, if that destination is another character or byte
	 * stream, flush it. Thus one flush() invocation will flush all the buffers
	 * in a chain of Writers and OutputStreams.
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public void flush() throws IOException {
		System.out.flush();
	}

	/**
	 * Write a portion of an array of characters.
	 * 
	 * @param cbuf
	 *            Array of characters
	 * @param off
	 *            Offset from which to start writing characters
	 * @param len
	 *            Number of characters to write
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
		System.out.print(String.copyValueOf(cbuf, off, len));
	}
}
