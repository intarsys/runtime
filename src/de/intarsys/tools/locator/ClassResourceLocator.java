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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

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

	private final String name;

	final private String resolvedName;

	/** The encoding of the designated source */
	private String encoding;

	private ILocator tempFileLocator;

	public ClassResourceLocator(Class<?> clazz, String name) {
		super();
		this.clazz = clazz;
		this.name = name;
		this.resolvedName = resolveName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassResourceLocator)) {
			return false;
		}
		return resolvedName.equals(((ClassResourceLocator) obj).resolvedName)
				&& clazz.equals(((ClassResourceLocator) obj).clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#exists()
	 */
	public boolean exists() {
		return getClazz().getResource(getResolvedName()) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	public ILocator getChild(String childName) {
		String child = getResolvedName() + SEPARATOR + childName;
		ClassResourceLocator result = new ClassResourceLocator(getClazz(),
				child);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	public String getFullName() {
		return getResolvedName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return getClazz().getResourceAsStream(getResolvedName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getLocalName()
	 */
	public String getLocalName() {
		if (getName() == null) {
			return "unknown"; //$NON-NLS-1$
		} else {
			return FileTools.getBaseName(getName());
		}
	}

	/**
	 * The resource name used when constructing this.
	 * 
	 * @return The resource name used when constructing this.
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throwReadOnly();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getParent()
	 */
	public ILocator getParent() {
		int index = getResolvedName().lastIndexOf(SEPARATOR);
		if (index > -1) {
			String parentname = getResolvedName().substring(0, index);
			return new ClassResourceLocator(getClazz(), parentname);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getRandomAccessData()
	 */
	synchronized public IRandomAccess getRandomAccess() throws IOException {
		if (tempFileLocator == null) {
			tempFileLocator = createTempFileLocator();
		}
		return tempFileLocator.getRandomAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	public Reader getReader() throws IOException {
		if (getEncoding() == null) {
			return new InputStreamReader(getInputStream());
		} else {
			return new InputStreamReader(getInputStream(), getEncoding());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getType()
	 */
	public String getType() {
		if (getName() == null) {
			return "<unknown>"; //$NON-NLS-1$
		}
		return FileTools.getExtension(new File(getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getTypedName()
	 */
	public String getTypedName() {
		if (getName() == null) {
			return "<unknown>"; //$NON-NLS-1$
		}
		return new File(getName()).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	public Writer getWriter() throws IOException {
		throwReadOnly();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	public Writer getWriter(String pEncoding) throws IOException {
		throwReadOnly();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return resolvedName.hashCode();
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
	public ILocator[] listLocators(final ILocatorNameFilter filter)
			throws IOException {
		return new ILocator[0];
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
				pName = SEPARATOR
						+ baseName.substring(0, index).replace('.', '/')
						+ SEPARATOR + pName;
			}
		}
		return pName;
	}

	protected void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	public void synch() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getResolvedName();
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
