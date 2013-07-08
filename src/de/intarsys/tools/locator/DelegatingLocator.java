/*
 * Copyright (c) 2008, intarsys consulting GmbH
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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * An adapter for implementing a delegation model on the {@link ILocator}
 * 
 */
abstract public class DelegatingLocator implements ILocator, IAdapterSupport {

	protected DelegatingLocator() {
		super();
	}

	public void delete() throws IOException {
		getLocator().delete();
	}

	public boolean exists() {
		try {
			return getLocator().exists();
		} catch (IOException e) {
			return false;
		}
	}

	public <T> T getAdapter(Class<T> clazz) {
		try {
			return AdapterTools.getAdapter(getLocator(), clazz);
		} catch (IOException e) {
			return null;
		}
	}

	public ILocator getChild(String name) {
		try {
			return getLocator().getChild(name);
		} catch (IOException e) {
			return null;
		}
	}

	public String getFullName() {
		try {
			return getLocator().getFullName();
		} catch (IOException e) {
			return "";
		}
	}

	public InputStream getInputStream() throws IOException {
		return getLocator().getInputStream();
	}

	public long getLength() throws IOException {
		return getLocator().getLength();
	}

	public String getLocalName() {
		try {
			return getLocator().getLocalName();
		} catch (IOException e) {
			return "";
		}
	}

	abstract protected ILocator getLocator() throws IOException;

	public OutputStream getOutputStream() throws IOException {
		return getLocator().getOutputStream();
	}

	public ILocator getParent() {
		try {
			return getLocator().getParent();
		} catch (IOException e) {
			return null;
		}
	}

	public IRandomAccess getRandomAccess() throws IOException {
		return getLocator().getRandomAccess();
	}

	public Reader getReader() throws IOException {
		return getLocator().getReader();
	}

	public Reader getReader(String encoding) throws IOException {
		return getLocator().getReader(encoding);
	}

	public String getType() {
		try {
			return getLocator().getType();
		} catch (IOException e) {
			return "";
		}
	}

	public String getTypedName() {
		try {
			return getLocator().getTypedName();
		} catch (IOException e) {
			return "";
		}
	}

	public Writer getWriter() throws IOException {
		return getLocator().getWriter();
	}

	public Writer getWriter(String encoding) throws IOException {
		return getLocator().getWriter(encoding);
	}

	public boolean isDirectory() {
		try {
			return getLocator().isDirectory();
		} catch (IOException e) {
			return false;
		}
	}

	public boolean isOutOfSynch() {
		try {
			return getLocator().isOutOfSynch();
		} catch (IOException e) {
			return false;
		}
	}

	public boolean isReadOnly() {
		try {
			return getLocator().isReadOnly();
		} catch (IOException e) {
			return true;
		}
	}

	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return getLocator().listLocators(filter);
	}

	public void rename(String newName) throws IOException {
		getLocator().rename(newName);
	}

	public void setReadOnly() {
		try {
			getLocator().setReadOnly();
		} catch (IOException e) {
			//
		}
	}

	public void synch() {
		try {
			getLocator().synch();
		} catch (IOException e) {
			//
		}
	}

	public URL toURL() {
		try {
			return getLocator().toURL();
		} catch (IOException e) {
			return null;
		}
	}
}
