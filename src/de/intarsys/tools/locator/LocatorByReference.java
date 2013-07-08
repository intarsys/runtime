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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * This {@link ILocator} serves as an adapter for an {@link ILocatorSupport}
 * instance.
 */
public class LocatorByReference implements ILocator, IAdapterSupport {

	private ILocatorSupport locatorSupport;

	public LocatorByReference(ILocatorSupport locatorSupport) {
		super();
		this.locatorSupport = locatorSupport;
	}

	public void delete() throws IOException {
		getLocator().delete();
	}

	public boolean exists() {
		return getLocator().exists();
	}

	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(getLocator(), clazz);
	}

	public ILocator getChild(String name) {
		return getLocator().getChild(name);
	}

	public String getFullName() {
		return getLocator().getFullName();
	}

	public InputStream getInputStream() throws IOException {
		return getLocator().getInputStream();
	}

	public long getLength() throws IOException {
		return getLocator().getLength();
	}

	public String getLocalName() {
		return getLocator().getLocalName();
	}

	protected ILocator getLocator() {
		return locatorSupport.getLocator();
	}

	public ILocatorSupport getLocatorSupport() {
		return locatorSupport;
	}

	public OutputStream getOutputStream() throws IOException {
		return getLocator().getOutputStream();
	}

	public ILocator getParent() {
		return getLocator().getParent();
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
		return getLocator().getType();
	}

	public String getTypedName() {
		return getLocator().getTypedName();
	}

	public Writer getWriter() throws IOException {
		return getLocator().getWriter();
	}

	public Writer getWriter(String encoding) throws IOException {
		return getLocator().getWriter(encoding);
	}

	public boolean isDirectory() {
		return getLocator().isDirectory();
	}

	public boolean isOutOfSynch() {
		return getLocator().isOutOfSynch();
	}

	public boolean isReadOnly() {
		return getLocator().isReadOnly();
	}

	public ILocator[] listLocators(ILocatorNameFilter filter)
			throws IOException {
		return getLocator().listLocators(filter);
	}

	public void rename(String newName) throws IOException {
		getLocator().rename(newName);
	}

	public void setReadOnly() {
		getLocator().setReadOnly();
	}

	public void synch() {
		getLocator().synch();
	}

	public URL toURL() {
		return getLocator().toURL();
	}
}
