/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.intarsys.tools.lang.Aliases;
import de.intarsys.tools.string.StringTools;

/**
 * Tool class to enhance the reflective capabilities about classes.
 */
public class ClassTools {
	/** The string used to separate paths in a list of path definitions */
	private static final String PATH_SEPARATOR = System.getProperty("path.separator"); //$NON-NLS-1$

	private static final Map<String, Class<?>> PRIMITIVE_TYPES = new HashMap<>();

	static {
		PRIMITIVE_TYPES.put("int", Integer.TYPE);
		PRIMITIVE_TYPES.put("long", Long.TYPE);
		PRIMITIVE_TYPES.put("double", Double.TYPE);
		PRIMITIVE_TYPES.put("float", Float.TYPE);
		PRIMITIVE_TYPES.put("boolean", Boolean.TYPE);
		PRIMITIVE_TYPES.put("char", Character.TYPE);
		PRIMITIVE_TYPES.put("byte", Byte.TYPE);
		PRIMITIVE_TYPES.put("void", Void.TYPE);
		PRIMITIVE_TYPES.put("short", Short.TYPE);
	}

	/**
	 * Create a list of all classes that are available from classPath.
	 * 
	 * <p>
	 * Be aware that the classes are loaded and so any static code is executed.
	 * </p>
	 * 
	 * @param classPath
	 *            The classpath to inspect
	 * 
	 * @return A list of all classes that are available from classPath.
	 * 
	 * @throws IOException
	 */
	public static List<Class<?>> collectClasses(String classPath) throws IOException {
		List<String> classNames = collectClassNames(classPath);
		ClassLoader loader = createClassLoader(classPath);
		List<Class<?>> classes = new ArrayList<>();
		for (Iterator<String> it = classNames.iterator(); it.hasNext();) {
			String className = it.next();
			try {
				classes.add(loader.loadClass(className));
			} catch (ClassNotFoundException e) {
				// ignore
			}
		}
		return classes;
	}

	/**
	 * Create a list of class names in dot notation that can be found in the
	 * classPath.
	 * 
	 * @param classPath
	 *            The classpath to inspect
	 * 
	 * @return A list of class names in dot notation that can be found in the
	 *         classPath.
	 * 
	 * @throws IOException
	 */
	public static List<String> collectClassNames(String classPath) throws IOException {
		List<String> classnames = new ArrayList<>();
		StringTokenizer path = new StringTokenizer(classPath, PATH_SEPARATOR);
		int pathCount = path.countTokens();
		for (int ii = 0; ii < pathCount; ii++) {
			String currentName = path.nextToken();
			File currentFile = new File(currentName);
			if (currentFile.isFile()) {
				extractClassNamesFromJar(classnames, currentFile);
			} else {
				extractClassNamesFromDir(classnames, currentFile, "");
			}
		}
		return classnames;
	}

