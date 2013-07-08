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
import java.net.URL;

import de.intarsys.tools.charset.ICharsetAccess;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.string.StringTools;

/**
 * An adapter from a byte[] object to {@link ILocator}.
 */
public class ByteArrayLocator extends CommonLocator implements Serializable,
		ICharsetAccess {

	protected byte[] content;

	protected int length;

	private String fullName;

	private String localName;

	private String type;

	private String charset;

	public ByteArrayLocator(byte[] data, String pFullName) {
		if (data == null) {
			data = new byte[0];
		}
		this.content = data;
		this.length = data.length;
		setFullName(pFullName);
	}

	public ByteArrayLocator(byte[] data, String pName, String pType) {
		if (data == null) {
			data = new byte[0];
		}
		this.content = data;
		this.length = data.length;
		if (StringTools.isEmpty(pType)) {
			setFullName(pName);
		} else {
			setFullName(pName + "." + pType); //$NON-NLS-1$
		}
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#exists()
	 */
	public boolean exists() {
		return content != null;
	}

	public byte[] getBytes() {
		byte[] result = new byte[length];
		System.arraycopy(content, 0, result, 0, length);
		return result;
	}

	public String getCharset() {
		return charset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String name) {
		return null;
	}

	public byte[] getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		return fullName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	public InputStream getInputStream() {
		return new ByteArrayInputStream(content, 0, length);
	}

	@Override
	public long getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getLocalName()
	 */
	public String getLocalName() {
		return localName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				super.close();
				content = buf;
				length = count;
			}

			@Override
			public void flush() throws IOException {
				super.flush();
				content = buf;
				length = count;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getParent()
	 */
	public ILocator getParent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getRandomAccessData()
	 */
	public IRandomAccess getRandomAccess() {
		return new RandomAccessByteArray(content, length) {
			@Override
			public void flush() throws IOException {
				super.flush();
				ByteArrayLocator.this.content = this.data;
				ByteArrayLocator.this.length = this.length;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		if (getCharset() == null) {
			return new InputStreamReader(getInputStream());
		} else {
			return new InputStreamReader(getInputStream(), getCharset());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
	public Reader getReader(String encoding) throws IOException {
		return new InputStreamReader(getInputStream(), encoding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getType()
	 */
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getTypedName()
	 */
	public String getTypedName() {
		return (type == null) ? localName : (localName + "." + type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	public Writer getWriter() throws IOException {
		if (getCharset() == null) {
			return new OutputStreamWriter(getOutputStream());
		} else {
			return new OutputStreamWriter(getOutputStream(), getCharset());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	public Writer getWriter(String encoding) throws IOException {
		return new OutputStreamWriter(getOutputStream(), encoding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isDirectory()
	 */
	public boolean isDirectory() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isOutOfSynch()
	 */
	public boolean isOutOfSynch() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isSynchSynchronous()
	 */
	public boolean isSynchSynchronous() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.locator.ILocator#listLocators(de.intarsys.tools.locator
	 * .ILocatorNameFilter)
	 */
	public ILocator[] listLocators(ILocatorNameFilter filter) {
		return new ILocator[0];
	}

	@Override
	public void rename(String newName) throws IOException {
		setFullName(newName);
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected void setFullName(String newName) {
		fullName = newName;
		localName = FileTools.getBaseName(newName);
		type = FileTools.getExtension(newName, null, getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	public void synch() {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#toURL()
	 */
	public URL toURL() {
		return null;
	}
}
