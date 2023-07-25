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
 * The name is the path relative to the given clazz, separated by "/", without
 * leading "/".
 *
 * Example: new ClassResourceLocator(clazz, "resource.txt") <br>
 * Example: new ClassResourceLocator(clazz, "child/resource.txt") <br>
 */
public class ClassResourceLocator extends CommonLocator {

	public static final String SEPARATOR = "/"; //$NON-NLS-1$

	private final Class<?> clazz;

	private final String resourceName;

	private final String resolvedName;

	/** The encoding of the designated source */
	private String encoding;

	private ILocator tempFileLocator;

	private URL resourceURL;

	/**
	 * Create a {@link ClassResourceLocator} with the path to the resource.
	 *
	 * @param clazz
	 * @param path
	 */
	public ClassResourceLocator(Class<?> clazz, String path) {
		super();
		this.clazz = clazz;
		this.resourceName = path;
		this.resolvedName = resolveName(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassResourceLocator)) {
			return false;
		}
		return resolvedName.equals(((ClassResourceLocator) obj).resolvedName)
				&& clazz.equals(((ClassResourceLocator) obj).clazz);
	}

	@Override
	public boolean exists() {
		return getClazz().getResource(getResolvedName()) != null;
	}

	@Override
	public ILocator getChild(String childName) {
		StringBuilder sb = new StringBuilder(getResolvedName());
		if (sb.length() > 1) {
			sb.append(SEPARATOR);
		}
		sb.append(childName);
		ClassResourceLocator result = new ClassResourceLocator(getClazz(), sb.toString());
		return result;
	}

	/**
	 * The {@link Class} used to define this.
	 * <p>
	 * The resource will be accessed the "getResourceAsStream" of this
	 * {@link Class} object.
	 *
	 * @return The {@link Class} used to define this.
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	protected String getEncoding() {
		return encoding;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getClazz().getResourceAsStream(getResolvedName());
	}

	@Override
	public String getName() {
		if (resourceName == null) {
			return "unknown"; //$NON-NLS-1$
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
		int index = getResolvedName().lastIndexOf(SEPARATOR);
		if ("/".equals(getResolvedName())) {
			return null;
		} else if (index > -1) {
			String parentname = getResolvedName().substring(0, index);
			return new ClassResourceLocator(getClazz(), parentname);
		} else {
			return new ClassResourceLocator(getClazz(), "");
		}
	}

	@Override
	public String getPath() {
		URL localResourceURL = getResourceURL();
		if (localResourceURL != null) {
			String file = localResourceURL.getFile();
			if (file.length() > 0) {
				return file;
			}
		}
		// as good as any
		return getResolvedName();
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
		if ((newEncoding == null) || newEncoding.equals(StringTools.EMPTY)) {
			return getReader();
		} else {
			return new InputStreamReader(getInputStream(), newEncoding);
		}
	}

	protected String getResolvedName() {
		return resolvedName;
	}

	/**
	 * The resource name used when constructing this.
	 *
	 * @return The resource name used when constructing this.
	 */
	protected String getResourceName() {
		return resourceName;
	}

	protected URL getResourceURL() {
		if (resourceURL == null) {
			resourceURL = getClass().getResource(getResolvedName());
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
		return resolvedName.hashCode();
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
		XFSScanner scanner = new XFSScanner(getClass().getClassLoader(), getResolvedName().substring(1), null);
		scanner.scan(found -> result.add(getChild(found.getName())));
		return result.toArray(new ILocator[result.size()]);
	}

	/**
	 * ..this is borrowed partially from java.lang.Class.
	 *
	 * @param pName
	 *            The name to resolve.
	 *
	 * @return The resolved name.
	 */
	private String resolveName(String pName) {
		if (pName == null) {
			return pName;
		}
		if (!pName.startsWith(SEPARATOR)) {
			Class<?> c = getClazz();
			while (c.isArray()) {
				c = c.getComponentType();
			}
			String baseName = c.getName();
			int index = baseName.lastIndexOf('.');
			if (index != -1) {
				pName = SEPARATOR + baseName.substring(0, index).replace('.', '/') + SEPARATOR + pName;
			} else {
				pName = SEPARATOR + pName;
			}
		}
		return pName;
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
		return url == null ? "<not found>#" + getResolvedName() : url.toString() + "#" + getResolvedName();
	}

	@Override
	public URI toURI() {
		return null;
	}

}
