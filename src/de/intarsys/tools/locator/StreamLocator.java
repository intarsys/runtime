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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * An adapter from {@link InputStream} to {@link ILocator}.
 * <p>
 * This one has different behavior as you can only request input or output
 * stream ONCE. No backing copy of the input / output stream is made by the
 * locator.
 */
public class StreamLocator extends CommonLocator {

	private boolean inputUsed = false;

	private boolean outputUsed = false;

	private final InputStream inputStream;

	private final OutputStream outputStream;

	private String type;

	private String localName;

	public StreamLocator(InputStream is, OutputStream os, String name,
			String type) {
		super();
		this.inputStream = is;
		this.outputStream = os;
		this.localName = name;
		this.type = type;
	}

	public StreamLocator(InputStream stream, String name, String type) {
		this(stream, null, name, type);
	}

	public StreamLocator(OutputStream stream, String name, String type) {
		this(null, stream, name, type);
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
		return inputStream != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String child) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		return getLocalName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		if (inputUsed) {
			throw new IOException("input already requested");
		}
		inputUsed = true;
		return inputStream;
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
	public OutputStream getOutputStream() throws IOException {
		if (outputUsed) {
			throw new IOException("output already requested");
		}
		outputUsed = true;
		return outputStream;
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
	public IRandomAccess getRandomAccess() throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		return new InputStreamReader(getInputStream());
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
		return new OutputStreamWriter(getOutputStream());
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

	@Override
	public boolean isReadOnly() {
		if (super.isReadOnly()) {
			return true;
		}
		return outputStream == null;
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
		localName = FileTools.getBaseName(newName);
		type = FileTools.getExtension(newName);
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
