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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A "non persistent" {@link ILocator}. This is used as a placeholder where an
 * {@link ILocator} is necessary but no real physical data representation is yet
 * defined / available.
 * 
 */
public class TransientLocator extends CommonLocator {
	private boolean alwaysUnchanged;

	private String localName;

	private String type;

	public TransientLocator(String name, String type) {
		if ((name == null) || (type == null)) {
			throw new NullPointerException(
					"name and type may not be null for TransientLocator");
		}
		this.localName = name;
		this.type = canonical(type);
	}

	private String canonical(String name) {
		return name.trim().toLowerCase();
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
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		return localName + "." + type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		throw new FileNotFoundException("transient locator");
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
		throw new FileNotFoundException("transient locator");
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
	 * @see de.intarsys.tools.locator.ILocator#getRandomAccess()
	 */
	public IRandomAccess getRandomAccess() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
	public Reader getReader(String encoding) throws IOException {
		throw new FileNotFoundException("transient locator");
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
		return localName + "." + type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	public Writer getWriter() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	public Writer getWriter(String encoding) throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	public boolean isAlwaysUnchanged() {
		return alwaysUnchanged;
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
	 * @see de.intarsys.tools.locator.ILocator#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return true;
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
	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return new ILocator[0];
	}

	@Override
	public void rename(String newName) throws IOException {
		localName = FileTools.getBaseName(newName);
		type = FileTools.getExtension(newName);
	}

	public void setAlwaysUnchanged(boolean paramAlwaysUnchanged) {
		alwaysUnchanged = paramAlwaysUnchanged;
	}

	public void setLocalName(String name) {
		this.localName = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	public void synch() {
		// no op
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
