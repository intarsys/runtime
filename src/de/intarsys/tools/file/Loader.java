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
package de.intarsys.tools.file;

import java.io.File;
import java.io.IOException;

import de.intarsys.tools.string.StringTools;

/**
 * A utility class to simplify the task of loading files and / or directories.
 * 
 */
abstract public class Loader {
	public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	public static final String DEFAULT_LANGUAGE = "en"; //$NON-NLS-1$

	protected static final String PROP_USERLANGUAGE = "user.language"; //$NON-NLS-1$

	public static final IPathFilter ANY_FILTER = new IPathFilter() {
		@Override
		public boolean accept(String path) {
			return true;
		}
	};

	public Loader() {
	}

	protected boolean basicLoad(File file, boolean readOnly, boolean recursive,
			String path, IPathFilter filter) throws IOException {
		if (file.isDirectory()) {
			return basicLoadDirectory(file, readOnly, recursive, path, filter);
		} else {
			return basicLoadFile(file, readOnly, path);
		}
	}

	protected boolean basicLoadDirectory(File file, boolean readOnly,
			boolean recursive, String path, IPathFilter filter)
			throws IOException {
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File childFile = files[i];
			if (recursive) {
				String newPath = path;
				if (childFile.isDirectory()) {
					newPath = path + childFile.getName() + PATH_SEPARATOR;
				}
				if (StringTools.isEmpty(newPath) || filter.accept(newPath)) {
					basicLoad(childFile, readOnly, recursive, newPath, filter);
				}
			} else {
				if (childFile.isFile()) {
					if (StringTools.isEmpty(path) || filter.accept(path)) {
						basicLoadFile(childFile, readOnly, path);
					}
				}
			}
		}
		return true;
	}

	abstract protected boolean basicLoadFile(File file, boolean readOnly,
			String path) throws IOException;

	public boolean load(File file, boolean readOnly, boolean recursive)
			throws IOException {
		return load(file, readOnly, recursive, ANY_FILTER);
	}

	public boolean load(File file, boolean readOnly, boolean recursive,
			IPathFilter filter) throws IOException {
		if (file == null || !file.exists()) {
			return false;
		}
		return basicLoad(file, readOnly, recursive, StringTools.EMPTY, filter);
	}

	public boolean load(File parent, String filename, boolean readOnly,
			boolean recursive) throws IOException {
		return load(parent, filename, readOnly, recursive, ANY_FILTER);
	}

	public boolean load(File parent, String filename, boolean readOnly,
			boolean recursive, IPathFilter filter) throws IOException {
		if (filename == null) {
			return false;
		}
		File file = new File(filename);
		if (!file.isAbsolute()) {
			file = new File(parent, filename);
		}
		return load(file, readOnly, recursive, filter);
	}

	public boolean loadNLS(File file, boolean readOnly, boolean recursive)
			throws IOException {
		if (file == null || !file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			String language = System.getProperty(PROP_USERLANGUAGE);
			File languageDir = new File(file, language);
			if (load(languageDir, readOnly, recursive)) {
				return true;
			}
			File defaultDir = new File(file, DEFAULT_LANGUAGE);
			return load(defaultDir, readOnly, recursive);
		} else {
			File parent = file.getParentFile();
			if (parent == null) {
				return false;
			}
			String basename = FileTools.getBaseName(file);
			String extension = FileTools.getExtension(file);
			String language = System.getProperty(PROP_USERLANGUAGE);
			File languageFile = new File(parent, basename + "_" + language //$NON-NLS-1$
					+ "." + extension); //$NON-NLS-1$
			if (file.exists()
					&& basicLoadFile(languageFile, readOnly, StringTools.EMPTY)) {
				return true;
			}
			return basicLoadFile(file, readOnly, StringTools.EMPTY);
		}
	}

	public boolean loadNLS(File parent, String filename, boolean readOnly,
			boolean recursive) throws IOException {
		if (filename == null) {
			return false;
		}
		File file = new File(filename);
		if (!file.isAbsolute()) {
			file = new File(parent, filename);
		}
		return loadNLS(file, readOnly, recursive);
	}

}
