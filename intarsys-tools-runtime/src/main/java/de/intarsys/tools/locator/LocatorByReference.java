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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.adapter.IAdapterSupport;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * This {@link ILocator} serves as an adapter for an {@link ILocatorSupport}
 * instance.
 */
public class LocatorByReference extends AbstractLocator implements IAdapterSupport {

	private ILocatorSupport locatorSupport;

	public LocatorByReference(ILocatorSupport locatorSupport) {
		super();
		this.locatorSupport = locatorSupport;
	}

	@Override
	public void delete() throws IOException {
		getLocator().delete();
	}

	@Override
	public boolean exists() {
		return getLocator().exists();
	}

	@Override
	public <T> T getAdapter(Class<T> clazz) {
		return AdapterTools.getAdapter(getLocator(), clazz, null);
	}

	@Override
	public ILocator getChild(String name) {
		return getLocator().getChild(name);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getLocator().getInputStream();
	}

	@Override
	public long getLength() throws IOException {
		return getLocator().getLength();
	}

	protected ILocator getLocator() {
		return locatorSupport.getLocator();
	}

	public ILocatorSupport getLocatorSupport() {
		return locatorSupport;
	}

	@Override
	public String getName() {
		return getLocator().getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return getLocator().getOutputStream();
	}

	@Override
	public ILocator getParent() {
		return getLocator().getParent();
	}

	@Override
	public String getPath() {
		return getLocator().getPath();
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		return getLocator().getRandomAccess();
	}

	@Override
	public Reader getReader() throws IOException {
		return getLocator().getReader();
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		return getLocator().getReader(encoding);
	}

	@Override
	public Writer getWriter() throws IOException {
		return getLocator().getWriter();
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		return getLocator().getWriter(encoding);
	}

	@Override
	public boolean isDirectory() {
		return getLocator().isDirectory();
	}

	@Override
	public boolean isOutOfSynch() {
		return getLocator().isOutOfSynch();
	}

	@Override
	public boolean isReadOnly() {
		return getLocator().isReadOnly();
	}

	@Override
	public ILocator[] listLocators(ILocatorNameFilter filter) throws IOException {
		return getLocator().listLocators(filter);
	}

	@Override
	public void rename(String newName) throws IOException {
		getLocator().rename(newName);
	}

	@Override
	public void setReadOnly() {
		getLocator().setReadOnly();
	}

	@Override
	public void synch() {
		getLocator().synch();
	}

	@Override
	public URI toURI() {
		return getLocator().toURI();
	}
}
