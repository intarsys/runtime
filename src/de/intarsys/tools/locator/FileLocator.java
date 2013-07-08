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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import de.intarsys.tools.charset.ICharsetAccess;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.locking.ILock;
import de.intarsys.tools.locking.ILockLevel;
import de.intarsys.tools.locking.ILockSupport;
import de.intarsys.tools.logging.LogTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessFile;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.stream.TempFileOutputStream;
import de.intarsys.tools.string.StringTools;

/**
 * File based implementation of {@link ILocator}.
 */
public class FileLocator extends CommonLocator implements ILockSupport,
		ICharsetAccess {

	class FileLocatorLock implements ILock {

		private java.io.RandomAccessFile ra;

		@Override
		public boolean acquire(Object owner, ILockLevel level) {
			try {
				ra = new java.io.RandomAccessFile(getFile(), "r");
				return true;
			} catch (FileNotFoundException e) {
				return false;
			}
		}

		@Override
		public void release(Object owner) {
			StreamTools.close(ra);
		}

	}

	/**
	 * The file referenced by the locator
	 */
	private File file;

	private String charset;

	/**
	 * Flag if the resource is out of synch
	 */
	private boolean outOfSynch;

	/**
	 * flag if we synchronize synchronously with every check
	 */
	private boolean synchSynchronous = false;

	/**
	 * Timestamp of last modification to the resource.
	 */
	private long lastModified = 0;

	/**
	 * flag if output is created initially in a temporary file
	 */
	private boolean useTempFile = false;

	private File canonicalFile;

	final private boolean append;

	public FileLocator(File file) {
		super();
		this.file = file;
		this.append = false;
	}

	public FileLocator(File file, boolean append) {
		super();
		this.file = file;
		this.append = append;
	}

	public FileLocator(String path) {
		this(new File(path));
	}

	@Override
	public void delete() throws IOException {
		if (getFile() == null) {
			return;
		}
		getFile().delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileLocator)) {
			return false;
		}
		return getCanonicalFile()
				.equals(((FileLocator) obj).getCanonicalFile());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#exists()
	 */
	@Override
	public boolean exists() {
		return getFile().exists();
	}

	/**
	 * The canonical file represented by this.
	 * 
	 * @return The canonical file represented by this.
	 */
	public File getCanonicalFile() {
		if (canonicalFile == null) {
			try {
				canonicalFile = getFile().getCanonicalFile();
			} catch (IOException e) {
				canonicalFile = getFile();
			}
		}
		return canonicalFile;
	}

	@Override
	public String getCharset() {
		return charset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getChild(java.lang.String)
	 */
	@Override
	public ILocator getChild(String name) {
		File childfile = new File(getFile(), name);
		FileLocator result = new FileLocator(childfile);
		result.setSynchSynchronous(isSynchSynchronous());
		return result;
	}

	/**
	 * Answer the file represented by this.
	 * 
	 * @return The canonical file represented by this.
	 */
	public File getFile() {
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getFullName()
	 */
	@Override
	public String getFullName() {
		if (getFile() == null) {
			return "unknown";
		}
		return getFile().getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		// trigger timestamp reading
		getLastModified();
		return new FileInputStream(getFile());
	}

	/**
	 * Answer the timestamp of the last modification.
	 * 
	 * @return the timestamp of the last modification.
	 */
	protected long getLastModified() {
		if (lastModified == 0) {
			if (file.exists()) {
				lastModified = file.lastModified();
			}
		}
		return lastModified;
	}

	@Override
	public long getLength() throws IOException {
		return file.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getLocalName()
	 */
	@Override
	public String getLocalName() {
		if (getFile() == null) {
			return "unknown";
		}
		return FileTools.getBaseName(getFile());
	}

	@Override
	public ILock getLock() {
		return new FileLocatorLock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		// trigger timestamp reading
		getLastModified();
		File parentFile = FileTools.getParentFile(getFile());
		if (parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (isUseTempFile()) {
			return new TempFileOutputStream(getFile(), "tmp_", "." + getType());
		}
		return new FileOutputStream(getFile(), isAppend());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getParent()
	 */
	@Override
	public ILocator getParent() {
		File parentFile = FileTools.getParentFile(getFile());
		if (parentFile == null) {
			return null;
		}
		FileLocator result = new FileLocator(parentFile);
		result.setSynchSynchronous(isSynchSynchronous());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getRandomAccess()
	 */
	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (isUseTempFile()) {
			throw new UnsupportedOperationException(
					"no random access to temp file");
		}
		return new RandomAccessFile(getFile());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader()
	 */
	@Override
	public Reader getReader() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(getCharset())) {
			return new InputStreamReader(getInputStream());
		}
		return new InputStreamReader(getInputStream(), getCharset());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getReader(java.lang.String)
	 */
	@Override
	public Reader getReader(String charset) throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(charset)) {
			return getReader();
		}
		return new InputStreamReader(getInputStream(), charset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getType()
	 */
	@Override
	public String getType() {
		if (getFile() == null) {
			return "<unknown>";
		}
		return FileTools.getExtension(getFile());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getTypedName()
	 */
	@Override
	public String getTypedName() {
		if (getFile() == null) {
			return "unknown";
		}
		return getFile().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter()
	 */
	@Override
	public Writer getWriter() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(getCharset())) {
			return new OutputStreamWriter(getOutputStream());
		}
		return new OutputStreamWriter(getOutputStream(), getCharset());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#getWriter(java.lang.String)
	 */
	@Override
	public Writer getWriter(String charset) throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(charset)) {
			return getWriter();
		}
		return new OutputStreamWriter(getOutputStream(), charset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		try {
			return getFile().getCanonicalFile().hashCode();
		} catch (IOException e) {
			return 17;
		}
	}

	public boolean isAppend() {
		return append;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		return getFile().isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isOutOfSynch()
	 */
	@Override
	public synchronized boolean isOutOfSynch() {
		if (isSynchSynchronous()) {
			synch();
		}
		return outOfSynch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		if (super.isReadOnly()) {
			return true;
		}
		IRandomAccess r = null;
		try {
			if (!getFile().exists()) {
				if (getFile().createNewFile()) {
					getFile().delete();
					return false;
				}
				return true;
			}
			r = getRandomAccess();
			if (r == null || r.isReadOnly()) {
				return true;
			}
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			StreamTools.close(r);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isSynchSynchronous()
	 */
	public boolean isSynchSynchronous() {
		return synchSynchronous;
	}

	/**
	 * <code>true</code> if temp file should be used.
	 * 
	 * @return <code>true</code> if temp file should be used.
	 */
	public boolean isUseTempFile() {
		return useTempFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.locator.ILocator#listLocators(de.intarsys.tools.locator
	 * .ILocatorNameFilter)
	 */
	@Override
	public ILocator[] listLocators(final ILocatorNameFilter filter)
			throws IOException {
		if (!getFile().exists()) {
			throw new FileNotFoundException(getFile().getName() + " not found");
		}
		if (!getFile().isDirectory()) {
			throw new IOException(getFile().getName() + " not a directory");
		}
		File[] candidates;
		if (filter == null) {
			candidates = getFile().listFiles();
		} else {
			candidates = getFile().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String current) {
					return filter.accept(FileLocator.this, current);
				}
			});
		}
		if (candidates == null) {
			throw new IOException("error listing resources");
		}

		ILocator[] result = new ILocator[candidates.length];
		for (int i = 0; i < result.length; i++) {
			FileLocator l = new FileLocator(candidates[i].getPath());
			l.setSynchSynchronous(isSynchSynchronous());
			l.setCharset(getCharset());
			result[i] = l;
		}
		return result;
	}

	@Override
	public void rename(String newName) throws IOException {
		if (getFile() == null) {
			return;
		}
		File newFile = new File(newName);
		if (!newFile.isAbsolute()) {
			File parent = FileTools.getParentFile(getFile());
			if (parent == null) {
				newFile = new File(newName);
			} else {
				newFile = new File(parent, newName);
			}
		}
		FileTools.renameFile(getFile(), newFile);
		file = newFile;
		canonicalFile = null;
		lastModified = 0;
	}

	@Override
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param synchSynchronous
	 */
	public void setSynchSynchronous(boolean synchSynchronous) {
		this.synchSynchronous = synchSynchronous;
	}

	/**
	 * @param useTempFile
	 */
	public void setUseTempFile(boolean useTempFile) {
		this.useTempFile = useTempFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	@Override
	public synchronized void synch() {
		if (getFile() == null) {
			return;
		}
		if ((getLastModified() != getFile().lastModified())) {
			LogTools.getLogger(this.getClass()).log(Level.FINEST,
					"'" + getFullName() + "' out of synch!");
			outOfSynch = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFile().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.locator.ILocator#toURL()
	 */
	@Override
	public URL toURL() {
		try {
			return getFile().toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
