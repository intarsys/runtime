/*
 * Copyright (c) 2007, intarsys consulting GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  - Neither the name of intarsys nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.stream.StreamTools;

public class ArchiveTools {

	/**
	 * A utility to manage archiving files in a directory.
	 * 
	 */
	static class DumpDirectory {

		public static DumpDirectory get(File file) {
			return get(file.getAbsolutePath());
		}

		public static synchronized DumpDirectory get(String name) {
			DumpDirectory dd = (DumpDirectory) dumpDirs.get(name);
			if (dd == null) {
				dd = new DumpDirectory(new File(name)); // just for locking;
				dd.prepare();
				dumpDirs.put(name, dd);
			}
			return dd;
		}

		File dir;

		int currentCount;

		int dirCount;

		long lastDump = System.currentTimeMillis();

		protected DumpDirectory(File dir) {
			super();
			setDir(dir);
		}

		protected void checkDir() throws IOException {
			// check and create directory
			if (!getDir().exists()) {
				if (!getDir().mkdirs()) {
					throw new IOException(" can't create temporary directory "
							+ getDir());
				}
			}
		}

		protected void checkFiles(int max) throws IOException {
			// todo execute check in low priority daemon
			//
			// clean up archive directory, delete oldest files if more than
			// count
			// available
			// files must be stored with dump archive to guarantee that preifx
			// sorts
			// correctly
			// this implementation relies on the fact, that only the receiver
			// object
			// manipulates files in the directory
			if ((max <= 0) || (currentCount <= max)) {
				return;
			}

			String[] fileNames = dir.list();
			if (fileNames == null) {
				throw new IOException("can not list directory "
						+ dir.getAbsolutePath());
			}
			if (fileNames.length != (dirCount + currentCount)) {
				prepare(fileNames);
			}
			Arrays.sort(fileNames);

			int delete = Math.min(fileNames.length, currentCount - max);
			for (int i = 0; i < delete; i++) {
				File deleteFile = new File(dir, fileNames[i]);
				if (deleteFile.isFile()) {
					if (!deleteFile.delete()) {
						throw new IOException("can not delete file "
								+ deleteFile.getAbsolutePath());
					}
					currentCount--;
				}
			}
		}

		public java.io.File getDir() {
			return dir;
		}

		public synchronized File getDumpFile(String name, int max)
				throws IOException {
			File newFile = new File(getDir(), getUniquePrefix() + "." + name);

			// check directory every time, maybe deleted by some nerd...
			checkDir();
			if (!newFile.createNewFile()) {
				throw new IOException("can not create file "
						+ newFile.getAbsolutePath());
			}
			currentCount++;
			try {
				checkFiles(max);
			} catch (IOException e) {
				// ignore if we can not hold the correct level in dump directory
			}
			return newFile;
		}

		public long getLastDump() {
			return lastDump;
		}

		protected long getUniqueMillis() {
			// ensure unique counter values, millisecondsbased
			long current = System.currentTimeMillis();
			while (getLastDump() >= current) {
				current++;
			}
			setLastDump(current);
			return current;
		}

		protected String getUniquePrefix() {
			return theFormat.format(getUniqueMillis());
		}

		protected void prepare() {
			String[] fileNames = dir.list();
			if (fileNames == null) {
				// error, ignore
				return;
			}
			prepare(fileNames);
		}

		protected void prepare(String[] fileNames) {
			currentCount = 0;
			dirCount = 0;
			if (fileNames != null) {
				for (int i = 0; i < fileNames.length; i++) {
					File f = new File(dir, fileNames[i]);
					if (f.isFile()) {
						currentCount++;
					} else {
						dirCount++;
					}
				}
			}
		}

		private void setDir(java.io.File newDir) {
			dir = newDir;
		}

		private void setLastDump(long newLastDump) {
			lastDump = newLastDump;
		}
	}

	// 
	private static Map dumpDirs = new HashMap();

	// the number format used for archiving
	private static final NumberFormat theFormat;

	static {
		theFormat = NumberFormat.getNumberInstance();
		theFormat.setMaximumFractionDigits(0);
		theFormat.setMinimumIntegerDigits(19);
		theFormat.setGroupingUsed(false);
	}

	/**
	 * Archive a files content.
	 * 
	 * <p>
	 * The method creates a copy in the archive directory with a unique name
	 * that is guaranteed to create a sortable representation so that newer
	 * files have a "greater" filename. Creation of file names is thread safe.
	 * If more than <code>max</code> files are in the archive directory, the
	 * oldest files are deleted. max = 0 means never create archive, max = -1
	 * means always create archive. If <code>deleteSource</code> is
	 * <code>true</code>, the file to be archived is deleted after the archive
	 * was created.
	 * </p>
	 * 
	 * @param root
	 *            The root for relative addressing.
	 * @param relativePath
	 *            The path relative to root where to create the archive.
	 * @param file
	 *            The file to archive.
	 * @param max
	 *            The maximum number of archive files allowed.han
	 * @param sourceEncoding
	 *            The encoding of the file to be archived.
	 * @param destinationEncoding
	 *            The encoding of the archived file.
	 * @param deleteSource
	 *            Flag if source should be deleted.
	 * @param forceArchive
	 *            Flag if we should archive even if file is already in the
	 *            archive directory.
	 * @return The name of the archived file, or null.
	 * 
	 * @throws IOException
	 */
	public static String archive(File root, String relativePath, File file,
			int max, String sourceEncoding, String destinationEncoding,
			boolean deleteSource, boolean forceArchive) throws IOException {
		if ((max == 0) || (root == null)) {
			// no archiving desired
			return null;
		}

		String dirName = FileTools.resolvePath(root, relativePath)
				.getAbsolutePath();
		File archive;
		if (!forceArchive
				&& dirName.equals(file.getParentFile().getAbsolutePath())) {
			// i'm already in the temp directory, no need to copy
			archive = file;
		} else {
			DumpDirectory d = DumpDirectory.get(dirName);
			archive = d.getDumpFile(file.getName(), max);
			if (deleteSource) {
				FileTools.renameFile(file, sourceEncoding, archive,
						destinationEncoding);
			} else {
				FileTools.copyFile(file, sourceEncoding, archive,
						destinationEncoding);
			}
		}
		try {
			archive.setLastModified(System.currentTimeMillis());
		} catch (Exception ignore) {
			// getLog().logWarning(getLogPrefix() + " could not set
			// modification
			// time for file " + renamedFile.getPath());
		}
		return archive.getAbsolutePath();
	}

	/**
	 * Create a archive file and dump the input stream to this file. Calls
	 * createArchive, and then copies the is to the output stream.
	 * 
	 * @param root
	 *            The root for relative addressing.
	 * @param relativePath
	 *            The path relative to root where to create the archive.
	 * @param filename
	 *            The file to archive.
	 * @param max
	 *            The maximum number of archive files allowed.han
	 * @param is
	 *            The input stream to be dumped.
	 * @return The name of the archived file, or null.
	 * 
	 * @throws IOException
	 */
	public static String archive(File root, String relativePath,
			String filename, InputStream is, int max) throws IOException {
		if ((max == 0) || (root == null)) {
			// no archiving desired
			return null;
		}

		File archive = createArchive(root, relativePath, filename, max);
		FileOutputStream os = new FileOutputStream(archive);
		try {
			StreamTools.copyStream(is, os);
		} catch (Exception e) {
			throw new IOException("archiving failed (" + e.getMessage() + ")");
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception ignore) {
				// ignore failure
			}
		}
		return archive.getAbsolutePath();
	}

	/**
	 * Just creates a archive file, nothing will be dumped inside this file.
	 * 
	 * @param root
	 *            The root for relative addressing.
	 * @param relativePath
	 *            The path relative to root where to create the archive.
	 * @param filename
	 *            The file to archive.
	 * @param max
	 *            The maximum number of archive files allowed.han
	 * 
	 * @return The name of the archived file, or null.
	 * 
	 * @throws IOException
	 */
	public static File createArchive(File root, String relativePath,
			String filename, int max) throws IOException {
		if ((max == 0) || (root == null)) {
			// no archiving desired
			return null;
		}

		String dirName = FileTools.resolvePath(root, relativePath)
				.getAbsolutePath();
		DumpDirectory d = DumpDirectory.get(dirName);
		File archive = d.getDumpFile(filename, max);
		return archive;
	}

	public static OutputStream createOutputStream(File root, String filename,
			int max) throws IOException {
		if ((max == 0) || (root == null)) {
			// no archiving desired
			return null;
		}
		DumpDirectory d = DumpDirectory.get(root);
		File archive = d.getDumpFile(filename, max);
		return new FileOutputStream(archive);
	}

}
