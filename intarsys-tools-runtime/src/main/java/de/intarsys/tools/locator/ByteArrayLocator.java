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
package de.intarsys.tools.locator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.string.StringTools;

/**
 * An adapter from a byte[] object to {@link ILocator}.
 */
public class ByteArrayLocator extends CommonLocator implements Serializable, ICharsetAccess {

	private byte[] content;

	private int length;

	private boolean inSync = true;

	private String fullName;

	private String charset;

	/**
	 * Create a {@link ByteArrayLocator} with a full name.
	 * 
	 * The name may contain path segments and extensions.
	 * 
	 * @param data
	 * @param pFullName
	 */
	public ByteArrayLocator(byte[] data, int length, String pFullName) {
		if (data == null) {
			data = new byte[length];
		}
		this.content = data;
		this.length = length;
		setFullName(pFullName);
	}

	/**
	 * Create a {@link ByteArrayLocator} with a full name.
	 * 
	 * The name may contain path segments and extensions.
	 * 
	 * @param data
	 * @param pFullName
	 */
	public ByteArrayLocator(byte[] data, String pFullName) {
		this(data, data == null ? 0 : data.length, pFullName);
	}

	/**
	 * Create a {@link ByteArrayLocator} with a name and an extension.
	 * 
	 * @param data
	 * @param pName
	 * @param pType
	 */
	public ByteArrayLocator(byte[] data, String pName, String pType) {
		this(data, StringTools.isEmpty(pType) ? pName : pName + '.' + pType);
	}

	/**
	 * Create a {@link ByteArrayLocator} with a full name.
	 * 
	 * The name may contain path segments and extensions.
	 * 
	 * @param pFullName
	 */
	public ByteArrayLocator(String pFullName) {
		this(null, 0, pFullName);
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	@Override
	public boolean exists() {
		return content != null;
	}

	public byte[] getBytes() {
		return Arrays.copyOf(content, length);
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public ILocator getChild(String name) {
		return null;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(content, 0, length);
	}

	@Override
	public long getLength() {
		return length;
	}

	@Override
	public String getName() {
		return PathTools.getName(fullName);
	}

	@Override
	public OutputStream getOutputStream() {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				super.close();
				setContent(buf, count);
			}

			@Override
			public void flush() throws IOException {
				super.flush();
				setContent(buf, count);
			}
		};
	}

	@Override
	public ILocator getParent() {
		return null;
	}

	@Override
	public String getPath() {
		return fullName;
	}

	@Override
	public IRandomAccess getRandomAccess() {
		return new RandomAccessByteArray(content, length) {
			@Override
			public void flush() throws IOException {
				super.flush();
				setContent(data, length);
			}
		};
	}

	@Override
	public Reader getReader() throws IOException {
		if (getCharset() == null) {
			return new InputStreamReader(getInputStream());
		} else {
			return new InputStreamReader(getInputStream(), getCharset());
		}
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		return new InputStreamReader(getInputStream(), encoding);
	}

	@Override
	public Writer getWriter() throws IOException {
		if (getCharset() == null) {
			return new OutputStreamWriter(getOutputStream());
		} else {
			return new OutputStreamWriter(getOutputStream(), getCharset());
		}
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		return new OutputStreamWriter(getOutputStream(), encoding);
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	/**
	 * Returns true if this locator's data has been modified by writing to one
	 * of its output streams or writers or its {@code IRandomAccess}.
	 *
	 * @see #synch()
	 */
	@Override
	public boolean isOutOfSynch() {
		return !inSync;
	}

	public boolean isSynchSynchronous() {
		return false;
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) {
		return new ILocator[0];
	}

	@Override
	public void rename(String newName) throws IOException {
		setFullName(newName);
	}

	@Override
	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected void setContent(byte[] content, int length) {
		this.content = content;
		this.length = length;
		this.inSync = false;
	}

	protected void setFullName(String newName) {
		fullName = newName;
	}

	/**
	 * Declares this locator to be in sync, that is, its data is unmodified.
	 *
	 * @see #isOutOfSynch()
	 */
	@Override
	public void synch() {
		inSync = true;
	}

	@Override
	public URI toURI() {
		return URI.create("bytes:[@]" + new String(Base64.encode(getBytes())));
	}

}