	public static <T> Class<T> createClass(String className, Class<T> expectedClass, ClassLoader classLoader)
			throws ObjectCreationException {
		if (StringTools.isEmpty(className)) {
			throw new ObjectCreationException("class name missing");
		}
		className = Aliases.get().resolve(className.trim());
		try {
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			}
			if (classLoader == null) {
				// as good as any
				if (expectedClass == null) {
					classLoader = ClassTools.class.getClassLoader();
				} else {
					classLoader = expectedClass.getClassLoader();
				}
			}
			Class<?> clazz = PRIMITIVE_TYPES.get(className);
			if (clazz == null) {
				clazz = Class.forName(className, false, classLoader);
			}
			if (expectedClass != null && !expectedClass.isAssignableFrom(clazz)) {
				throw new ObjectCreationException(
						"class '" + clazz.getName() + "' not compatible with expected type '" + expectedClass + "'");
			}
			return (Class<T>) clazz;
		} catch (Throwable e) {
			// Class.forName may throw Error when class name mismatched !!
			throw new ObjectCreationException("class '" + className + "' not found", e);
		}
	}

	/**
	 * Create a new ClassLoader on the specified classpath
	 * 
	 * @param classPath
	 *            The classpath we want to load from.
	 * 
	 * @return A new ClassLoader on the specified classpath
	 */
	public static ClassLoader createClassLoader(String classPath) {
		URL[] allURLs = null;
		StringTokenizer path = new StringTokenizer(classPath, PATH_SEPARATOR);
		int i = path.countTokens();
		allURLs = new URL[i + 1];
		for (int ii = 0; ii < i; ii++) {
			String currentPath = path.nextToken();
			try {
				URL url = new URL("file", "", currentPath);
				allURLs[ii] = url;
			} catch (MalformedURLException e) {
				// should not happen
			}
		}
		ClassLoader loader = URLClassLoader.newInstance(allURLs);
		return loader;
	}

	/**
	 * Create a list of all classes "current" and its subdirectories.
	 * 
	 * @param result
	 *            The collection that is filled with new classnames
	 * @param currentFile
	 *            The directory/file under inspection
	 * @param relativePath
	 *            The path extending from the initial root we are currently
	 *            inspecting.
	 * 
	 * @throws IOException
	 */
	protected static void extractClassNamesFromDir(List<String> result, File currentFile, String relativePath)
			throws IOException {
		if (currentFile.isFile()) {
			String filename = currentFile.getName();
			if (filename.endsWith(".class")) {
				String className = filename.substring(0, filename.length() - 6);
				result.add(relativePath + className);
			}
		} else {
			String[] allFiles = currentFile.list();
			if (allFiles == null) {
				throw new IOException("error creating directory listing for " + currentFile.getAbsolutePath());
			}
			for (int i = 0; i < allFiles.length; i++) {
				File child = new File(currentFile, allFiles[i]);
				extractClassNamesFromDir(result, child, relativePath + allFiles[i] + ".");
			}
		}
	}

	/**
	 * Create a list of all ".class" entries in the jar file.
	 * 
	 * @param result
	 *            The collection that is filled with new classnames
	 * @param file
	 *            The jar file we are inspecting.
	 * 
	 * @throws IOException
	 */
	protected static void extractClassNamesFromJar(List<String> result, File file) throws IOException {
		JarFile jar = null;
		try {
			jar = new JarFile(file);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".class")) {
					String className = entryName.substring(0, entryName.length() - 6);
					className = className.replace('/', '.');
					result.add(className);
				}
			}
		} finally {
			if (jar != null) {
				jar.close();
			}
		}
	}

	/**
	 * Search for a constant (static field) name in the class for the given value.
	 * If the class itself doesn't declare the constant, its interfaces and
	 * subclasses are also searched.
	 *
	 * <ul>
	 * <li><code>null</code> constants will not return a result.</li>
	 * <li>If a constant value exists more than once, the name of the first declared
	 * constant will be returned.</li>
	 * </ul>
	 *
	 *
	 * @param clazz       class or interface to search
	 * @param searchValue constant to search for
	 * @return
	 */
	public static String getConstantName(Class<?> clazz, Object searchValue) {
		if (searchValue == null) {
			return null;
		}
		Optional<String> constantName = Arrays.stream(clazz.getDeclaredFields()) //
				.filter((field) -> Modifier.isStatic(field.getModifiers())) //
				.filter((field) -> {
					try {
						Object constantValue = field.get(null);
						return constantValue != null && constantValue.equals(searchValue);
					} catch (IllegalAccessException e) {
						return false;
					}
				}) //
				.findFirst() //
				.map((field) -> field.getName());
		if (constantName.isPresent()) {
			return constantName.get();
		}
		for (Class<?> interfac : clazz.getInterfaces()) {
			String name = getConstantName(interfac, searchValue);
			if (name != null) {
				return name;
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			return getConstantName(clazz.getSuperclass(), searchValue);
		}
		return null;
	}

	public static URL getContainerResourceUrl(Class<?> clazz) {
		String tmpName = "/" + clazz.getName().replace(".", "/") + ".class";
		URL resourceUrl = clazz.getResource(tmpName);
		return getContainerResourceUrl(resourceUrl);
	}

	public static URL getContainerResourceUrl(URL resourceUrl) {
		if (resourceUrl.getProtocol().equals("jar")) {
			String tmpPath = resourceUrl.toString();
			tmpPath = tmpPath.substring(4);
			int tmpPos = tmpPath.indexOf("!");
			if (tmpPos >= 0) {
				tmpPath = tmpPath.substring(0, tmpPos);
			}
			try {
				return new URL(tmpPath);
			} catch (MalformedURLException e) {
				//
			}
		} else if (resourceUrl.getProtocol().equals("file")) {
			String tmpPath = resourceUrl.toString();
			int tmpPos = tmpPath.indexOf("/WEB-INF/");
			if (tmpPos >= 0) {
				tmpPath = tmpPath.substring(0, tmpPos + 1);
				try {
					return new URL(tmpPath);
				} catch (MalformedURLException e) {
					//
				}
			}
		}
		return resourceUrl;
	}

	public static String getPackageName(Class<?> clazz) {
		String name = clazz.getName();
		return getPackageName(name);
	}

	public static String getPackageName(String name) {
		int pos = name.lastIndexOf('.');
		if (pos >= 0) {
			return name.substring(0, pos);
		}
		// default package
		return "";
	}

	/**
	 * Return the unqualified name of the class.
	 * 
	 * The unqualified name is the string after the last '.' in the class name.
	 * 
	 * Example:
	 * StringTools.getUnqualifiedName(String.class) -> String
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getUnqualifiedName(Class<?> clazz) {
		String name = clazz.getName();
		return getUnqualifiedName(name);
	}

	/**
	 * Return the unqualified name of the class name.
	 * 
	 * The unqualified name is the string after the last '.' in the class name.
	 * 
	 * Example:
	 * StringTools.getUnqualifiedName("java.lang.String") -> String
	 * 
	 * @param name
	 * @return
	 */
	public static String getUnqualifiedName(String name) {
		int pos = name.lastIndexOf('.');
		if (pos >= 0) {
			return name.substring(pos + 1);
		}
		return name;
	}

	/**
	 * 
	 */
	private ClassTools() {
		super();
	}
}
