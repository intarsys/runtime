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
import java.net.URI;
import java.net.URISyntaxException;

import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.locking.ILock;
import de.intarsys.tools.locking.ILockLevel;
import de.intarsys.tools.locking.ILockSupport;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessFile;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.stream.TempFileOutputStream;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * File based implementation of {@link ILocator}.
 */
public class FileLocator extends CommonLocator implements ILockSupport, ICharsetAccess {

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

	private static final ILogger Log = LogTools.getLogger(FileLocator.class);

	private static File createFile(String fileNameOrUri) {
		try {
			return new File(new URI(fileNameOrUri));
		} catch (IllegalArgumentException | URISyntaxException ex) {
			// not a valid file URI; OK
		}
		return new File(fileNameOrUri);
	}

	private final boolean append;

	private File canonicalFile;

	private String charset;

	/**
	 * The file referenced by the locator
	 */
	private File file;

	/**
	 * Timestamp of last modification to the resource.
	 */
	private volatile long lastModified;

	/**
	 * Flag if the resource is out of synch
	 */
	private boolean outOfSynch;

	/**
	 * flag if we synchronize synchronously with every check
	 */
	private boolean synchSynchronous;

	/**
	 * flag if output is created initially in a temporary file
	 */
	private boolean useTempFile;

	/**
	 * Create a {@link FileLocator} to a {@link File}
	 * 
	 * @param file
	 */
	public FileLocator(File file) {
		super();
		this.file = file;
		this.append = false;
	}

	/**
	 * Create a {@link FileLocator} to a {@link File}
	 * 
	 * @param file
	 * @param append
	 */
	public FileLocator(File file, boolean append) {
		super();
		this.file = file;
		this.append = append;
	}

	/**
	 * Create a {@link FileLocator} to a file path.
	 * 
	 * @param fileNameOrUri
	 */
	public FileLocator(String fileNameOrUri) {
		this(createFile(fileNameOrUri));
	}

	@Override
	public void delete() throws IOException {
		FileTools.deleteRecursivly(getFile());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileLocator)) {
			return false;
		}
		return getCanonicalFile().equals(((FileLocator) obj).getCanonicalFile());
	}

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

	@Override
	public ILock getLock() {
		return new FileLocatorLock();
	}

	@Override
	public String getName() {
		if (getFile() == null) {
			return "unknown";
		}
		return getFile().getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// trigger timestamp reading
		getLastModified();
		File parentFile = FileTools.getParentFile(getFile());
		FileTools.mkdirs(parentFile);
		if (isUseTempFile()) {
			return new TempFileOutputStream(getFile(), "tmp_", "." + LocatorTools.getExtension(this));
		}
		return new FileOutputStream(getFile(), isAppend());
	}

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

	@Override
	public String getPath() {
		if (getFile() == null) {
			return "unknown";
		}
		return PathTools.toCanonicalSeparator(getFile().getAbsolutePath());
	}

	@Override
	public IRandomAccess getRandomAccess() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (isUseTempFile()) {
			throw new UnsupportedOperationException("no random access to temp file");
		}
		return new RandomAccessFile(getFile());
	}

	@Override
	public Reader getReader() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(getCharset())) {
			return new InputStreamReader(getInputStream());
		}
		return new InputStreamReader(getInputStream(), getCharset());
	}

	@Override
	public Reader getReader(String charset) throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(charset)) {
			return getReader();
		}
		return new InputStreamReader(getInputStream(), charset);
	}

	@Override
	public Writer getWriter() throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(getCharset())) {
			return new OutputStreamWriter(getOutputStream());
		}
		return new OutputStreamWriter(getOutputStream(), getCharset());
	}

	@Override
	public Writer getWriter(String charset) throws IOException {
		// trigger timestamp reading
		getLastModified();
		if (StringTools.isEmpty(charset)) {
			return getWriter();
		}
		return new OutputStreamWriter(getOutputStream(), charset);
	}

	@Override
	public int hashCode() {
		return getCanonicalFile().hashCode();
	}

	public boolean isAppend() {
		return append;
	}

	@Override
	public boolean isDirectory() {
		return getFile().isDirectory();
	}

	@Override
	public synchronized boolean isOutOfSynch() {
		if (isSynchSynchronous()) {
			synchCheck();
		}
		return outOfSynch;
	}

	@Override
	public boolean isReadOnly() {
		if (super.isReadOnly()) {
			return true;
		}
		IRandomAccess r = null;
		try {
			if (!getFile().exists()) {
				if (getFile().createNewFile()) {
					if (!getFile().delete()) { // NOSONAR
						Log.warn("could not delete test file {}", getFile());
					}
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

	@Override
	public ILocator[] listLocators(final ILocatorNameFilter filter) throws IOException {
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

	@Override
	public synchronized void synch() {
		/*
		 * synch brings the representation and real world in line
		 */
		outOfSynch = false;
		lastModified = 0;
		getLastModified();
	}

	protected synchronized void synchCheck() {
		outOfSynch = false;
		if (getFile() == null) {
			return;
		}
		if ((getLastModified() != getFile().lastModified())) {
			LogTools.getLogger(this.getClass()).log(Level.TRACE, "'" + getPath() + "' out of synch!");
			outOfSynch = true;
		}
	}

	@Override
	public String toString() {
		return getFile().toString();
	}

	@Override
	public URI toURI() {
		return getFile().toURI();
	}

}
