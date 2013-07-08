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

import de.intarsys.tools.string.StringTools;

/**
 * A stream object dumping its data in hex format.
 * 
 */
public class HexDumpStream extends FilterOutputStream {
	private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static int BYTES_PER_LINE = 16;

	private int tbc = 0;

	private int lbc = 0;

	private final char[] line = new char[BYTES_PER_LINE];

	/**
	 * Creates a new HexOutputStream.
	 * 
	 * @param out
	 *            The input stream.
	 */
	public HexDumpStream(OutputStream out) {
		super(out);
	}

	@Override
	public void flush() throws IOException {
		String tbcString = Long.toHexString(tbc);
		int i;
		for (i = tbcString.length(); i < 8;) {
			out.write('0');
			i++;
		}
		out.write(tbcString.getBytes());
		out.write(' ');
		for (i = 0; i < lbc;) {
			out.write(hex[line[i] >> 4 & 0xF]);
			out.write(hex[line[i] >> 0 & 0xF]);
			out.write(' ');
			i++;
		}
		for (; i < BYTES_PER_LINE;) {
			out.write(' ');
			out.write(' ');
			out.write(' ');
			i++;
		}
		out.write(' ');
		for (i = 0; i < lbc;) {
			if (Character.isISOControl(line[i])) {
				out.write('.');
			} else {
				out.write(line[i]);
			}
			i++;
		}
		out.write(StringTools.LS.getBytes());
		tbc += lbc;
		lbc = 0;
		super.flush();
	}

	@Override
	public synchronized void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len)
			throws IOException {
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

	@Override
	public synchronized void write(int b) throws IOException {
		line[lbc++] = (char) b;
		if (lbc >= BYTES_PER_LINE) {
			flush();
		}
	}
}
