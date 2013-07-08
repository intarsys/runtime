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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A writer that logs any char written.
 * <p>
 * The writer is piped in the writing process, any characters written are copied
 * to an associated writer object.
 * 
 * <pre>
 * Client -&gt; LogWriter -&gt; Writer -&gt; Destination
 *            |
 *            v
 *           Writer (Copy)
 * </pre>
 */
public class LogWriter extends FilterWriter {
	/**
	 * 
	 */
	private Writer log;

	/**
	 * EscapeWriter constructor comment.
	 * 
	 * @param o
	 *            java.io.Writer
	 * @param log
	 * 
	 */
	public LogWriter(Writer o, Writer log) {
		super(o);
		setLog(log);
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
		super.close();
		if (getLog() != null) {
			getLog().close();
		}
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
		super.flush();
		if (getLog() != null) {
			getLog().flush();
		}
	}

	public java.io.Writer getLog() {
		return log;
	}

	private void setLog(java.io.Writer newLog) {
		log = newLog;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		super.write(cbuf, off, len);
		if (getLog() != null) {
			getLog().write(cbuf, off, len);
		}
	}

	@Override
	public void write(int c) throws IOException {
		super.write(c);
		if (getLog() != null) {
			getLog().write(c);
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		super.write(str, off, len);
		if (getLog() != null) {
			getLog().write(str, off, len);
		}
	}
}
