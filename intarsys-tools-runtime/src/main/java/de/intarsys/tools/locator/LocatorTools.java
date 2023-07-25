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
package de.intarsys.tools.locator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import de.intarsys.tools.adapter.AdapterTools;
import de.intarsys.tools.content.ICharsetAccess;
import de.intarsys.tools.content.ICharsetSupport;
import de.intarsys.tools.content.IMimeTypeAccess;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.digest.DigestTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.digest.IDigester;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.valueholder.IValueHolder;

/**
 * Tool methods for dealing with {@link ILocator}.
 * 
 */
public class LocatorTools {

	public static final String PATH_SEPARATOR = ";"; //$NON-NLS-1$

	private static final long MAX_BUFFER_SIZE = 1024L * 1024L * 10L;

	public static void checkHash(ILocator locator, Object hash) throws IOException {
		Objects.requireNonNull(locator);
		IDigest digest = DigestTools.createDigest(hash);
		if (digest == null) {
			return;
		}
		IDigest docDigest = digest(locator, digest.getAlgorithmName());
		if (!digest.equals(docDigest)) {
			throw new IOException("document digest validation failed (" + locator.getPath() + ")");
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static void checkHash(ILocator locator, String algorithmName, Object hash) throws IOException {
		checkHash(locator, hash);
	}

	/**
	 * Copy locator content.
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void copy(ILocator source, ILocator target) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = source.getInputStream();
			os = target.getOutputStream();
			StreamTools.copy(is, os);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	public static void copyRecursively(ILocator source, ILocator target) throws IOException {
		if (source.isDirectory()) {
			for (ILocator child : source.listLocators(null)) {
				copyRecursively(child, target.getChild(child.getName()));
			}
		} else {
			copy(source, target);
		}
	}

	/**
	 * A temporary {@link File} with a copy of the data from "locator".
	 * 
	 * @param locator
	 * @return The temporary file.
	 * @throws IOException
	 */
	public static File copyToTempFile(ILocator locator) throws IOException {
		File tempFile = FileTools.createTempFile(locator.getName());
		save(locator, tempFile);
		return tempFile;
	}

	public static ILocator createLocator(Object value, ILocatorFactory factory, ILocator defaultValue)
			throws IOException {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof ILocator) {
			return (ILocator) value;
		}
		if (value instanceof ILocatorSupport) {
			return ((ILocatorSupport) value).getLocator();
		}
		if (value instanceof IArgs) {
			ILocator resultLocator = null;
			IArgs tempArgs = (IArgs) value;
			String charset = ArgTools.getString(tempArgs, "charset", null);
			String contentType = ArgTools.getString(tempArgs, "contentType", null);
			Object tempLocator = tempArgs.get("locator");
			if (tempLocator == null) {
				tempLocator = tempArgs.get("path");
			}
			if (tempLocator != null) {
				resultLocator = createLocator(tempLocator, factory, null);
			}
			if (resultLocator == null) {
				String name = ArgTools.getString(tempArgs, "name", "locator.bytes");
				name = PathTools.getName(name);
				Object tempContent = tempArgs.get("text");
				if (tempContent == null) {
					tempContent = tempArgs.get("content");
					if (tempContent == null) {
						tempContent = tempArgs.get("bytes");
					}
					if (tempContent == null) {
						resultLocator = null;
					} else if (tempContent instanceof ILocator) {
						resultLocator = (ILocator) tempContent;
					} else if (tempContent instanceof byte[]) {
						resultLocator = new ByteArrayLocator((byte[]) tempContent, name);
					} else if (tempContent instanceof String) {
						byte[] bytes = Base64.decode((String) tempContent);
						resultLocator = new ByteArrayLocator(bytes, name);
					} else {
						try {
							byte[] bytes = ConverterRegistry.get().convert(tempContent, byte[].class);
							if (bytes == null) {
								resultLocator = null;
							} else {
								resultLocator = new ByteArrayLocator(bytes, name);
							}
						} catch (ConversionException e) {
							//
						}
						if (resultLocator == null) {
							try {
								String string = ConverterRegistry.get().convert(tempContent, String.class);
								byte[] bytes = Base64.decode(string);
								if (bytes == null) {
									resultLocator = null;
								} else {
									resultLocator = new ByteArrayLocator(bytes, name);
								}
							} catch (ConversionException e) {
								throw new IOException("can't create locator");
							}
						}
					}
				} else {
					resultLocator = new StringLocator(StringTools.safeString(tempContent), name);
				}
			}
			if (resultLocator != null) {
				if (charset != null) {
					ICharsetAccess charsetAccess = AdapterTools.getAdapter(resultLocator, ICharsetAccess.class, null);
					if (charsetAccess != null) {
						charsetAccess.setCharset(charset);
					}
				}
				if (contentType != null) {
					IMimeTypeAccess contentTypeAccess = AdapterTools.getAdapter(resultLocator, IMimeTypeAccess.class,
							null);
					if (contentTypeAccess != null) {
						contentTypeAccess.setContentType(contentType);
					}
				}
				try {
					IDigest digest = ArgTools.getDigest(tempArgs, "hash");
					LocatorTools.checkHash(resultLocator, digest);
				} catch (IOException e) {
					throw new IOException("locator hash error", e);
				}
				if (ArgTools.getBoolStrict(tempArgs, "attachArgs", false)) {
					resultLocator = new LocatorWithArgs(resultLocator, tempArgs);
				}
			}
			return resultLocator;
		}
		if (value instanceof byte[]) {
			return new ByteArrayLocator((byte[]) value, "locator.bytes");
		}
		if (factory == null) {
			factory = LocatorFactory.get();
		}
		if (value instanceof File) {
			return factory.createLocator(((File) value).getAbsolutePath());
		}
		if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return defaultValue;
			}
			return factory.createLocator((String) value);
		}
		try {
			return ConverterRegistry.get().convert(value, ILocator.class);
		} catch (ConversionException e) {
			throw new IOException("can't create locator", e);
		}
	}

	public static ILocatorFactory createLocatorFactory(Object context) {
		// if context already claims to be able to resolve, do not look any
		// further.
		if (context instanceof ILocatorFactory) {
			return (ILocatorFactory) context;
		}
		if (context instanceof ILocatorFactorySupport) {
			return ((ILocatorFactorySupport) context).getLocatorFactory();
		}
		SchemeBasedLocatorFactory sblf = new SchemeBasedLocatorFactory();
		if (context instanceof IFileEnvironment) {
			FileLocatorFactory flf = new FileLocatorFactory(new IValueHolder<File>() {

				@Override
				public File get() {
					return ((IFileEnvironment) context).getBaseDir();
				}

				@Override
				public File set(File newValue) {
					throw new UnsupportedOperationException();
				}
			});
			sblf.registerLocatorFactory("file", flf);
			sblf.setNoSchemeLocatorFactory(flf);
		}
		if (context instanceof IClassLoaderSupport) {
			ClassLoaderResourceLocatorFactory clrlf = new ClassLoaderResourceLocatorFactory(
					new IValueHolder<ClassLoader>() {

						@Override
						public ClassLoader get() {
							return ((IClassLoaderSupport) context).getClassLoader();
						}

						@Override
						public ClassLoader set(ClassLoader newValue) {
							throw new UnsupportedOperationException();
						}

					});
			sblf.registerLocatorFactory("classpath", clrlf);
		}
		return sblf;
	}

	public static ILocator[] createLocators(String paths, ILocatorFactory factory) throws IOException {
		if (factory == null) {
			factory = LocatorFactory.get();
		}
		List<ILocator> locators = new ArrayList<>();
		for (StringTokenizer t = new StringTokenizer(paths, PATH_SEPARATOR); t.hasMoreTokens();) {
			String path = t.nextToken();
			locators.add(factory.createLocator(path));
		}
		ILocator[] result = new ILocator[locators.size()];
		return locators.toArray(result);
	}

	public static ILocatorFactory createLookupFactory(Object... contexts) {
		LookupLocatorFactory llf = new LookupLocatorFactory();
		for (Object context : contexts) {
			ILocatorFactory factory = createLocatorFactory(context);
			if (factory != null) {
				llf.addLocatorFactory(factory);
			}
		}
		return llf;
	}

	/**
	 * A new {@link ILocator} to hold transient data.
	 * 
	 * @param name
	 * @param size
	 * 
	 * @return The temporary {@link ILocator}
	 * @throws IOException
	 */
	public static ILocator createTransientLocator(String name, long size) throws IOException {
		if (size >= 0 && size < MAX_BUFFER_SIZE) {
			return new ByteArrayLocator(new byte[(int) size], 0, name);
		}
		File tempFile = FileTools.createTempFile(name);
		return new FileLocator(tempFile);
	}

	public static IDigest digest(ILocator locator, IDigester digester) throws IOException {
		if (locator == null) {
			return null;
		}
		InputStream is = locator.getInputStream();
		try {
			return DigestTools.digest(digester, is);
		} finally {
			StreamTools.close(is);
		}
	}

	public static IDigest digest(ILocator locator, String algorithmName) throws IOException {
		try {
			IDigester digester = DigestTools.createDigester(algorithmName);
			return digest(locator, digester);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Get the local name of the locator in its directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * @param locator The filename whose base name is requested.
	 * 
	 * @return The local name of the file in its directory without the extension.
	 */
	public static String getBaseName(ILocator locator) {
		return getBaseName(locator, null, StringTools.EMPTY);
	}

	/**
	 * Get the local name of the locator in its directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * The extensionPrefix may be used to designate a static prefix that should
	 * be considered part of the extension, not the base name. This is useful
	 * for handling "2nd order" extensions like "document.txt.p7s".
	 * 
	 * The special case ".name" is not treated like an extension and returned as
	 * the basename.
	 * 
	 * @param locator
	 *            The filename whose base name is requested.
	 * @param extensionPrefix
	 *            An optional static prefix that should be considered part of
	 *            the extension, not the basename.
	 * @param defaultName
	 *            returned if filename is null or a empty String
	 * @return The local name of the file in its directory without the
	 *         extension.
	 */
	public static String getBaseName(ILocator locator, String extensionPrefix, String defaultName) {
		if (locator == null) {
			return defaultName;
		}
		// first strip path prefixes
		return PathTools.getBaseName(locator.getName(), extensionPrefix, defaultName);
	}

	/**
	 * The bytes referenced by "locator".
	 * 
	 * @param locator
	 * @return The bytes referenced by "locator".
	 * @throws IOException
	 */
	public static byte[] getBytes(ILocator locator) throws IOException {
		if (locator == null) {
			return null; // NOSONAR
		}
		if (locator instanceof ByteArrayLocator) {
			return ((ByteArrayLocator) locator).getBytes();
		}
		InputStream is = null;
		try {
			is = locator.getInputStream();
			return StreamTools.getBytes(is);
		} finally {
			StreamTools.close(is);
		}
	}

	/**
	 * Get the extension of the locator name. If no extension is present, the
	 * empty string is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param locator
	 *            The locator whose extension is requested.
	 * 
	 * @return The extension of the locator name. If no extension is present,
	 *         the empty string is returned.
	 */
	public static String getExtension(ILocator locator) {
		return getExtension(locator, null, StringTools.EMPTY);
	}

	/**
	 * Get the extension of the locator name. If no extension is present, the
	 * defaultName is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param locator         The locator whose extension is requested.
	 * @param extensionPrefix An extension may use a prefix (by some unique
	 *                        timestamp e.g.) like
	 *                        &lt;name&gt;.&lt;prefix&gt;.&lt;extension&gt;, where
	 *                        prefix is considered being part of the extension. If
	 *                        the filename is .&lt;prefix&gt;. &lt;extension&gt; or
	 *                        &lt;prefix&gt;.&lt;extension&gt;, the prefix is
	 *                        considered being the filename!
	 * @param defaultName     returned if the filename is empty or null or there is
	 *                        no extension
	 * 
	 * @return The extension of the file name. If no extension is present, the empty
	 *         string is returned.
	 */
	public static String getExtension(ILocator locator, String extensionPrefix, String defaultName) {
		if (locator == null) {
			return defaultName;
		}
		return PathTools.getExtension(locator.getName(), extensionPrefix, defaultName);
	}

	public static File getFile(ILocator locator) throws IOException {
		if (locator instanceof FileLocator) {
			return ((FileLocator) locator).getFile();
		}
		File file = FileTools.createTempFile(locator.getName());
		file.deleteOnExit();
		save(locator, file);
		return file;
	}

	public static ILocator getRoot(ILocator locator) {
		ILocator root = locator;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	/**
	 * The {@link String} data referenced by "locator".
	 * 
	 * If possible, the encoding associated with the locator is used, the
	 * platform default encoding otherwise.
	 * 
	 * @param locator
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getString(ILocator locator) throws IOException {
		return getString(locator, (String) null);
	}

	/**
	 * The {@link String} data referenced by "locator".
	 * 
	 * The content is decoded using encoding. If no encoding is given, either
	 * the charset associated with the locator or the platform default is used.
	 * 
	 * @param locator
	 * @param charset
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getString(ILocator locator, Charset charset) throws IOException {
		return getString(locator, charset == null ? null : charset.name());
	}

	/**
	 * The {@link String} data referenced by "locator".
	 * 
	 * The content is decoded using encoding. If no encoding is given, either
	 * the charset associated with the locator or the platform default is used.
	 * 
	 * @param locator
	 * @param encoding
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getString(ILocator locator, String encoding) throws IOException {
		if (locator instanceof StringLocator) {
			return ((StringLocator) locator).getContent();
		}
		if (encoding == null) {
			if (locator instanceof ICharsetSupport) {
				encoding = ((ICharsetSupport) locator).getCharset();
			} else {
				encoding = Charset.defaultCharset().name();
			}
		}
		Reader r = null;
		try {
			r = locator.getReader(encoding);
			return StreamTools.getString(r);
		} finally {
			StreamTools.close(r);
		}
	}

	/**
	 * The {@link String} data referenced by "locator".
	 * 
	 * The encoding is derived from the {@link InputStream} or defaulted to the
	 * charset declared by the {@link ILocator} or the parameter
	 * defaultEncoding.
	 * 
	 * @param locator
	 * @param defaultEncoding
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getStringAutoDetect(ILocator locator, String defaultEncoding) throws IOException {
		if (locator instanceof StringLocator) {
			return ((StringLocator) locator).getContent();
		}
		InputStream is = null;
		Reader r = null;
		try {
			is = new BufferedInputStream(locator.getInputStream(), 1024);
			if (defaultEncoding == null && locator instanceof ICharsetSupport) {
				defaultEncoding = ((ICharsetSupport) locator).getCharset();
			}
			r = ReaderTools.createTaggedReader(is, defaultEncoding, 1024);
			return StreamTools.getString(r);
		} finally {
			StreamTools.close(r);
			StreamTools.close(is);
		}
	}

	/**
	 * Make the locator unique within its parent by inserting a serial number
	 * between basename and extension if necessary.
	 * 
	 * Example: makeUnique("c:/temp/gnu.pdf") -> "c:/temp/gnu.7.pdf"
	 * 
	 * @param locator
	 * @return
	 */
	public static ILocator makeUnique(ILocator locator) {
		if (locator == null) {
			return null;
		}
		if (locator.exists()) {
			int counter = 1;
			ILocator parentLocator = locator.getParent();
			if (parentLocator != null) {
				String basename = LocatorTools.getBaseName(locator);
				String extension = LocatorTools.getExtension(locator);
				while (locator.exists()) {
					locator = parentLocator.getChild(basename + "." + counter++ + "." + extension); //$NON-NLS-2$
				}
			}
		}
		return locator;
	}

	/**
	 * Write bytes to locator.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #write(ILocator,byte[])} instead
	 */
	@Deprecated
	public static void putBytes(ILocator locator, byte[] bytes) throws IOException {
		write(locator, bytes);
	}

	/**
	 * Write value to locator.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #write(ILocator,String)} instead
	 */
	@Deprecated
	public static void putString(ILocator locator, String value) throws IOException {
		write(locator, value);
	}

	/**
	 * Write value to locator using encoding.
	 * 
	 * @throws IOException
	 * @deprecated Use {@link #write(ILocator,String,String)} instead
	 */
	@Deprecated
	public static void putString(ILocator locator, String value, String encoding) throws IOException {
		write(locator, value, encoding);
	}

	/**
	 * Save "locator"s content to a file. The previous file content is deleted.
	 * 
	 * @param locator
	 * @param file
	 * @throws IOException
	 */
	public static void save(ILocator locator, File file) throws IOException {
		if (file.isDirectory()) {
			file = new File(file, locator.getName());
		}
		LocatorTools.copy(locator, new FileLocator(file));
	}

	/**
	 * Write bytes to locator.
	 * 
	 * @throws IOException
	 */
	public static void write(ILocator locator, byte[] bytes) throws IOException {
		if (locator == null) {
			return;
		}
		OutputStream os = null;
		try {
			os = locator.getOutputStream();
			os.write(bytes);
		} finally {
			StreamTools.close(os);
		}
	}

	/**
	 * Write value to locator.
	 * 
	 * @throws IOException
	 */
	public static void write(ILocator locator, String value) throws IOException {
		if (locator == null) {
			return;
		}
		Writer w = null;
		try {
			w = locator.getWriter();
			w.write(value);
		} finally {
			StreamTools.close(w);
		}
	}

	/**
	 * Write value to locator.
	 * 
	 * @throws IOException
	 */
	public static void write(ILocator locator, String value, Charset charset) throws IOException {
		write(locator, value, charset.name());
	}

	/**
	 * Write value to locator using encoding.
	 * 
	 * @throws IOException
	 */
	public static void write(ILocator locator, String value, String encoding) throws IOException {
		if (locator == null) {
			return;
		}
		Writer w = null;
		try {
			w = locator.getWriter(encoding);
			w.write(value);
		} finally {
			StreamTools.close(w);
		}
	}

	private LocatorTools() {
	}
}
