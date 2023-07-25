/*
 * Copyright (c) 2008, intarsys GmbH
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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import de.intarsys.tools.yalf.api.ILogger;

/**
 * Tools for dealing with temporary files and directories.
 * <p>
 * This class provides a locked, VM unique temp directory. Upon startup,
 * unlocked directories are removed.
 */
public class TempTools {

	private static volatile File TempDir;

	private static volatile File TempDirVM;

	private static AtomicInteger FileCounter = new AtomicInteger(0);

	private static AtomicInteger DirCounter = new AtomicInteger(0);

	private static final ILogger Log = PACKAGE.Log;

	/**
	 * Clean up temporary directories.
	 */
	protected static void cleanUp() {
		File root = getTempDir();
		cleanUp(root);
	}

	protected static void cleanUp(File root) {
		File[] files = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory() && file.getName().startsWith(TempTools.class.getName())) {
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
		try {
			FileTools.deleteRecursivly(file);
		} catch (IOException e) {
			Log.warn("failed to cleanup temp dir {}", file, e);
		}
	}

	public static synchronized File createTempDir(String prefix, String suffix) {
		// resource local to unique VM temp directory
		// - don't need to check for name
		String tempName = prefix + DirCounter.getAndIncrement() + suffix;
		File tempFile = new File(TempTools.getTempDirVM(), tempName);
		try {
			FileTools.mkdirs(tempFile);
			return tempFile;
		} catch (IOException e) {
			Log.warn("failed to create dir " + tempFile.getAbsolutePath(), e);
			return TempTools.getTempDir();
		}
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
			Log.trace("create temp directory,  check {}", tempFile);
			if (!tempFile.exists() && tempFile.mkdirs()) {
				FileTools.Lock lock = FileTools.lock(tempFile);
				if (lock != null) {
					break;
				}
			}
			counter++;
		}
		Log.trace("create temp directory,  lock {}", tempFile);
		return tempFile;
	}

	public static synchronized File createTempFile(String prefix, String suffix) {
		// resource local to unique VM temp directory
		String tempName = prefix + suffix;
		File tempFile = new File(TempTools.getTempDirVM(), tempName);
		if (tempFile.exists()) {
			tempName = prefix + FileCounter.getAndIncrement() + suffix;
			tempFile = new File(TempTools.getTempDirVM(), tempName);
		}
		return tempFile;
	}

	/**
	 * The temporary directory for the platform.
	 * 
	 * @return
	 */
	public static File getTempDir() {
		File result = TempDir;
		if (result == null) {
			synchronized (TempTools.class) {
				result = TempDir;
				if (result == null) {
					TempDir = result = new File(System.getProperty("java.io.tmpdir"));
					cleanUp();
				}
			}
		}
		return result;
	}

	/**
	 * A unique temporary directory for this VM.
	 * 
	 * @return
	 */
	public static File getTempDirVM() {
		File result = TempDirVM;
		if (result == null) {
			synchronized (TempTools.class) {
				result = TempDirVM;
				if (result == null) {
					TempDirVM = result = createTempDirVM(getTempDir());
				}
			}
		}
		return result;
	}

	public static void setTempDir(File pTempDir) {
		synchronized (TempTools.class) {
			TempDir = pTempDir;
		}
		cleanUp();
	}

	private TempTools() {
	}
}
