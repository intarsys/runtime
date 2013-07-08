/*
 * Copyright (c) 2008, intarsys consulting GmbH
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

import java.io.File;
import java.io.FileFilter;

/**
 * Tools for dealing with temporary files and directories.
 * <p>
 * This class provides a locked, VM unique temp directory. Upon startup,
 * unlocked directories are removed.
 */
public class TempTools {

	private volatile static File tempDir;

	private volatile static File tempDirVM;

	private static FileTools.Lock lock;

	private static int fileCounter = 0;

	private static int dirCounter = 0;

	static {
		cleanUp();
	}

	/**
	 * Clean up temporary directories.
	 */
	protected static void cleanUp() {
		File[] files = getTempDir().listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()
						&& file.getName().startsWith(TempTools.class.getName())) {
					return true;
				}
				return false;
			}

		});
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			cleanUpTempDir(files[i]);
		}
	}

	/**
	 * Clean up a single temporary directory
	 * 
	 * @param file
	 */
	protected static void cleanUpTempDir(File file) {
		FileTools.Lock tempLock = FileTools.lock(file);
		if (tempLock == null) {
			return;
		}
		tempLock.release();
		// orphaned directory, try to clean up
		FileTools.deleteRecursivly(file);
	}

	synchronized public static File createTempDir(String prefix, String suffix) {
		// resource local to unique VM temp directory
		// - don't need to check for name
		String tempName = prefix + dirCounter++ + suffix;
		File tempFile = new File(TempTools.getTempDirVM(), tempName);
		tempFile.mkdirs();
		return tempFile;
	}

	/**
	 * Create the root directory for all temp files
	 * 
	 * @param parent
	 * @return
	 */
	protected static File createTempDirVM(File parent) {
		String name = TempTools.class.getName();
		File tempFile = new File(parent, name);
		int counter = 0;
		while (true) {
			tempFile = new File(parent, name + "_" + counter);
			if (!tempFile.exists() && tempFile.mkdirs()) {
				lock = FileTools.lock(tempFile);
				if (lock != null) {
					break;
				}
			}
			counter++;
		}
		return tempFile;
	}

	synchronized public static File createTempFile(String prefix, String suffix) {
		// resource local to unique VM temp directory
		String tempName = prefix + suffix;
		File tempFile = new File(TempTools.getTempDirVM(), tempName);
		if (tempFile.exists()) {
			tempName = prefix + fileCounter++ + suffix;
			tempFile = new File(TempTools.getTempDirVM(), tempName);
		}
		return tempFile;
	}

	public static File getTempDir() {
		if (tempDir == null) {
			tempDir = new File(System.getProperty("java.io.tmpdir"));
		}
		return tempDir;
	}

	public static File getTempDirVM() {
		if (tempDirVM == null) {
			tempDirVM = createTempDirVM(getTempDir());
		}
		return tempDirVM;
	}

	public static void setTempDir(File pTempDir) {
		tempDir = pTempDir;
	}
}
