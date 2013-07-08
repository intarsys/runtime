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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.intarsys.tools.file.FileTools;

/**
 * A stream wrapper that defers writing to the final destination until the
 * stream is closed. The output is written to a temporary file in the
 * destinations directory. When the stream is closed, the temp file is copied to
 * the destination and deleted.
 * 
 */
public class TempFileOutputStream extends OutputStream {
	/** The final destination for the output */
	private File destination;

	/** The temporary file to use. */
	private File tempFile;

	/** The temporary output stream */
	private FileOutputStream tempOutput;

	public TempFileOutputStream(File destination) throws IOException {
		this(destination, "tmp", "tmp");
	}

	public TempFileOutputStream(File destination, String prefix, String suffix)
			throws IOException {
		super();
		this.destination = destination;
		tempFile = File.createTempFile(prefix, suffix,
				destination.getParentFile());
		tempOutput = new FileOutputStream(tempFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		try {
			tempOutput.close();
			FileTools.copyBinaryFile(tempFile, destination);
		} finally {
			tempFile.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		tempOutput.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		tempOutput.write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		tempOutput.write(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		tempOutput.write(b);
	}
}
