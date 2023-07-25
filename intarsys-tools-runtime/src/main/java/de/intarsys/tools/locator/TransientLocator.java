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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

/**
 * A "non persistent" {@link ILocator}. This is used as a placeholder where an
 * {@link ILocator} is necessary but no real physical data representation is yet
 * defined / available.
 * 
 */
public class TransientLocator extends CommonLocator {

	private boolean alwaysUnchanged;

	private String fullname;

	public TransientLocator(String pFullName) {
		super();
		setFullName(pFullName);
	}

	public TransientLocator(String name, String type) {
		if ((name == null) || (type == null)) {
			throw new NullPointerException("name and type may not be null for TransientLocator");
		}
		if (StringTools.isEmpty(type)) {
			setFullName(name);
		} else {
			setFullName(name + "." + canonical(type));
		}
	}

	private String canonical(String name) {
		return name.trim().toLowerCase();
	}

	@Override
	public void delete() throws IOException {
		// nothing to do...
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ILocator getChild(String name) {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public String getName() {
		return PathTools.getName(fullname);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public ILocator getParent() {
		return null;
	}

	@Override
	public String getPath() {
		return fullname;
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public Reader getReader() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public Reader getReader(String encoding) throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public Writer getWriter() throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	@Override
	public Writer getWriter(String encoding) throws IOException {
		throw new FileNotFoundException("transient locator");
	}

	public boolean isAlwaysUnchanged() {
		return alwaysUnchanged;
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

	public void setAlwaysUnchanged(boolean paramAlwaysUnchanged) {
		alwaysUnchanged = paramAlwaysUnchanged;
	}

	public void setFullName(String newName) {
		this.fullname = newName;
	}

	@Override
	public void synch() {
		// no op
	}

	@Override
	public URI toURI() {
		return null;
	}

}
