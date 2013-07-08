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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import de.intarsys.tools.file.TempTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * An abstraction to access a directory structure to be deployed along with the
 * application via classloader. Initially a ZIP file is searched. The ZIP is
 * expanded to a temporary directory.
 */
public class InstallZip extends Install {

	public InstallZip(String path, String name, boolean platformDependent) {
		super(path, name, platformDependent);
	}

	protected void loadEntry(File parent, ZipFile zipFile, ZipEntry entry)
			throws IOException, FileNotFoundException {
		InputStream is = null;
		FileOutputStream os = null;
		String entryName = entry.getName();
		File entryFile = new File(parent, entryName);
		if (entry.isDirectory()) {
			entryFile.mkdirs();
		} else {
			File entryDir = entryFile.getParentFile();
			if (entryDir != null && !entryDir.exists()) {
				entryDir.mkdirs();
			}
			is = zipFile.getInputStream(entry);
			os = new FileOutputStream(entryFile);
			StreamTools.copyStream(is, os);
		}
	}

	@Override
	protected File loadURL(URL url) throws IOException {
		// create temporary zip file
		File file = TempTools.createTempFile("file", getName());
		copy(url, file);
		deleteOnExit(file);
		// fill new temporary directory
		File dir = TempTools.createTempDir("dir", getName());
		loadZip(file, dir);
		return dir;
	}

	protected void loadZip(File zip, File parent) throws ZipException,
			IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zip);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				loadEntry(parent, zipFile, entry);
			}
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}
}
