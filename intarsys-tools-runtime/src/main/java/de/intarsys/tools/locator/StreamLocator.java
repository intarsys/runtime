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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;

import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

/**
 * An adapter from {@link InputStream} to {@link ILocator}.
 * <p>
 * This one has different behavior as you can only request input or output
 * stream ONCE. No backing copy of the input / output stream is made by the
 * locator.
 */
public class StreamLocator extends CommonLocator implements ICharsetAccess {

	private boolean inputUsed;

	private boolean outputUsed;

	private final InputStream inputStream;

	private final OutputStream outputStream;

	private String fullName;

	private String charset;

	/**
	 * Create a {@link StreamLocator} with input and output.
	 * 
	 * The input stream and output streams are directly forwarded to the
	 * respective API methods and can be used only once!
	 * 
	 * @param is
	 * @param os
	 * @param fullName
	 */
	public StreamLocator(InputStream is, OutputStream os, String fullName) {
		super();
		this.inputStream = is;
		this.outputStream = os;
		setFullName(fullName);
	}

	/**
	 * Create a {@link StreamLocator} with input and output.
	 * 
	 * The input stream and output streams are directly forwarded to the
	 * respective API methods and can be used only once!
	 * 
	 * @param is
	 * @param os
	 * @param name
	 * @param type
	 */
	public StreamLocator(InputStream is, OutputStream os, String name, String type) {
		this(is, os, StringTools.isEmpty(type) ? name : name + '.' + type);
	}

	public StreamLocator(InputStream stream, String fullname) {
		this(stream, (OutputStream) null, fullname);
	}

	public StreamLocator(InputStream stream, String name, String type) {
		this(stream, null, name, type);
	}

	public StreamLocator(OutputStream stream, String fullname) {
		this(null, stream, fullname);
	}

	public StreamLocator(OutputStream stream, String name, String type) {
		this(null, stream, name, type);
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	@Override
	public boolean exists() {
		return inputStream != null;
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public ILocator getChild(String child) {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inputUsed) {
			throw new IOException("input already requested");
		}
		inputUsed = true;
		return inputStream;
	}

	@Override
	public String getName() {
		return PathTools.getName(fullName);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (outputUsed) {
			throw new IOException("output already requested");
		}
		outputUsed = true;
		return outputStream;
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
	public IRandomAccess getRandomAccess() throws IOException {
		throw new UnsupportedOperationException();
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

	@Override
	public boolean isOutOfSynch() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		if (super.isReadOnly()) {
			return true;
		}
		return outputStream == null;
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

	protected void setFullName(String newName) {
		fullName = newName;
	}

	@Override
	public void synch() {
		//
	}

	@Override
	public URI toURI() {
		return null;
	}

}
