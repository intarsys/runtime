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
package de.intarsys.tools.installresource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import de.intarsys.tools.file.TempTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * An abstraction to access a directory structure to be deployed along with the
 * application via the class loader. Initially a file containing a list of
 * filenames is searched. All files referenced in the list are created in a
 * common directory.
 */
public class InstallFileList extends Install {

	public InstallFileList(String path, String name, boolean platformDependent) {
		super(path, name, platformDependent);
	}

	protected void loadEntry(File parent, String name) throws IOException {
		if (name.length() == 0 || name.startsWith("#")) { //$NON-NLS-1$
			return;
		}
		Enumeration<URL> urls = find(name);
		if (urls != null) {
			if (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				File entryFile = new File(parent, name);
				File entryDir = entryFile.getParentFile();
				if (entryDir != null && !entryDir.exists()) {
					entryDir.mkdirs();
				}
				copy(url, entryFile);
			}
		}
	}

	protected void loadList(File parent, InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = is.read();
		while (i != -1) {
			if (i == '\n') {
				String entryName = sb.toString().trim();
				loadEntry(parent, entryName);
				sb.setLength(0);
				i = is.read();
				continue;
			}
			sb.append((char) i);
			i = is.read();
		}
		String entryName = sb.toString().trim();
		loadEntry(parent, entryName);
	}

	@Override
	protected File loadURL(URL url) throws IOException {
		InputStream is = url.openStream();
		try {
			File file = TempTools.createTempDir("dir", getName());
			loadList(file, is);
			return file;
		} finally {
			StreamTools.close(is);
		}
	}
}
