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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;

import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.string.StringTools;

/**
 * An adapter from a {@link String} to {@link ILocator}.
 * 
 */
public class StringLocator extends CommonLocator implements ICharsetAccess {
	private final String content;

	private String charset = System.getProperty("file.encoding");

	private String fullName;

	/**
	 * Create a {@link StringLocator} with a full name.
	 * 
	 * The name may contain path segments and extensions.
	 * 
	 * @param content
	 * @param pFullName
	 */
	public StringLocator(String content, String pFullName) {
		super();
		this.content = content;
		setFullName(pFullName);
	}

	/**
	 * Create a {@link StringLocator} with a name and an extension.
	 * 
	 * @param content
	 * @param pName
	 * @param pType
	 */
	public StringLocator(String content, String pName, String pType) {
		super();
		this.content = content;
		if (StringTools.isEmpty(pType)) {
			setFullName(pName);
		} else {
			setFullName(pName + "." + pType);
		}
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	@Override
	public boolean exists() {
		return getContent() != null;
	}

	protected byte[] getBytes() {
		byte[] bytes;
		try {
			bytes = getContent().getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			bytes = getContent().getBytes();
		}
		return bytes;
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public ILocator getChild(String child) {
		return null;
	}

	public String getContent() {
		return content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		byte[] bytes = getBytes();
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public long getLength() throws IOException {
		return getBytes().length;
	}

	@Override
	public String getName() {
		return PathTools.getName(fullName);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("locator is read only");
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
		byte[] bytes = getBytes();
		return new RandomAccessByteArray(bytes);
	}

	@Override
	public Reader getReader() throws IOException {
		return new StringReader(getContent());
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		return new StringReader(getContent());
	}

	@Override
	public Writer getWriter() throws IOException {
		throw new IOException("locator is read only");
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		throw new IOException("locator is read only");
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
		return true;
	}

	public boolean isSynchSynchronous() {
		return false;
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
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
		return URI.create("string:[a]" + getContent());
	}

}
