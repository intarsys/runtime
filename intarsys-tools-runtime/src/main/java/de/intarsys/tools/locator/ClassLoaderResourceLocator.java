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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.xfs.XFSScanner;

/**
 * An {@link ILocator} for resources accessible by a class loader.
 * 
 * The name is the fully qualified path to the resource, separated by "/",
 * without leading "/".
 * 
 * Example: "de/intarsys/tools/locator/resource.txt"
 */
public class ClassLoaderResourceLocator extends CommonLocator {

	private final ClassLoader classLoader;

	private final String resourceName;

	private URL resourceURL;

	/** The encoding of the designated source */
	private String encoding;

	private ILocator tempFileLocator;

	/**
	 * Create a {@link ClassLoaderResourceLocator} with the path to the
	 * resource.
	 * 
	 * @param classLoader
	 * @param path
	 */
	public ClassLoaderResourceLocator(ClassLoader classLoader, String path) {
		super();
		this.classLoader = classLoader;
		this.resourceName = path.replace("\\", "/");
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassLoaderResourceLocator)) {
			return false;
		}
		return resourceName.equals(((ClassLoaderResourceLocator) obj).resourceName)
				&& classLoader.equals(((ClassLoaderResourceLocator) obj).classLoader);
	}

	@Override
	public boolean exists() {
		return getClassLoader().getResource(getPath()) != null;
	}

	@Override
	public ILocator getChild(String childName) {
		StringBuilder sb = new StringBuilder(getPath());
		if (sb.length() > 0) {
			sb.append("/");
		}
		sb.append(childName);
		ClassLoaderResourceLocator result = new ClassLoaderResourceLocator(getClassLoader(), sb.toString());
		return result;
	}

	/**
	 * The {@link ClassLoader} used to access the resource.
	 * 
	 * @return The {@link ClassLoader} used to access the resource.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	protected String getEncoding() {
		return encoding;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream is = getClassLoader().getResourceAsStream(getPath());
		if (is == null) {
			throw new FileNotFoundException("resource '" + getName() + "' not found");
		}
		return is;
	}

	@Override
	public long getLength() throws IOException {
		return getRandomAccess().getLength();
	}

	@Override
	public String getName() {
		if (resourceName == null) {
			return "unknown";
		} else {
			return PathTools.getName(resourceName);
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throwReadOnly();
		return null;
	}

	@Override
	public ILocator getParent() {
		int index = getPath().lastIndexOf("/");
		if (index > -1) {
			String parentname = getPath().substring(0, index);
			return new ClassLoaderResourceLocator(getClassLoader(), parentname);
		} else if ("".equals(getPath())) {
			return null;
		} else {
			return new ClassLoaderResourceLocator(getClassLoader(), "");
		}
	}

	@Override
	public String getPath() {
		return getResourceName();
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

	/**
	 * The resource name defined when constructing this.
	 * 
	 * @return The resource name defined when constructing this.
	 */
	protected String getResourceName() {
		return resourceName;
	}

	protected URL getResourceURL() {
		if (resourceURL == null) {
			resourceURL = getClassLoader().getResource(getPath());
		}
		return resourceURL;
	}

	@Override
	public Writer getWriter() throws IOException {
		throwReadOnly();
		return null;
	}

	@Override
	public Writer getWriter(String pEncoding) throws IOException {
		throwReadOnly();
		return null;
	}

	@Override
	public int hashCode() {
		return resourceName.hashCode();
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
		List<ILocator> result = new ArrayList<>();
		// try to use x-fs
		XFSScanner scanner = new XFSScanner(getClassLoader(), getPath(), null);
		scanner.scan(found -> {
			if (filter == null || filter.accept(this, found.getName())) {
				result.add(getChild(found.getName()));
			}
		});
		return result.toArray(new ILocator[result.size()]);
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
		URL url = getResourceURL();
		return url == null ? "<not found>" : url.toString() + "#" + getPath();
	}

	@Override
	public URI toURI() {
		return null;
	}

}
