package de.intarsys.tools.reflect;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Tool methods for handling {@link ClassLoader} instances.
 */
public final class ClassLoaderTools {

	private static final ILogger Log = LogTools.getLogger(ClassLoaderTools.class);

	/**
	 * Add {@code file} as a valid {@link URL} to the list of {@code urls}.
	 * 
	 * @param urls
	 * @param file
	 */
	public static void collectURLsFile(List<URL> urls, File file) {
		URL url = toURL(file);
		if (url != null) {
			Log.debug("collectURLsFile({})", url);
			urls.add(url);
		}
	}

	/**
	 * Add all ";" separated {@code path } segments as URLs into {@code urls}. Each path segment is treated as relative
	 * to {@code baseDir}.
	 * 
	 * @param urls
	 * @param baseDir
	 * @param path
	 */
	public static void collectURLsPath(List<URL> urls, File baseDir, String path) {
		if (!StringTools.isEmpty(path)) {
			Log.debug("collectURLsClasspath({}, {})", baseDir, path);
			String[] names = path.split(";");
			for (String name : names) {
				File file = new File(name);
				if (!file.isAbsolute()) {
					file = new File(baseDir, name);
				}
				collectURLsFile(urls, file);
			}
		}
	}

	/**
	 * Add all ".jar" artifacts in {@code directory} to {@code urls}, except artifacts that are listed in
	 * {@code exclude}.
	 * 
	 * @param urls
	 * @param directory
	 * @param exclude
	 */
	public static void collectURLsScan(List<URL> urls, File directory, final List<String> exclude) {
		Log.debug("collectURLsScan({})", directory);
		File[] jarFiles = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File path) {
				return (path.isFile() && path.canRead() && path.getName().toLowerCase().endsWith(".jar")
						&& !exclude.contains(path.getName()));
			}
		});
		if (jarFiles != null) {
			for (File file : jarFiles) {
				collectURLsFile(urls, file);
			}
		}
	}

	/**
	 * Collect all *.jar resources from the directories denoted in the ";" separated {@code scanPath}, except artifacts
	 * that are listed in {@code scanExclude}. Path segments that are not absolute are resolved relative to
	 * {@code baseDir}.
	 * 
	 * @param urls
	 * @param baseDir
	 * @param scanPath
	 * @param scanExclude
	 */
	public static void collectURLsScan(List<URL> urls, File baseDir, String scanPath, String scanExclude) {
		if (!StringTools.isEmpty(scanPath)) {
			Log.debug("collectURLsScan({}, {}, {})", baseDir, scanPath, scanExclude);
			String[] names = scanPath.split(";");
			List<String> exclude;
			if (StringTools.isEmpty(scanExclude)) {
				exclude = new ArrayList<>();
			} else {
				exclude = Arrays.asList(scanExclude.split(";"));
			}
			for (String name : names) {
				File file = new File(name);
				if (!file.isAbsolute()) {
					file = new File(baseDir, name);
				}
				collectURLsScan(urls, file, exclude);
			}
		}
	}

	/**
	 * Create a {@link ClassLoader} from the array of {@code files}.
	 * 
	 * @param parent
	 * @param files
	 * @return
	 */
	public static ClassLoader createClassLoader(ClassLoader parent, File... files) {
		return new URLClassLoader(toURLs(files), parent);
	}

	/**
	 * Create a {@link ClassLoader} based on parent using the algorithms in
	 * {@link #collectURLsPath(List, File, String)} and {@link #collectURLsScan(List, File, String, String)}.
	 * 
	 * @param parent
	 * @param baseDir
	 * @param classpath
	 * @param classpathScan
	 * @param classpathScanExclude
	 * @return
	 */
	public static ClassLoader createClassLoader(ClassLoader parent, File baseDir, String classpath,
			String classpathScan, String classpathScanExclude) {
		List<URL> urls = new ArrayList<>();
		collectURLsPath(urls, baseDir, classpath);
		collectURLsScan(urls, baseDir, classpathScan, classpathScanExclude);
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
	}

	/**
	 * Create a {@link ClassLoader} from a {@link List} of {@link URL} instances.
	 * 
	 * @param parent
	 * @param urls
	 * @return
	 */
	public static ClassLoader createClassLoader(ClassLoader parent, List<URL> urls) {
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
	}

	/**
	 * @return Any plausible {@link ClassLoader} for this execution context.
	 * 
	 */
	public static ClassLoader defaultClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

	/**
	 * "Tweak" the current {@link ClassLoader} by adding new {@link URL} instances.
	 * 
	 * This is used for tweaking a servlet container {@link ClassLoader} in certain execution contexts.
	 * 
	 * @param classLoader
	 * @param url
	 */
	@SuppressWarnings("java:S3011")
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
			throw new IllegalArgumentException("cannot extend classloader", e);
		}
	}

	/**
	 * Parse a ";" separated {@code path} and return the respective segments as URLs.
	 * 
	 * @param path
	 * @return
	 */
	public static URL[] parseURLs(String path) {
		if (StringTools.isEmpty(path)) {
			return null; // NOSONAR
		}
		String[] names = path.split(";"); //$NON-NLS-1$
		return ClassLoaderTools.toURLs(names);
	}

	/**
	 * Convert a file to a {@link URL} suitable for a {@link ClassLoader}.
	 * 
	 * @param file
	 * @return
	 */
	public static URL toURL(File file) {
		if (file == null) {
			return null;
		}
		URL url = null;
		try {
			url = file.getAbsoluteFile().toURI().toURL();
		} catch (MalformedURLException e) {
			if (file.isFile()) {
				try {
					url = new URL("file", "", file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (MalformedURLException ex) {
					throw new IllegalStateException();
				}
			} else {
				try {
					url = new URL("file", "", file.getAbsolutePath() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch (MalformedURLException ex) {
					throw new IllegalStateException();
				}
			}
		}
		return url;
	}

	/**
	 * Convert all {@code files} to {@link URL}s.
	 * 
	 * @param files
	 * @return
	 */
	public static URL[] toURLs(File... files) {
		return Arrays.stream(files).map(file -> toURL(file)).toArray(URL[]::new);
	}

	/**
	 * Convert all {@code paths} to {@link URL}s.
	 * 
	 * @param paths
	 * @return
	 */
	public static URL[] toURLs(String... paths) {
		return Arrays.stream(paths).map(path -> toURL(new File(path))).toArray(URL[]::new);
	}

	private ClassLoaderTools() {
	}

}
