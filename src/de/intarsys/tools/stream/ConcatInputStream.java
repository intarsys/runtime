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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A stream that reads from all its inputs until all are empty.
 * 
 */
public class ConcatInputStream extends InputStream {
	/**
	 * The list of all input streams that are read in succession.
	 */
	final private List inputs = new ArrayList();

	/**
	 * The input stream we are currently reading from
	 */
	private InputStream current = null;

	/**
	 * The currently active input stream.
	 */
	private int index = 0;

	/**
	 * 
	 */
	public ConcatInputStream() {
		super();
	}

	public void addInput(InputStream input) {
		inputs.add(input);
		if (current == null) {
			current = input;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		IOException ex = null;
		for (Iterator it = inputs.iterator(); it.hasNext();) {
			InputStream is = (InputStream) it.next();
			try {
				is.close();
			} catch (IOException e) {
				ex = e;
			}
		}
		super.close();
		if (ex != null) {
			throw ex;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if (current == null) {
			return -1;
		}
		int result = current.read();
		if (result == -1) {
			index++;
			if (index >= inputs.size()) {
				current = null;
			} else {
				current = (InputStream) inputs.get(index);
			}
			return read();
		} else {
			return result;
		}
	}
}
