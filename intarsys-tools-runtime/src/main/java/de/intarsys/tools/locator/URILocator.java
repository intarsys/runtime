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

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * An {@link ILocator} for URI-based link definition.
 * 
 * This implementation can not be used for physical access, but for reference
 * purposes only.
 */
public class URILocator extends CommonLocator {

	private final URI uri;

	public URILocator(URI uri) {
		super();
		this.uri = uri;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof URILocator)) {
			return false;
		}
		return uri.equals(((URILocator) obj).uri);
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ILocator getChild(String childName) {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public String getName() {
		return PathTools.getName(getURI().getPath());
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public ILocator getParent() {
		return null;
	}

	@Override
	public String getPath() {
		return getURI().getPath();
	}

	@Override
	public synchronized IRandomAccess getRandomAccess() throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public Reader getReader() throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public Reader getReader(String newEncoding) throws IOException {
		throw new IOException("unsupported");
	}

	public URI getURI() {
		return uri;
	}

	@Override
	public Writer getWriter() throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public Writer getWriter(String pEncoding) throws IOException {
		throw new IOException("unsupported");
	}

	@Override
	public int hashCode() {
		return getURI().hashCode();
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
	public ILocator[] listLocators(final ILocatorNameFilter filter) throws IOException {
		return new ILocator[0];
	}

	@Override
	public void synch() {
		// do nothing
	}

	@Override
	public String toString() {
		return getURI().toString();
	}

	@Override
	public URI toURI() {
		return getURI();
	}

}
