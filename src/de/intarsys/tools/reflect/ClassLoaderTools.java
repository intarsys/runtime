package de.intarsys.tools.reflect;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import de.intarsys.tools.string.StringTools;

/**
 * Tool methods for handling {@link ClassLoader} instances.
 */
public class ClassLoaderTools {

	public static ClassLoader createClassLoader(ClassLoader parent,
			File baseDir, String classpath, String classpathScan,
			String classpathScanExclude) {
		URL[] urlArray = createURLs(baseDir, classpath, classpathScan,
				classpathScanExclude);
		URLClassLoader classLoader = new URLClassLoader(urlArray, parent);
		return classLoader;
	}

	public static void createURL(List<URL> urls, File file) {
		if (file.isFile()) {
			try {
				urls.add(new URL("file", "", file.getAbsolutePath())); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (MalformedURLException e) {
				// couldn't possibly happen
			}
		} else {
			try {
				urls.add(new URL("file", "", file.getAbsolutePath() //$NON-NLS-1$ //$NON-NLS-2$
						+ "/")); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				// couldn't possibly happen
			}
		}
	}

	public static URL[] createURLs(File baseDir, String classpath,
			String classpathScan, String classpathScanExclude) {
		List<URL> urls = new ArrayList<URL>();
		//
		if (!StringTools.isEmpty(classpath)) {
			String[] names = classpath.split(";");
			for (String name : names) {
				File file = new File(name);
				if (!file.isAbsolute()) {
					file = new File(baseDir, name);
				}
				createURL(urls, file);
			}
		}
		//
		if (!StringTools.isEmpty(classpathScan)) {
			String[] names = classpathScan.split(";");
			List<String> exclude;
			if (StringTools.isEmpty(classpathScanExclude)) {
				exclude = new ArrayList<String>();
			} else {
				exclude = Arrays.asList(classpathScanExclude.split(";"));
			}
			for (String name : names) {
				File file = new File(name);
				if (!file.isAbsolute()) {
					file = new File(baseDir, name);
				}
				createURLScanJar(urls, file, exclude);
			}
		}
		//
		URL[] urlArray = urls.toArray(new URL[urls.size()]);
		return urlArray;
	}

	public static URL[] createURLs(URL codebase, String classpath,
			boolean relativeOnly) throws IOException {
		String codebaseString = codebase.toString();
		List<URL> urls = new ArrayList<URL>();
		//
		if (!StringTools.isEmpty(classpath)) {
			StringTokenizer st = new StringTokenizer(classpath, ";");
			while (st.hasMoreElements()) {
				String name = st.nextToken();
				URL newUrl = new URL(codebase, name.trim());
				if (relativeOnly) {
					if (!newUrl.toString().startsWith(codebaseString)) {
						throw new IOException("url " + newUrl
								+ " not relative to " + codebaseString);
					}
				}
				urls.add(newUrl);
			}
		}
		//
		URL[] urlArray = urls.toArray(new URL[urls.size()]);
		return urlArray;
	}

	public static void createURLScanJar(List<URL> result, File directory,
			final List<String> exclude) {
		File[] jarFiles = directory.listFiles(new FileFilter() {
			public boolean accept(File path) {
				return (path.isFile() && path.canRead()
						&& path.getName().toLowerCase().endsWith(".jar") && !exclude
						.contains(path.getName()));
			}
		});
		if (jarFiles != null) {
			for (File file : jarFiles) {
				try {
					result.add(new URL("file", "", file.getAbsolutePath())); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (MalformedURLException e) {
					// couldn't possibly happen
				}
			}
		}
	}

	public static void extendClassLoader(ClassLoader classLoader, URL url) {
		try {
			Method addUrl;
			try {
				addUrl = classLoader.getClass().getDeclaredMethod("addURL", //$NON-NLS-1$
						URL.class);
			} catch (Exception e) {
				//
				addUrl = classLoader.getClass().getDeclaredMethod("addAppURL", //$NON-NLS-1$
						URL.class);
			}
			addUrl.setAccessible(true);
			addUrl.invoke(classLoader, url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
