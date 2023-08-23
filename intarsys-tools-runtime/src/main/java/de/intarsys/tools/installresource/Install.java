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
package de.intarsys.tools.installresource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.system.SystemTools;
import de.intarsys.tools.url.URLTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

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
public abstract class Install {

	private static String PlatformId;

	private static String PlatformFallbackId;

	private static String PlatformDefaultId;

	private static ILogger Log = LogTools.getLogger(Install.class);

	protected static void copy(URL url, File file) throws IOException, FileNotFoundException {
		InputStream is = null;
		FileOutputStream os = null;
		try {
			is = url.openStream();
			os = new FileOutputStream(file);
			StreamTools.copy(is, os);
		} catch (IOException e) {
			throw new IOException("resource '" + url.getFile() + "' load error", e); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	protected static String createPlatformFallbackId() {
		String[] split = SystemTools.getOSName().split("\\s"); //$NON-NLS-1$
		return (split[0] + "-" + SystemTools.getOSArch()).toLowerCase(); //$NON-NLS-1$
	}

	protected static String createPlatformId() {
		return (SystemTools.getOSName() + "-" + SystemTools.getOSArch()) //$NON-NLS-1$
				.toLowerCase();
	}

	/**
	 * Mark file and all descendents subject to delete.
	 * 
	 * @param file
	 */
	protected static void deleteOnExit(File file) {
		if (file == null) {
			return;
		}
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					File child = children[i];
					deleteOnExit(child);
				}
			}
		}
		file.deleteOnExit();
	}

	public static String getPlatformDefaultId() {
		if (PlatformDefaultId == null) {
			return "default"; //$NON-NLS-1$
		}
		return PlatformDefaultId;
	}

	public static String getPlatformFallbackId() {
		if (PlatformFallbackId == null) {
			return createPlatformFallbackId();
		}
		return PlatformFallbackId;
	}

	public static String getPlatformId() {
		if (PlatformId == null) {
			return createPlatformId();
		}
		return PlatformId;
	}

	public static void setPlatformDefaultId(String value) {
		PlatformDefaultId = value;
	}

	public static void setPlatformFallbackId(String value) {
		PlatformFallbackId = value;
	}

	public static void setPlatformId(String value) {
		PlatformId = value;
	}

	private boolean platformDependent;

	protected final String name;

	protected final String path;

	protected File[] files;

	protected ClassLoader classLoader;

	protected Install(String path, String name, boolean platformDependent) {
		super();
		String tempPath = PathTools.withoutLeadingSeparator(path);
		tempPath = URLTools.withoutTrailingSeparator(tempPath);
		this.path = tempPath;
		this.name = name;
		this.platformDependent = platformDependent;
	}

	/**
	 * Delete the temporary installation.
	 */
	public void delete() throws IOException {
		if (files == null) {
			return;
		}
		for (File file : files) {
			FileTools.deleteRecursivly(file);
		}
	}

	/**
	 * Find all URL's to a specific resource.
	 * 
	 * @param name
	 * @return
	 */
	protected Iterator<URL> find(String name) {
		Iterator<URL> urls = null;
		try {
			String resourceName = null;
			if (isPlatformDependent()) {
				resourceName = getResourceNameFull(name);
				Log.trace("{} search platform dependent {}", getLogLabel(), resourceName);
				urls = open(resourceName);
				if (!urls.hasNext()) {
					resourceName = getResourceNameFallback(name);
					Log.trace("{} search platform dependent {}", getLogLabel(), resourceName);
					urls = open(resourceName);
					if (!urls.hasNext()) {
						resourceName = getResourceNameDefault(name);
						Log.trace("{} search platform dependent {}", getLogLabel(), resourceName);
						urls = open(resourceName);
					}
				}
			} else {
				resourceName = getResourceNamePlain(name);
				Log.trace("{} search {}", getLogLabel(), resourceName);
				urls = open(resourceName);
			}
		} catch (Exception e) {
			urls = Collections.emptyIterator();
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

	protected Object getLogLabel() {
		return getClass().getName() + " " + getPath() + ", " + getName();
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	protected String getResourceNameDefault(String name) {
		return getPlatformDefaultId() + "/" + name; //$NON-NLS-1$
	}

	protected String getResourceNameFallback(String name) {
		return getPlatformFallbackId() + "/" + name; //$NON-NLS-1$
	}

	protected String getResourceNameFull(String name) {
		return getPlatformId() + "/" + name; //$NON-NLS-1$
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
		Log.trace("{} load", getLogLabel());
		Iterator<URL> urls = find(getName());
		if (urls.hasNext()) {
			URL url = urls.next();
			try {
				Log.trace("{} load resource {}", getLogLabel(), url);
				File file = loadURL(url);
				deleteOnExit(file);
				files = new File[] { file };
				return file;
			} catch (IOException e) {
				Log.trace("{} load resource {} failed ({})", getLogLabel(), url, ExceptionTools.getMessage(e));
				throw e;
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
		Log.trace("{} loadAll", getLogLabel());
		List<File> tempFiles = new ArrayList<>();
		Iterator<URL> urls = find(getName());
		while (urls.hasNext()) {
			URL url = urls.next();
			try {
				Log.trace("{} loadAll resource {}", getLogLabel(), url);
				File file = loadURL(url);
				deleteOnExit(file);
				tempFiles.add(file);
			} catch (Exception e) {
				Log.trace("{} loadAll resource {} failed ({})", getLogLabel(), url, ExceptionTools.getMessage(e));
				throw e;
			}
		}
		files = tempFiles.toArray(new File[tempFiles.size()]);
		return files;
	}

	protected abstract File loadURL(URL url) throws IOException;

	@SuppressWarnings("java:S2112")
	protected Iterator<URL> open(String name) throws IOException {
		String resourceName = prefix(name);
		Enumeration<URL> urls = getClassLoader().getResources(resourceName);
		if (urls.hasMoreElements()) {
			// compact list, because one resource will be listed as two, if it
			// is reachable by the parent classloader also.
			HashSet<URL> urlSet = new HashSet<>();
			urlSet.addAll(Collections.list(urls));
			return urlSet.iterator();
		} else {
			return Collections.emptyIterator();
		}
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
