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

import java.io.IOException;

/**
 * An output stream killing its data.
 * 
 */
public class NullOutputStream extends java.io.OutputStream {
	/**
	 * NullOutputStream constructor comment.
	 */
	public NullOutputStream() {
		super();
	}

	@Override
	public void write(byte[] b) throws IOException {
		// ignore character
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// ignore
	}

	/**
	 * Writes the specified byte to this output stream. The general contract for
	 * <code>write</code> is that one byte is written to the output stream.
	 * The byte to be written is the eight low-order bits of the argument
	 * <code>b</code>. The 24 high-order bits of <code>b</code> are
	 * ignored.
	 * 
	 * <p>
	 * Subclasses of <code>OutputStream</code> must provide an implementation
	 * for this method.
	 * </p>
	 * 
	 * @param b
	 *            the <code>byte</code>.
	 * 
	 * @exception IOException
	 *                if an I/O error occurs. In particular, an
	 *                <code>IOException</code> may be thrown if the output
	 *                stream has been closed.
	 */
	@Override
	public void write(int b) throws IOException {
		// ignore
	}
}
