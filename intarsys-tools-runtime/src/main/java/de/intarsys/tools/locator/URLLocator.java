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
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link ILocator} for URL-based access.
 */
public class URLLocator extends CommonLocator {

	private final URL url;

	/** The encoding of the designated source */
	private String encoding;

	private ILocator tempFileLocator;

	public URLLocator(URL url) {
		super();
		this.url = url;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof URLLocator)) {
			return false;
		}
		return url.equals(((URLLocator) obj).getUrl()); // NOSONAR
	}

	@Override
	public boolean exists() {
		try {
			getUrl().openConnection();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public ILocator getChild(String childName) {
		return null;
	}

	protected String getEncoding() {
		return encoding;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getUrl().openStream();
	}

	@Override
	public String getName() {
		String path = getUrl().getPath();
		return PathTools.getName(path);
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
		return getUrl().toExternalForm();
	}

	@Override
	public synchronized IRandomAccess getRandomAccess() throws IOException {
		if (tempFileLocator == null) {
			tempFileLocator = createTempFileLocator();
		}
		return tempFileLocator.getRandomAccess();
	}

	@Override
	public Reader getReader() throws IOException {
		if (getEncoding() == null) {
			return new InputStreamReader(getInputStream());
		} else {
			return new InputStreamReader(getInputStream(), getEncoding());
		}
	}

	@Override
	public Reader getReader(String newEncoding) throws IOException {
		if (StringTools.isEmpty(newEncoding)) {
			return getReader();
		} else {
			return new InputStreamReader(getInputStream(), newEncoding);
		}
	}

	public URL getUrl() {
		return url;
	}

	@Override
	public Writer getWriter() throws IOException {
		throw new IOException("locator is read only");
	}

	@Override
	public Writer getWriter(String pEncoding) throws IOException {
		throw new IOException("locator is read only");
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode(); // NOSONAR
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

	protected void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void synch() {
		// do nothing
	}

	@Override
	public String toString() {
		return getUrl().toExternalForm();
	}

	@Override
	public URI toURI() {
		try {
			return getUrl().toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
