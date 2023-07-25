/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.randomaccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.intarsys.tools.file.FileTools;

/**
 * Implements random access to a file.
 */
public class RandomAccessFile extends AbstractRandomAccess {

	/**
	 * The wrapped RandomAccessFile of the java library
	 */
	private java.io.RandomAccessFile fileAccess;

	/**
	 * Flag if this is used read only
	 */
	private boolean readOnly;

	/**
	 * @param file
	 *            to open for random access
	 * @throws FileNotFoundException
	 *             if file was not found or the file is locked by a different
	 *             process
	 */
	public RandomAccessFile(File file) throws IOException {
		this(file, true);
	}

	/**
	 * @param file
	 *            to open for random access
	 * @throws FileNotFoundException
	 *             if file was not found or the file is locked by a different
	 *             process
	 */
	public RandomAccessFile(File file, boolean create) throws IOException {
		if (create && !file.exists()) {
			File dir = file.getParentFile();
			FileTools.mkdirs(dir);
			file.createNewFile(); // NOSONAR
		}
		if (!file.exists()) {
			throw new FileNotFoundException("file does not exist or can't be created");
		}
		if (file.canWrite()) {
			try {
				fileAccess = new java.io.RandomAccessFile(file, "rw");
				return;
			} catch (IOException e) {
				// canWrite() doesn't check for user permissions
				// try again with readonly
			}
		}
		fileAccess = new java.io.RandomAccessFile(file, "r");
		readOnly = true;
	}

	@Override
	public void close() throws IOException {
		if (fileAccess != null) {
			fileAccess.close();
		}
	}

	@Override
	public void flush() throws IOException {
		fileAccess.getChannel().force(true);
	}

	@Override
	public long getLength() throws IOException {
		return fileAccess.length();
	}

	@Override
	public long getOffset() throws IOException {
		return fileAccess.getFilePointer();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public int read() throws IOException {
		return fileAccess.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return fileAccess.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int start, int numBytes) throws IOException {
		return fileAccess.read(buffer, start, numBytes);
	}

	@Override
	public void seek(long offset) throws IOException {
		fileAccess.seek(offset);
	}

	@Override
	public void seekBy(long delta) throws IOException {
		seek(fileAccess.getFilePointer() + delta);
	}

	@Override
	public void setLength(long newLength) throws IOException {
		fileAccess.setLength(newLength);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		fileAccess.write(buffer);
	}

	@Override
	public void write(byte[] buffer, int start, int numBytes) throws IOException {
		fileAccess.write(buffer, start, numBytes);
	}

	@Override
	public void write(int b) throws IOException {
		fileAccess.write(b);
	}
}
