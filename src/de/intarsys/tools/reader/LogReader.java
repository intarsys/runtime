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
import java.io.Writer;

/**
 * A reader that logs any char read.
 * <p>
 * The reader is piped in the reading process, any characters read are written
 * to an associated writer object.
 * 
 * <pre>
 * Client -&gt; LogReader -&gt; Reader -&gt; Source
 *            |
 *            v
 *           Writer
 * </pre>
 * 
 * todo make closing of associated writer configurable
 */
public class LogReader extends FilterReader {
	Writer log;

	public LogReader(Reader in, Writer w) {
		super(in);
		setLog(w);
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (getLog() != null) {
			getLog().close();
		}
	}

	public java.io.Writer getLog() {
		return log;
	}

	@Override
	public int read() throws java.io.IOException {
		int b = super.read();
		if ((b > -1) && (log != null)) {
			log.write(b);
		}
		return b;
	}

	/**
	 * Read characters into a portion of an array. This method will block until
	 * some input is available, an I/O error occurs, or the end of the stream is
	 * reached.
	 * 
	 * @param cbuf
	 *            Destination buffer
	 * @param off
	 *            Offset at which to start storing characters
	 * @param len
	 *            Maximum number of characters to read
	 * 
	 * @return The number of characters read, or -1 if the end of the stream has
	 *         been reached
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int bytes = super.read(cbuf, off, len);
		if ((bytes > -1) && (log != null)) {
			log.write(cbuf, off, bytes);
		}
		return bytes;
	}

	public void setLog(java.io.Writer newLog) {
		log = newLog;
	}
}
