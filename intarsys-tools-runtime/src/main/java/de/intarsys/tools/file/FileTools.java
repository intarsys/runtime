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
package de.intarsys.tools.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.system.SystemTools;
import de.intarsys.tools.valueholder.ObjectHolder;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Some utility methods to ease life with {@link File} instances.
 */
public class FileTools {

	public static class Lock {
		protected File file;
		protected File lockFile;
		protected FileOutputStream lockStream;
		protected boolean valid = true;

		protected Lock() {
		}

		public synchronized boolean isValid() {
			return valid;
		}

		public synchronized void release() {
			unlock(this);
		}
	}

	public static final String DIRECTORY_LOCK = "directory.lock"; //$NON-NLS-1$

	private static final ILogger Log = LogTools.getLogger(FileTools.class);

	private static final Map<File, Lock> LOCKS = new HashMap<>();

	/**
	 * Concatenate the two files given in <code>source</code> and
	 * <code>destination</code>.
	 * 
	 * @param source
	 *            The file to be appended.
	 * @param destination
	 *            The file to append to.
	 * 
	 * @throws IOException
	 */
	public static void appendFile(File source, File destination) throws IOException {
		if (equals(source, destination)) {
			return;
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(destination, true);
			// no need for buffering, StreamTools does this internally
			StreamTools.copy(is, os);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")");
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	/**
	 * Utility method for checking the availablity of a directory.
	 * 
	 * @param dir
	 *            The directory to check.
	 * @param create
	 *            Flag if we should create if dir not already exists.
	 * @param checkCanRead
	 *            Flag if we should check read permission.
	 * @param checkCanWrite
	 *            Flag if we should check write permission.
	 * 
	 * @return The checked directory.
	 * 
	 * @throws IOException
	 */
	public static File checkDirectory(File dir, boolean create, boolean checkCanRead, boolean checkCanWrite)
			throws IOException {
		if (dir == null) {
			return dir;
		}
		if (!dir.exists() && create) {
			FileTools.mkdirs(dir);
		}
		if (!dir.exists()) {
			throw new IOException("Can't create directory " + dir.getPath());
		}
		if (!dir.isDirectory()) {
			throw new IOException("Can't create directory " + dir.getPath());
		}
		if (checkCanRead && !dir.canRead()) {
			throw new IOException("No read access for directory " + dir.getPath());
		}
		if (checkCanWrite && !dir.canWrite()) {
			throw new IOException("No write access for directory " + dir.getPath());
		}
		return dir;
	}

	/**
	 * @see #checkDirectory(File, boolean, boolean, boolean)
	 */
	public static File checkDirectory(String path, boolean create, boolean checkCanRead, boolean checkCanWrite)
			throws IOException {
		if (StringTools.isEmpty(path)) {
			return null;
		}
		return checkDirectory(new File(path), create, checkCanRead, checkCanWrite);
	}

	/**
	 * Copy the byte content of <code>source</code> to <code>destination</code>. The
	 * "lastModified" property is always kept intact.
	 * 
	 * @param source
	 *            The file whose contents we should copy.
	 * @param destination
	 *            The file where the contents are copied to.
	 * 
	 * @throws IOException
	 */
	public static void copyBinaryFile(File source, File destination) throws IOException {
		if (destination.isDirectory()) {
			destination = new File(destination, source.getName());
		}
		Log.trace("copy binary '{}' to '{}'", source.getAbsolutePath(), destination.getAbsolutePath());
		if (destination.getParentFile() != null && !destination.getParentFile().exists()) {
			Log.trace("make directories for '{}'", destination.getAbsolutePath());
			FileTools.mkdirs(destination.getParentFile());
		}
		try {
			Files.copy(
					source.toPath(), destination.toPath(),
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			Log.debug("copy binary failed ({})", ExceptionTools.getMessage(e));
			throw new IOException("copy binary failed for '" + source.getAbsolutePath() + "' to '"
					+ destination.getAbsolutePath() + "'", e);
		}
	}

	/**
	 * @see #copyBinaryFile(File, File)
	 */
	public static void copyFile(File source, File destination) throws IOException {
		copyBinaryFile(source, destination);
	}

	/**
	 * Copy the character content of <code>source</code> to
	 * <code>destination</code>. The "lastModified" property is always kept intact.
	 * 
	 * @param source
	 *            The file whose contents we should copy.
	 * @param sourceEncoding
	 *            The encoding of the source byte stream.
	 * @param destination
	 *            The file where the contents are copied to.
	 * @param destinationEncoding
	 *            The encoding of the destination byte stream.
	 * 
	 * @throws IOException
	 */
	public static void copyFile(File source, String sourceEncoding, File destination, String destinationEncoding)
			throws IOException {
		if ((sourceEncoding == null) || (destinationEncoding == null) || sourceEncoding.equals(destinationEncoding)) {
			copyBinaryFile(source, destination);
			return;
		}
		Log.trace("copy encoded '{}' to '{}'", source.getAbsolutePath(), destination.getAbsolutePath());
		if (destination.isDirectory()) {
			destination = new File(destination, source.getName());
		}
		if (destination.getParentFile() != null && !destination.getParentFile().exists()) {
			Log.trace("make directories for '" + destination.getAbsolutePath() + "'");
			FileTools.mkdirs(destination.getParentFile());
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(destination));
			StreamTools.copy(is, sourceEncoding, os, destinationEncoding);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			Log.debug("copy encoded failed ({})", ExceptionTools.getMessage(e));
			throw new IOException("copy encoded failed for '" + source.getAbsolutePath() + "' to '"
					+ destination.getAbsolutePath() + "'", e);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		destination.setLastModified(source.lastModified()); // NOSONAR result not required
	}

	/**
	 * Copy from source to destination. If source is a file, a simple file copy is
	 * made. If source is a directory, every content is copied from within source
	 * into destination which must be a directory (that does not need to exist
	 * already). The "lastModified" property of the source directories and files is
	 * preserved.
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyRecursively(File source, File destination) throws IOException {
		if (source.isFile()) {
			copyFile(source, destination);
			return;
		}
		if (!source.isDirectory()) {
			throw new IOException("file '" + source.getAbsolutePath() + "' does not exist.");
		}
		if (destination.isFile()) {
			throw new IOException("cannot copy directory into file");
		}
		FileTools.mkdirs(destination);
		String[] content = source.list();
		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				copyRecursively(new File(source, content[i]), new File(destination, content[i]));
			}
		}
		destination.setLastModified(source.lastModified()); // NOSONAR result not required
	}

	/**
	 * Copy from source to a subdirectory of destination. If source is a file, a
	 * simple file copy is made. If source is a directory, every content is copied
	 * from within source into destination subdirectory. The "lastModified" property
	 * of the source directories and files is preserved.
	 * 
	 * @param source
	 * @param destinationParent
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public static File copyRecursivelyInto(File source, File destinationParent, String newName) throws IOException {
		if (destinationParent.isFile()) {
			throw new IOException("can't copy into file");
		}
		String destinationName = (newName == null) ? source.getName() : newName;
		File destinationFile = new File(destinationParent, destinationName);
		if (equals(source, destinationFile)) {
			return destinationFile;
		}
		copyRecursively(source, destinationFile);
		return destinationFile;
	}

	/**
	 * Create an empty physical file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void createEmptyFile(File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		try {
			//
		} finally {
			StreamTools.close(os);
		}
	}

	/**
	 * Create a file object representing a temporary file in the user's temp dir
	 * with the same name as the given file.
	 * 
	 * @param file
	 *            file to use
	 * @return file object representing a temporary file
	 */
	public static File createTempFile(File file) throws IOException {
		String name;
		String extension;
		int index;

		name = file.getName();
		index = name.lastIndexOf('.');
		if (index >= 0) {
			extension = name.substring(index);
			name = name.substring(0, index);
		} else {
			extension = StringTools.EMPTY;
		}
		if (name.length() < 3) {
			name = "tmp" + name;
		}
		return TempTools.createTempFile(name, extension);
	}

	/**
	 * Create a file object representing a temporary file in the user's temp dir
	 * with the given filename.
	 * <p>
	 * This does not actually create a file in the file system.
	 * 
	 * @param filename
	 *            filename to use
	 * @return file object representing a temporary file
	 */
	public static File createTempFile(String filename) throws IOException {
		return createTempFile(new File(filename));
	}

	/**
	 * The delete operation that Java should have provided.... If deletion fails, an
	 * {@link IOException} is thrown.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void delete(File file) throws IOException {
		if (!file.exists()) {
			Log.trace("delete '{}' skipped", file.getAbsolutePath());
			return;
		}
		Files.delete(file.toPath());
		Log.trace("deleted '{}'", file);
	}

	/**
	 * Delete any file in <code>directory</code> that is older than
	 * <code>millis</code> milliseconds. When <code>recursiveScan</code> is
	 * <code>true</code> the directory lookup is made recursive.
	 * 
	 * @param directory
	 *            The directory to scan.
	 * @param millis
	 *            The number of milliseconds a file is allowed to live.
	 * @param recursiveScan
	 *            Flag if we should handle directories recursive.
	 * 
	 * @throws IOException
	 */
	public static void deleteAfter(File directory, long millis, boolean recursiveScan) throws IOException {
		if (millis <= 0) {
			return;
		}

		String[] fileNames = directory.list();
		if (fileNames == null) {
			throw new IOException("cannot list " + directory);
		}

		long checkMillis = System.currentTimeMillis() - millis;
		for (int j = 0; j < fileNames.length; j++) {
			File file = new File(directory, fileNames[j]);
			if (file.isDirectory() && recursiveScan) {
				deleteAfter(file, millis, recursiveScan);
			}
			if (file.lastModified() < checkMillis) {
				Files.delete(file.toPath());
			}
		}
	}

	/**
	 * Delete all empty directories in the parent hierarchy of file, up to and
	 * excluding root.
	 * 
	 * @param root
	 * @param file
	 * @throws IOException
	 */
	public static void deleteEmptyDirectories(File root, File file) throws IOException {
		if (file == null || equals(file, root)) {
			return;
		}
		File dir = null;
		if (file.isDirectory()) {
			dir = file;
		} else {
			dir = file.getParentFile();
		}
		if (dir != null) {
			File[] files = dir.listFiles();
			if (files != null && files.length == 0) {
				try {
					Files.delete(dir.toPath());
					deleteEmptyDirectories(root, dir.getParentFile());
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Deletes a file or directory, if necessary recursively. The target file (directory) is deleted itself.
	 * 
	 * The algorithm tries to delete as much as possible, deferring exceptions.
	 * 
	 * @param file
	 *            The file or directory to delete.
	 * 
	 * @throws IOException
	 */
	public static void deleteRecursivly(File file) throws IOException {
		deleteRecursivly(file, true);
	}

	/**
	 * Deletes a file or directory, if necessary recursively.
	 * 
	 * The algorithm tries to delete as much as possible, deferring exceptions.
	 * 
	 * @param file
	 *            The file or directory to delete.
	 * @param deleteRoot
	 *            Flag if the root directory should be deleted itself.
	 * 
	 */
	public static void deleteRecursivly(File file, boolean deleteRoot) throws IOException {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isFile()) {
			Files.delete(file.toPath());
			return;
		}
		ObjectHolder<IOException> ex = new ObjectHolder<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toPath())) {
			ds.forEach((path) -> {
				try {
					deleteRecursivly(path.toFile());
				} catch (IOException e) {
					ex.set(e);
				}
			});
		}
		if (deleteRoot) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				ex.set(e);
			}
		}
		if (ex.get() != null) {
			throw ex.get();
		}
	}

	/**
	 * <code>true</code> when the two files represent the same physical file in the
	 * file system.
	 * 
	 * @param source
	 *            The first file to be checked.
	 * @param destination
	 *            The second file to be checked.
	 * 
	 * @return <code>true</code> when the two files represent the same physical file
	 *         in the file system.
	 * @throws IOException
	 */
	public static boolean equals(File source, File destination) throws IOException {
		// we happened to get strange exception when checking on Win, so wanted to get details
		File canonicalSource;
		try {
			canonicalSource = source.getCanonicalFile();
		} catch (IOException e) {
			throw new IOException(e.getMessage() + " ('" + source.getAbsolutePath() + "')", e);
		}
		File canonicalDest;
		try {
			canonicalDest = destination.getCanonicalFile();
		} catch (IOException e) {
			throw new IOException(e.getMessage() + " ('" + destination.getAbsolutePath() + "')", e);
		}
		return canonicalSource.equals(canonicalDest);
	}

	/**
	 * Get the local name of the file in its directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * @param file
	 *            The file whose base name is requested.
	 * 
	 * @return The local name of the file in its directory without the extension.
	 */
	public static String getBaseName(File file) {
		if (file == null) {
			return PathTools.getBaseName((String) null, null, StringTools.EMPTY);
		} else {
			return PathTools.getBaseName(file.getName(), null, StringTools.EMPTY);
		}
	}

	/**
	 * Get the local name of the file in its directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * @param filename
	 *            The filename whose base name is requested.
	 * 
	 * @return The local name of the file in its directory without the extension.
	 */
	public static String getBaseName(String filename) {
		return PathTools.getBaseName(filename);
	}

	/**
	 * Get the local name of the file in its directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * The extensionPrefix may be used to designate a static prefix that should be
	 * considered part of the extension, not the base name. This is useful for
	 * handling "2nd order" extensions like "document.txt.p7s".
	 * 
	 * The special case ".name" is not treated like an extension and returned as the
	 * basename.
	 * 
	 * @param filename
	 *            The filename whose base name is requested.
	 * @param extensionPrefix
	 *            An optional static prefix that should be considered
	 *            part of the extension, not the basename.
	 * @param defaultName
	 *            returned if filename is null or a empty String
	 * @return The local name of the file in its directory without the extension.
	 */
	public static String getBaseName(String filename, String extensionPrefix, String defaultName) {
		return PathTools.getBaseName(filename, extensionPrefix, defaultName);
	}

	/**
	 * Create a byte array with the files content.
	 * 
	 * @param file
	 *            The file to read.
	 * 
	 * @return Create a byte array with the files content.
	 * 
	 * @throws IOException
	 */
	public static byte[] getBytes(File file) throws IOException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			// no need for buffering, StreamTools does this internally
			return StreamTools.getBytes(is);
		} finally {
			StreamTools.close(is);
		}
	}

	public static String getEncoding() {
		return System.getProperty("file.encoding");
	}

	/**
	 * Get the extension of the file name. If no extension is present, the empty
	 * string is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param file
	 *            The file whose extension is requested.
	 * 
	 * @return The extension of the file name. If no extension is present, the empty
	 *         string is returned.
	 */
	public static String getExtension(File file) {
		return PathTools.getExtension(file.getName(), null, StringTools.EMPTY);
	}

	/**
	 * Get the extension of the file name. If no extension is present, the empty
	 * string is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param filename
	 *            The filename whose extension is requested.
	 * 
	 * @return The extension of the file name. If no extension is present, the empty
	 *         string is returned.
	 */
	public static String getExtension(String filename) {
		return PathTools.getExtension(filename);
	}

	/**
	 * Get the extension of the file name. If no extension is present, the
	 * defaultName is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param filename        The filename whose extension is requested.
	 * @param extensionPrefix An extension may use a prefix (by some unique
	 *                        timestamp e.g.) like
	 *                        &lt;name>.&lt;prefix&gt;.&lt;extension&gt;, where
	 *                        prefix is considered being part of the extension. If
	 *                        the filename is .&lt;prefix&gt;. &lt;extension&gt; or
	 *                        &lt;prefix&gt;.&lt;extension&gt;, the prefix is
	 *                        considered being the filename!
	 * @param defaultName     returned if the filename is empty or null or there is
	 *                        no extension
	 * 
	 * @return The extension of the file name. If no extension is present, the empty
	 *         string is returned.
	 */
	public static String getExtension(String filename, String extensionPrefix, String defaultName) {
		return PathTools.getExtension(filename, extensionPrefix, defaultName);
	}

	/**
	 * Get the local name of the file in its directory (with extension).
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu.txt"
	 * </pre>
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileName(File file) {
		return PathTools.getName(file.getName(), StringTools.EMPTY);
	}

	/**
	 * Get the local name of the file in its directory (with extension).
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu.txt"
	 * </pre>
	 * 
	 * @param filename
	 *            The filename whose name is requested.
	 * 
	 * @return The local name of the file in its directory (with extension)
	 */
	public static String getFileName(String filename) {
		return PathTools.getName(filename);
	}

	/**
	 * Get the local name of the file in its directory (with extension).
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu.txt"
	 * </pre>
	 * 
	 * @param filename
	 *            The filename whose name is requested.
	 * @param defaultName
	 *            returned if filename is null or a empty String
	 * 
	 * @return The local name of the file in its directory (with extension)
	 */
	public static String getFileName(String filename, String defaultName) {
		return PathTools.getName(filename, defaultName);
	}

	protected static File getLockFile(File file) {
		File lockFile = null;
		if (!file.exists() || file.isFile()) {
			lockFile = new File(file.getAbsolutePath() + ".lock");
		} else {
			lockFile = new File(file, DIRECTORY_LOCK);
		}
		return lockFile;
	}

	/**
	 * Try to get a valid parent for file.
	 * 
	 * @param file
	 */
	public static File getParentFile(File file) {
		File parentFile = file.getParentFile();
		if (parentFile == null) {
			parentFile = file.getAbsoluteFile().getParentFile();
		}
		if (parentFile == null) {
			return null;
		}
		// do not check "grandpa" for UNC here!
		return parentFile;
	}

	/**
	 * break a path down into individual elements and add to a list. example : if a
	 * path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
	 * 
	 * @param f
	 *            input file
	 * 
	 * @return a List collection with the individual elements of the path in reverse
	 *         order
	 */
	private static List getPathList(File f) throws IOException {
		List l = new ArrayList();
		File r = f.getCanonicalFile();
		while (r != null) {
			if (r.getName().length() == 0) {
				int dblptIndex = r.getPath().indexOf(":");
				if (dblptIndex == -1) {
					l.add("");
				} else {
					l.add(r.getPath().substring(0, dblptIndex));
				}
			} else {
				l.add(r.getName());
			}
			r = r.getParentFile();
		}

		List reversed = new ArrayList();
		for (int i = l.size() - 1; i >= 0; i--) {
			reversed.add(l.get(i));
		}
		return reversed;
	}

	/**
	 * Get the path part of the file name.
	 * 
	 * The path never ends with a File.separator, except when it designates the
	 * root.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "gnu.txt" -> ""
	 * </pre>
	 * 
	 * <pre>
	 * "/gnu.txt" -> "/"
	 * </pre>
	 * 
	 * <pre>
	 * "a/gnu.txt" -> "a"
	 * </pre>
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "/a"
	 * </pre>
	 * 
	 * 
	 * @param file The file whose path is requested.
	 * 
	 * @return The path name of the file
	 */
	public static String getPathName(File file) {
		return PathTools.getParent(file.getPath(), StringTools.EMPTY);
	}

	/**
	 * Get the path part of the file name.
	 * 
	 * The path never ends with a File.separator, except when it designates the
	 * root.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "gnu.txt" -> ""
	 * </pre>
	 * 
	 * <pre>
	 * "/gnu.txt" -> "/"
	 * </pre>
	 * 
	 * <pre>
	 * "a/gnu.txt" -> "a"
	 * </pre>
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "/a"
	 * </pre>
	 * 
	 * 
	 * @param filename The filename whose path is requested.
	 * 
	 * @return The path name of the file
	 */
	public static String getPathName(String filename) {
		return PathTools.getParent(filename);
	}

	/**
	 * Get the path part of the file name.
	 * 
	 * The path never ends with a File.separator, except when it designates the
	 * root.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "gnu.txt" -> ""
	 * </pre>
	 * 
	 * <pre>
	 * "/gnu.txt" -> "/"
	 * </pre>
	 * 
	 * <pre>
	 * "a/gnu.txt" -> "a"
	 * </pre>
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "/a"
	 * </pre>
	 * 
	 * @param filename
	 *            The filename whose path is requested.
	 * @param defaultName
	 *            returned if filename is null or a empty String
	 * 
	 * @return The path name of the file
	 */
	public static String getPathName(String filename, String defaultName) {
		return PathTools.getParent(filename, defaultName);
	}

	/**
	 * get relative path of "file" with respect to "base" directory example :
	 * 
	 * <code>
	 * base = /a/b/c;
	 * file = /a/d/e/x.txt;
	 * getRelativePath(file, base) == ../../d/e/x.txt;
	 * </code>
	 * 
	 * @param base
	 *            base path, should be a directory, not a file, or it doesn't make
	 *            sense
	 * @param file
	 *            file to generate path for
	 * 
	 * @return path from home to f as a string
	 */
	public static String getPathRelativeTo(File file, File base) throws IOException {
		String relativePath = null;
		if (base != null) {
			List fileList = getPathList(file);
			List baseList = getPathList(base);
			relativePath = matchPathLists(fileList, baseList);
		}
		if (relativePath == null) {
			return file.getAbsolutePath();
		} else {
			return relativePath;
		}
	}

	/**
	 * get relative path of "file" with respect to "base" directory, but only if
	 * file is an ancestor.
	 * 
	 * <code>
	 * base = /a/b/c;
	 * file = /a/d/e/x.txt;
	 * getRelativePath(file, base) == ../../d/e/x.txt;
	 * </code>
	 * 
	 * @param file
	 * @param base
	 * @return
	 */
	public static String getPathRelativeToIfAncestor(File file, File base) {
		if (base == null) {
			return file.getPath();
		} else {
			if (FileTools.isAncestor(base, file)) {
				try {
					return FileTools.getPathRelativeTo(file, base);
				} catch (IOException e) {
					return file.getPath();
				}
			} else {
				return file.getPath();
			}
		}
	}

	/**
	 * Read a file's content at once and return as a string.
	 * 
	 * <p>
	 * Use with care!
	 * </p>
	 * 
	 * @param file
	 *            The file to read.
	 * 
	 * @return The string content of the file.
	 * 
	 * @throws IOException
	 */
	public static String getString(File file) throws IOException {
		return getString(file, System.getProperty("file.encoding"));
	}

	/**
	 * Read a file's content at once and return as a string using charset.
	 * 
	 * @param file
	 *            The file to read.
	 * @param charset
	 *            The charset to use.
	 * 
	 * @return The string content of the file.
	 * 
	 * @throws IOException
	 */
	public static String getString(File file, Charset charset) throws IOException {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			return StreamTools.getString(is, charset);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Read a file's content at once and return as a string using encoding.
	 * 
	 * @param file
	 *            The file to read.
	 * @param encoding
	 *            The encoding to use.
	 * 
	 * @return The string content of the file.
	 * 
	 * @throws IOException
	 */
	public static String getString(File file, String encoding) throws IOException {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			return StreamTools.getString(is, encoding);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public static boolean isAncestor(File parent, File descendant) {
		if (parent == null) {
			return false;
		}
		File current = descendant;
		while (!parent.equals(current)) {
			if (current == null) {
				return false;
			}
			current = current.getParentFile();
		}
		return true;
	}

	public static boolean isExtensionMatch(File file, String extensions) {
		if (StringTools.isEmpty(extensions)) {
			return false;
		}
		String[] tempExtensions = extensions.toLowerCase().split(";");
		String tempName = file.getName().toLowerCase();
		for (int i = 0; i < tempExtensions.length; i++) {
			if (tempName.endsWith(tempExtensions[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLegalPath(String filename) {
		if (SystemTools.isWindows()) {
			String fileName = (new File(filename)).getName();
			if ((filename.indexOf("<") != -1) || (filename.indexOf(">") != -1) || (filename.indexOf("?") != -1)
					|| (filename.indexOf("\"") != -1) || (fileName.indexOf(":") != -1) || (filename.indexOf("|") != -1)
					|| (filename.indexOf("*") != -1)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Answer true if a cooperative {@link Lock} is held on file.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isLocked(File file) {
		synchronized (LOCKS) {
			if (LOCKS.get(file) != null) {
				return true;
			}
			return isLockedBasic(file);
		}
	}

	public static boolean isLockedBasic(File file) {
		File lockFile = getLockFile(file);
		if (lockFile.exists()) {
			// see if we have a dead lock (no pun intended)
			Lock tempLock = null;
			try {
				tempLock = lock(file);
				if (tempLock != null) {
					// no longer used
					Log.log(Level.INFO, "found abandoned lock '" + tempLock.file.getAbsolutePath() //$NON-NLS-1$
							+ "', take over");
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				// can't acquire lock
				return true;
			} finally {
				if (tempLock != null) {
					tempLock.release();
				}
			}
		}
		return false;
	}

	public static boolean isReadOnly(File file) {
		boolean delete = !file.exists();
		RandomAccessFile r = null;
		try {
			// if accessible and non-existent, this will create the file; we
			// will delete it afterwards
			r = new RandomAccessFile(file, "rw"); //$NON-NLS-1$
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			StreamTools.close(r);
			// delete the created file
			if (delete && file.exists()) {
				file.delete(); // NOSONAR
			}
		}
	}

	public static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	/**
	 * Create a cooperative {@link Lock} for file.
	 * 
	 * This is not appropriate for synchronization with partners that are not aware
	 * of this special protocol as no real synchronization is done on the target
	 * file. The cooperating system must agree on this special protocol implemented
	 * here.
	 * 
	 * The lock is implemented using an explicit stand-in file. For a file this is
	 * "&lt;filename&gt;.lock", for a directory this is
	 * "&lt;directoryname&gt;/directory.lock".
	 * 
	 * @param file
	 * @return
	 */
	public static Lock lock(File file) {
		synchronized (LOCKS) {
			if (LOCKS.get(file) != null) {
				// some systems do not prevent locking in the same VM
				Log.log(Level.TRACE, "lock acquisition failed on " + file);
				return null;
			}
			Lock lock = lockBasic(file);
			LOCKS.put(file, lock);
			return lock;
		}
	}

	public static Lock lockBasic(File file) {
		FileOutputStream os = null;
		FileChannel channel = null;
		File lockFile = getLockFile(file);
		try {
			os = new FileOutputStream(lockFile);
			channel = os.getChannel();
			FileLock fileLock = channel.tryLock();
			if (fileLock != null) {
				lockFile.deleteOnExit();
				Lock lock = new Lock();
				lock.file = file;
				lock.lockFile = lockFile;
				lock.lockStream = os;
				Log.trace("lock acquired on {}", file);
				return lock;
			}
			// force clean-up
			throw new IOException("lock failed");
		} catch (Exception ex) {
			StreamTools.close(os);
			StreamTools.close(channel);
			Log.trace("lock acquisition failed on {}", file);
			return null;
		}
	}

	/**
	 * figure out a string representing the relative path of 'f' with respect to 'r'
	 * 
	 * @param r
	 *            home path
	 * @param f
	 *            path of file
	 * 
	 * @return The relative path
	 */
	private static String matchPathLists(List fileList, List baseList) {
		Iterator sourceIterator = baseList.iterator();
		Iterator targetIterator = fileList.iterator();

		// remove equal path components
		boolean intersection = false;
		while (sourceIterator.hasNext() && targetIterator.hasNext()) {
			if (sourceIterator.next().equals(targetIterator.next())) {
				sourceIterator.remove();
				targetIterator.remove();
				intersection = true;
			} else {
				break;
			}
		}

		// only the differing path components remain
		if (!intersection) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < baseList.size(); i++) {
			sb.append("../");
		}
		for (Iterator i = fileList.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append("/");
			}
		}
		return sb.toString();
	}

	/**
	 * Create the directory denoted by file.
	 * 
	 * If the method terminates without exception, the directory file exits (either
	 * because it was created or it already existed).
	 * 
	 * If the method terminates with an exception, the file may and/or its parent
	 * directories may exist.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void mkdirs(File file) throws IOException {
		if (file == null) {
			return;
		}
		if (file.exists()) {
			return;
		}
		if (!file.mkdirs()) {
			// windows race condition
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			if (file.exists()) {
				return;
			}
			if (!file.mkdirs()) {
				throw new IOException("failed to create " + file.getAbsolutePath());
			}
		}
	}

	/**
	 * @see #renameFile(File, String, File, String)
	 */
	public static void renameFile(File source, File destination) throws IOException {
		renameFile(source, null, destination, null);
	}

	/**
	 * "Rename" a file.
	 * 
	 * <p>
	 * The effect is that there is a new file <code>destination</code>, encoded in
	 * <code>destinationEncoding</code>, the old file <code>source</code> is
	 * deleted.
	 * </p>
	 * 
	 * @param source
	 *            The source name of the file.
	 * @param sourceEncoding
	 *            The encoding of the source file.
	 * @param destination
	 *            The destination name of the file.
	 * @param destinationEncoding
	 *            The encoding of the destination file.
	 * 
	 * @throws IOException
	 * 
	 */
	public static void renameFile(File source, String sourceEncoding, File destination, String destinationEncoding)
			throws IOException {
		if (equals(source, destination)) {
			return;
		}
		try {
			Files.delete(destination.toPath());
		} catch (IOException e) {
			// ignore, did not exist
		}
		boolean renameByCopy = ((sourceEncoding != null) && (destinationEncoding != null)
				&& !sourceEncoding.equals(destinationEncoding));
		if (!renameByCopy) {
			Log.trace("rename '{}' to '{}'", source.getAbsolutePath(), destination.getAbsolutePath());
			if (source.renameTo(destination)) {
				return;
			}
			Log.trace("rename native failed");
		}
		// we must try harder - either because encodings do not match or native
		// renaming is not successful.
		File tempDestination = new File(destination.getAbsolutePath() + ".--temporary--");
		Log.debug("rename create temp file for '" + source.getAbsolutePath() + "' at '"
				+ tempDestination.getAbsolutePath());
		copyFile(source, sourceEncoding, tempDestination, destinationEncoding);
		if (!tempDestination.renameTo(destination)) {
			tempDestination.delete(); // NOSONAR
			throw new IOException("rename temp file failed for '" + tempDestination.getAbsolutePath() + "'");
		}
		if (!source.delete()) { // NOSONAR
			// undo creation of copy
			destination.delete(); // NOSONAR
			throw new IOException("rename delete source failed for '" + source.getAbsolutePath() + "'");
		}
	}

	/**
	 * Create a new filename, removing the extension from filename if present and
	 * adding the new extension, separated by ".".
	 * 
	 * Example: replaceExtension("xyz.new.pdf", "p7s") -> "xyz.new.p7s"
	 * 
	 * @param filename
	 * @param extension
	 * @return
	 */
	public static String replaceExtension(String filename, String extension) {
		if (StringTools.isEmpty(filename)) {
			return filename;
		}
		int dotPos = filename.lastIndexOf('.');
		if (dotPos >= 1) {
			filename = filename.substring(0, dotPos);
		}
		return filename + "." + extension;
	}

	/**
	 * Return a new {@link File} instance for "path". If path is relative, than it
	 * will be interpreted as a child of "parent", if it is absolute, it is returned
	 * as is.
	 * <p>
	 * ATTENTION: On windows, if "path" is absolute but without drive or UNC prefix,
	 * this root information is NOT taken from "parent".
	 * 
	 * @param parent
	 * @param path
	 * @return Return a new {@link File} instance for "path".
	 */
	public static File resolvePath(File parent, String path) {
		if (StringTools.isEmpty(path)) {
			return (parent == null) ? new File("") : parent;
		}
		if (parent == null) {
			return new File(path);
		}

		File file = new File(path);
		if (file.isAbsolute()) {
			return file;
		}
		return new File(parent, path);
	}

	/**
	 * Create a byte array with the files content.
	 * 
	 * @param file
	 *            The file to read.
	 * 
	 * @return Create a byte array with the files content.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #getBytes(File)} instead
	 */
	@Deprecated
	public static byte[] toBytes(File file) throws IOException {
		return getBytes(file);
	}

	/**
	 * Read a file's content at once and return as a string.
	 * 
	 * <p>
	 * Use with care!
	 * </p>
	 * 
	 * @param file
	 *            The file to read.
	 * 
	 * @return The string content of the file.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #getString(File)} instead
	 */
	@Deprecated
	public static String toString(File file) throws IOException {
		return getString(file);
	}

	/**
	 * Read a file's content at once and return as a string in the correct encoding.
	 * 
	 * <p>
	 * Use with care!
	 * </p>
	 * 
	 * @param file
	 *            The file to read.
	 * @param encoding
	 *            The encoding to use.
	 * 
	 * @return The string content of the file.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #getString(File,String)} instead
	 */
	@Deprecated
	public static String toString(File file, String encoding) throws IOException {
		return getString(file, encoding);
	}

	/**
	 * Replaces all characters that are generally not allowed or considered useful
	 * in filenames with underscore.
	 * 
	 * @param param
	 *            java.lang.String
	 * 
	 * @return java.lang.String
	 */
	public static String trimPath(String param) {
		if (param == null) {
			return null;
		}
		String tmp = param.trim();
		// uniform use of slash and backslash
		String drivePrefix = StringTools.EMPTY;
		// save drive
		if ((tmp.length() >= 2) && (tmp.charAt(1) == ':')) {
			drivePrefix = tmp.substring(0, 2);
			tmp = tmp.substring(2);
		}
		tmp = tmp.replaceAll("[\\*\"\\?\\<\\>\\|\\:\\n\\t\\r\\f]", "_");
		return drivePrefix + tmp;
	}

	protected static void unlock(Lock lock) {
		synchronized (LOCKS) {
			LOCKS.remove(lock.file);
			lock.valid = false;
			StreamTools.close(lock.lockStream);
			try {
				Files.delete(lock.lockFile.toPath());
			} catch (IOException e) {
				Log.warn("failed to remove lock file {}", lock.lockFile, e);
			}
		}
	}

	/**
	 * Wait for a file to arrive.
	 * 
	 * <p>
	 * The method waits at most <code>timeout</code> milliseconds for a file to
	 * arrive. When <code>delay</code> is != 0 the method checks the file's size for
	 * changes until it reaches a stable size.
	 * </p>
	 * 
	 * @param file
	 *            The file to wait for.
	 * @param timeout
	 *            The maximum time in milliseconds to wait for first occurence
	 *            of <code>file</code>.
	 * @param delay
	 *            The number of milliseconds between two checks against the
	 *            files size.
	 * 
	 * @throws IOException
	 */
	public static void wait(File file, long timeout, long delay) throws IOException {
		// todo zero length files
		long stop = System.currentTimeMillis() + timeout;
		for (;;) {
			try {
				if (file.exists()) {
					if (delay > 0) {
						long oldSize = -1;
						long newSize = file.length();
						for (;;) {
							if (oldSize != newSize) {
								oldSize = newSize;
								Thread.sleep(delay);
								newSize = file.length();
								continue;
							}
							break;
						}
					}
					return;
				}
				if (System.currentTimeMillis() > stop) {
					// timeout
					throw new IOException("timeout waiting for " + file.getPath());
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("interrupted waiting for " + file.getPath());
			}
		}
	}

	/**
	 * Create a file from the byte content.
	 * 
	 * @param file
	 *            The file to write/create
	 * @param bytes
	 *            The data to be written into the file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, byte[] bytes) throws IOException {
		if (bytes == null) {
			throw new NullPointerException();
		}
		FileOutputStream os = new FileOutputStream(file);
		try {
			// write stream all at once
			os.write(bytes);
		} finally {
			StreamTools.close(os);
		}
	}

	/**
	 * Create a file from the string content.
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text) throws IOException {
		write(file, text, Charset.defaultCharset(), false);
	}

	/**
	 * Create a file from the string content.
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * @param append
	 *            Flag to append to an existing file or create a new file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text, boolean append) throws IOException {
		write(file, text, Charset.defaultCharset(), append);
	}

	/**
	 * Create a file from the string content.
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text, Charset charset) throws IOException {
		write(file, text, charset, false);
	}

	/**
	 * Create a file from the string content / append a string to a file
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * @param append
	 *            Flag to append to an existing file or create a new file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text, Charset charset, boolean append) throws IOException {
		OutputStream os = null;
		Writer writer = null;
		try {
			os = new FileOutputStream(file, append);
			writer = new OutputStreamWriter(os, charset);
			writer.write(text);
		} finally {
			StreamTools.close(writer);
			StreamTools.close(os);
		}
	}

	/**
	 * Create a file from the string content.
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text, String encoding) throws IOException {
		write(file, text, encoding, false);
	}

	/**
	 * Create a file from the string content / append a string to a file
	 * 
	 * @param file
	 *            The file to write/create
	 * @param text
	 *            The text to be written into the file.
	 * @param append
	 *            Flag to append to an existing file or create a new file.
	 * 
	 * @throws IOException
	 */
	public static void write(File file, String text, String encoding, boolean append) throws IOException {
		if (text == null) {
			throw new NullPointerException();
		}
		OutputStream os = null;
		Writer writer = null;
		try {
			os = new FileOutputStream(file, append);
			writer = new OutputStreamWriter(os, encoding);
			writer.write(text);
		} finally {
			StreamTools.close(writer);
			StreamTools.close(os);
		}
	}

	private FileTools() {
		//
	}
}
