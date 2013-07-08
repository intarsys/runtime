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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that logs any char written.
 * <p>
 * The output stream is piped in the writing process, any characters written are
 * copied to an associated output stream object.
 * 
 * <pre>
 * Client -&gt; LogOutputStream -&gt; OutputStream -&gt; Destination
 *            |
 *            v
 *           OutputStream (Copy)
 * </pre>
 */
public class LogOutputStream extends FilterOutputStream {
	/**
	 * 
	 */
	private OutputStream log;

	/**
	 * SplitStream - Konstruktorkommentar.
	 * 
	 * @param newout
	 * 
	 * @param newlog
	 * 
	 */
	public LogOutputStream(OutputStream newout, OutputStream newlog) {
		super(newout);
		setLog(newlog);
	}

	@Override
	public void close() throws IOException {
		super.close();
		if ((log != null)) {
			log.close();
		}
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		if (log != null) {
			log.flush();
		}
	}

	public java.io.OutputStream getLog() {
		return log;
	}

	public void setLog(java.io.OutputStream newLog) {
		log = newLog;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		if (log != null) {
			log.write(b);
		}
	}
}
