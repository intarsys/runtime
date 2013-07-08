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
package de.intarsys.tools.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An {@link InputStream} that logs any char read.
 * <p>
 * This stream is piped in the reading process, any characters read are written
 * to an associated {@link OutputStream} object.
 * 
 * <pre>
 * Client -&gt; LogInputStream -&gt; InputStream -&gt; Source
 *            |
 *            v
 *           OutputStream
 * </pre>
 */
public class LogInputStream extends FilterInputStream {

	private OutputStream log;

	public LogInputStream(InputStream newin, OutputStream newlog) {
		super(newin);
		setLog(newlog);
	}

	@Override
	public void close() throws IOException {
		super.close();
		if ((log != null)) {
			log.close();
		}
	}

	public java.io.OutputStream getLog() {
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

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int bytes = super.read(b, off, len);
		if ((bytes > -1) && (log != null)) {
			log.write(b, off, bytes);
		}
		return bytes;
	}

	public void setLog(java.io.OutputStream newLog) {
		log = newLog;
	}
}
