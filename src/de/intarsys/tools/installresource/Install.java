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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.system.SystemTools;

/**
 * Abstract superclass for implementing file resource deployment behavior.
 * <p>
 * This is a useful package to deploy file based resources along with the
 * classloader. Files or directories are detected via classloader and copied
 * temporarily to the file system. Here you can access the resources locally -
 * for example for loading a native library.
 * <p>
 * Platform dependent resources are supported via subdirectories.
 * <p>
 * When loading a non platform dependent resource, the file is searched in the
 * class loader with getResourceAsStream(x) where x is
 * <ul>
 * <li>[path]/[filename]</li>
 * </ul>
 * 
 * When loading a platform dependent resource, the file is searched in the class
 * loader with getResourceAsStream(x) where x is one of
 * <ul>
 * <li>[path]/[os.name]-[os.arch]/[filename]</li>
 * <li>[path]/[os.short name]-[os.arch]/[filename]</li>
 * <li>[path]/default/[filename]</li>
 * </ul>
 * 
 * In this definition
 * 
 * <ul>
 * <li>[path] is a path prefix defined upon creation of an {@link Install}
 * instance</li>
 * <li>[os.name] is the System property os.name in lowercase</li>
 * <li>[os.short name] is derived from the System property os.name by using the
 * beginning up to the first whitespace in lowercase</li>
 * <li>[os.arch] is the System property os.arch in lowercase</li>
 * <li>[filename] is the name defined upon object creation</li>
 * </ul>
 * 
 * For example, with InstallFile("foo", "bar.dll", true) on a Windows Vista
 * machine you will search for:
 * 
 * <ul>
 * <li>foo/windows vista-x86/bar.dll</li>
 * <li>foo/windows-x86/bar.dll</li>
 * <li>foo/default/bar.dll</li>
 * </ul>
 * 
 */
abstract public class Install {

	private static String platformId;

	private static String platformFallbackId;

	private static String platformDefaultId;

	static protected void copy(URL url, File file) throws IOException,
			FileNotFoundException {
		InputStream is = url.openStream();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			StreamTools.copyStream(is, os);
		} catch (IOException e) {
			throw ExceptionTools.createIOException("resource '" + url.getFile() //$NON-NLS-1$
					+ "' load error", e); //$NON-NLS-1$
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	protected static String createPlatformFallbackId() {
		String[] split = SystemTools.getOSName().split("\\s");
		return (split[0] + "-" + SystemTools.getOSArch()).toLowerCase();
	}

	protected static String createPlatformId() {
		return (SystemTools.getOSName() + "-" + SystemTools.getOSArch())
				.toLowerCase();
	}

	/**
	 * Mark file and all descendents subject to delete.
	 * 
	 * @param file
	 */
	static protected void deleteOnExit(File file) {
		if (file == null) {
			return;
		}
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				File child = children[i];
				deleteOnExit(child);
			}
		}
		file.deleteOnExit();
	}

	public static String getPlatformDefaultId() {
		if (platformDefaultId == null) {
			return "default";
		}
		return platformDefaultId;
	}

	public static String getPlatformFallbackId() {
		if (platformFallbackId == null) {
			return createPlatformFallbackId();
		}
		return platformFallbackId;
	}

	public static String getPlatformId() {
		if (platformId == null) {
			return createPlatformId();
		}
		return platformId;
	}

	public static void setPlatformDefaultId(String value) {
		platformDefaultId = value;
	}

	public static void setPlatformFallbackId(String value) {
		platformFallbackId = value;
	}

	public static void setPlatformId(String value) {
		platformId = value;
	}

	private boolean platformDependent = false;

	protected final String name;

	protected final String path;

	protected File[] files;

	protected ClassLoader classLoader;

	public Install(String path, String name, boolean platformDependent) {
		super();
		String tempPath = path;
		if (StringTools.isEmpty(tempPath)) {
			tempPath = "";
		} else {
			if (tempPath.endsWith("/")) {
				tempPath = tempPath.substring(0, tempPath.length() - 1);
			}
			if (tempPath.startsWith("/")) {
				tempPath = tempPath.substring(1);
			}
		}
		this.path = tempPath;
		this.name = name;
		this.platformDependent = platformDependent;
	}

	/**
	 * Delete the temporary installation.
	 * 
	 * @return <code>true</code> if all artifacts are deleted.
	 */
	public boolean delete() {
		if (files == null) {
			return true;
		}
		boolean deleted = true;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			deleted = deleted && FileTools.deleteRecursivly(file);
		}
		return deleted;
	}

	/**
	 * Find all URL's to a specific resource.
	 * 
	 * @param name
	 * @return
	 */
	protected Enumeration<URL> find(String name) {
		Enumeration urls = null;
		try {
			if (isPlatformDependent()) {
				urls = open(getResourceNameFull(name));
				if (!urls.hasMoreElements()) {
					urls = open(getResourceNameFallback(name));
					if (!urls.hasMoreElements()) {
						urls = open(getResourceNameDefault(name));
					}
				}
			} else {
				urls = open(getResourceNamePlain(name));
			}
		} catch (Exception e) {
			//
		}
		return urls;
	}

	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			ClassLoader result = Thread.currentThread().getContextClassLoader();
			if (result == null) {
				result = getClass().getClassLoader();
			}
			return result;
		}
		return classLoader;
	}

	public File getFile() {
		if (files == null || files.length == 0) {
			return null;
		}
		return files[0];
	}

	public File[] getFiles() {
		return files;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	protected String getResourceNameDefault(String name) {
		return getPlatformDefaultId() + "/" + name;
	}

	protected String getResourceNameFallback(String name) {
		return getPlatformFallbackId() + "/" + name;
	}

	protected String getResourceNameFull(String name) {
		return getPlatformId() + "/" + name;
	}

	protected String getResourceNamePlain(String name) {
		return name;
	}

	public boolean isPlatformDependent() {
		return platformDependent;
	}

	/**
	 * Load the first occurrence of the designated target from the classloader
	 * and save it as a local temporary resource. The path to this resource is
	 * returned.
	 * 
	 * @return Load the first occurrence of the designated target from the
	 *         classloader.
	 * @throws IOException
	 */
	public File load() throws IOException {
		Enumeration<URL> urls = find(getName());
		if (urls != null) {
			if (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				File file = loadURL(url);
				deleteOnExit(file);
				files = new File[] { file };
				return file;
			}
		}
		return null;
	}

	/**
	 * Load all occurrences of the designated target from the classloader.
	 * 
	 * @return Load all occurrences of the designated target from the
	 *         classloader.
	 * @throws IOException
	 */
	public File[] loadAll() throws IOException {
		List<File> tempFiles = new ArrayList<File>();
		Enumeration<URL> urls = find(getName());
		if (urls != null) {
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				File file = loadURL(url);
				deleteOnExit(file);
				tempFiles.add(file);
			}
		}
		files = tempFiles.toArray(new File[tempFiles.size()]);
		return files;
	}

	abstract protected File loadURL(URL url) throws IOException;

	protected Enumeration<URL> open(String name) throws IOException {
		String resourceName = prefix(name);
		return getClassLoader().getResources(resourceName);
	}

	protected String prefix(String name) {
		StringBuilder sb = new StringBuilder();
		if (getPath() != null) {
			sb.append(getPath());
			if (sb.length() > 0) {
				sb.append("/"); //$NON-NLS-1$
			}
		}
		sb.append(name);
		return sb.toString();
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
