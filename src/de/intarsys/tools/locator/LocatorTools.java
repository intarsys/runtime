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
package de.intarsys.tools.locator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.intarsys.tools.charset.ICharsetAccess;
import de.intarsys.tools.charset.ICharsetSupport;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.digest.DigestTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.digest.IDigester;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.FileTools;
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

	public static void checkHash(ILocator locator, Object hash)
			throws IOException {
		IDigest digest = DigestTools.createDigest(hash);
		if (digest == null) {
			return;
		}
		IDigest docDigest = digest(locator, digest.getAlgorithmName());
		if (!docDigest.equals(digest)) {
			throw new IOException("document digest validation failed ("
					+ locator.getFullName() + ")");
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static void checkHash(ILocator locator, String algorithmName,
			Object hash) throws IOException {
		checkHash(locator, hash);
	}

	/**
	 * Copy locator content.
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void copy(ILocator source, ILocator target)
			throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = source.getInputStream();
			os = target.getOutputStream();
			StreamTools.copyStream(is, false, os, false);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	/**
	 * Create a new {@link ILocatorFactory} that will first try a lookup in the
	 * local context object, with regard to {@link IFileEnvironment} and
	 * {@link IClassLoaderSupport}, then in the fallback factory (if available).
	 * 
	 * The {@link ILocatorFactory} does not statically work on the context
	 * values at the moment of invocation of this method, but creates an
	 * indirection to the context.
	 * 
	 * @param context
	 * @param fallback
	 * @return
	 */
	public static ILocatorFactory createLocalLocatorFactory(
			final Object context, ILocatorFactory fallback) {
		SchemeBasedLocatorFactory sblf = new SchemeBasedLocatorFactory();
		if (context instanceof IFileEnvironment) {
			FileLocatorFactory flf = new FileLocatorFactory(
					new IValueHolder<File>() {

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
							return ((IClassLoaderSupport) context)
									.getClassLoader();
						}

						@Override
						public ClassLoader set(ClassLoader newValue) {
							throw new UnsupportedOperationException();
						}
					});
			sblf.registerLocatorFactory("classpath", clrlf);
		}
		if (fallback == null) {
			return sblf;
		} else {
			LookupLocatorFactory llf = new LookupLocatorFactory();
			llf.addLocatorFactory(sblf);
			llf.addLocatorFactory(fallback);
			return llf;
		}
	}

	public static ILocator createLocator(Object value, ILocatorFactory factory,
			ILocator defaultValue) {
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
			Object tempLocator = tempArgs.get("locator");
			if (tempLocator == null) {
				tempLocator = tempArgs.get("path");
			}
			if (tempLocator != null) {
				resultLocator = createLocator(tempLocator, factory, null);
				if (resultLocator instanceof ICharsetAccess && charset != null) {
					((ICharsetAccess) resultLocator).setCharset(charset);
				}
			}
			if (resultLocator == null) {
				String name = ArgTools.getString(tempArgs, "name",
						"locator.bytes");
				name = FileTools.getFileName(name);
				Object tempContent = tempArgs.get("content");
				if (tempContent instanceof ILocator) {
					resultLocator = (ILocator) tempContent;
				} else if (tempContent instanceof byte[]) {
					resultLocator = new ByteArrayLocator((byte[]) tempContent,
							name);
				} else if (tempContent instanceof String) {
					byte[] bytes = Base64.decode((String) tempContent);
					resultLocator = new ByteArrayLocator(bytes, name);
				} else {
					try {
						byte[] bytes = ConverterRegistry.get().convert(
								tempContent, byte[].class);
						resultLocator = new ByteArrayLocator(bytes, name);
					} catch (ConversionException e1) {
						try {
							String string = ConverterRegistry.get().convert(
									tempContent, String.class);
							byte[] bytes = Base64.decode(string);
							resultLocator = new ByteArrayLocator(bytes, name);
						} catch (ConversionException e2) {
							// ignore
						}
					}
					if (resultLocator == null) {
						resultLocator = new ByteArrayLocator(null, name);
					}
				}
				if (resultLocator instanceof ICharsetAccess && charset != null) {
					((ICharsetAccess) resultLocator).setCharset(charset);
				}
			}
			try {
				IDigest digest = ArgTools.getDigest(tempArgs, "hash");
				LocatorTools.checkHash(resultLocator, digest);
			} catch (IOException e) {
				throw new IllegalArgumentException("hash", e);
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
			try {
				return factory.createLocator(((File) value).getAbsolutePath());
			} catch (IOException e) {
				return defaultValue;
			}
		}
		if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return defaultValue;
			}
			try {
				return factory.createLocator((String) value);
			} catch (IOException e) {
				return defaultValue;
			}
		}
		try {
			return ConverterRegistry.get().convert(value, ILocator.class);
		} catch (ConversionException e) {
			return defaultValue;
		}
	}

	public static ILocator[] createLocators(String paths,
			ILocatorFactory factory) throws IOException {
		if (factory == null) {
			factory = LocatorFactory.get();
		}
		List<ILocator> locators = new ArrayList<ILocator>();
		for (StringTokenizer t = new StringTokenizer(paths, PATH_SEPARATOR); t
				.hasMoreTokens();) {
			String path = t.nextToken();
			locators.add(factory.createLocator(path));
		}
		ILocator[] result = new ILocator[locators.size()];
		return locators.toArray(result);
	}

	/**
	 * A temporary {@link File} with a copy of the data from "locator".
	 * 
	 * @param locator
	 * @return The temporary file.
	 * @throws IOException
	 */
	public static File createTempFile(ILocator locator) throws IOException {
		File tempFile = FileTools.createTempFile(locator.getTypedName());
		save(locator, tempFile);
		return tempFile;
	}

	/**
	 * A new temporary {@link ILocator} with a copy of the data from "locator".
	 * The {@link ILocator} and the data backing store created is guaranteed to
	 * live as long as it is used (not garbage collected). Best effort is made
	 * to delete all artifacts after it is no longer used.
	 * 
	 * @param locator
	 * @return The temporary {@link ILocator}
	 * @throws IOException
	 */
	public static ILocator createTempLocator(ILocator locator)
			throws IOException {
		return createTempLocator(locator, false);
	}

	/**
	 * A new temporary {@link ILocator} with a copy of the data from "locator".
	 * The {@link ILocator} and the data backing store created is guaranteed to
	 * live as long as it is used (not garbage collected). Best effort is made
	 * to delete all artifacts after it is no longer used.
	 * 
	 * @param locator
	 * @param keepName
	 *            Flag if the resulting locator should have the same name as the
	 *            original input.
	 * @return The temporary {@link ILocator}
	 * @throws IOException
	 */
	public static ILocator createTempLocator(ILocator locator, boolean keepName)
			throws IOException {
		if (locator.getLength() != -1 && locator.getLength() < 5000) {
			byte[] bytes = getBytes(locator);
			return new ByteArrayLocator(bytes, locator.getTypedName());
		}
		File tempFile = FileTools.createTempFile(locator.getTypedName());
		save(locator, tempFile);
		if (keepName) {
			return new RenamedLocator(new FileLocator(tempFile),
					locator.getFullName());
		} else {
			return new FileLocator(tempFile);
		}
	}

	public static IDigest digest(ILocator locator, String algorithmName)
			throws IOException {
		InputStream is = locator.getInputStream();
		try {
			IDigester digester = DigestTools.createDigester(algorithmName);
			return DigestTools.digest(digester, is);
		} catch (NoSuchAlgorithmException e) {
			throw ExceptionTools.createIOException(e.getMessage(), e);
		} finally {
			StreamTools.close(is);
		}
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
			return null;
		}
		if (locator instanceof ByteArrayLocator) {
			return ((ByteArrayLocator) locator).getBytes();
		}
		InputStream is = null;
		try {
			is = locator.getInputStream();
			return StreamTools.toByteArray(is);
		} finally {
			StreamTools.close(is);
		}
	}

	public static File getFile(ILocator locator) throws IOException {
		if (locator instanceof FileLocator) {
			return ((FileLocator) locator).getFile();
		}
		// add conversion (temp file) code
		throw new IOException("can't create file for '" + locator.getFullName() //$NON-NLS-1$
				+ "'"); //$NON-NLS-1$
	}

	/**
	 * The {@link String} data referenced by "locator". If necessary, the
	 * encoding associated with the locator is used by using its "getReader"
	 * method.
	 * 
	 * @param locator
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getString(ILocator locator) throws IOException {
		if (locator instanceof StringLocator) {
			return ((StringLocator) locator).getContent();
		}
		Reader r = null;
		try {
			r = locator.getReader();
			return StreamTools.toString(r);
		} finally {
			StreamTools.close(r);
		}
	}

	/**
	 * The {@link String} data referenced by "locator". The encoding is derived
	 * from the {@link InputStream} or defaulted to the charset declared by the
	 * {@link ILocator} or the parameter defaultCharsetName.
	 * 
	 * @param locator
	 * @param defaultCharsetName
	 * @return The {@link String} data referenced by "locator".
	 * @throws IOException
	 */
	public static String getString(ILocator locator, String defaultCharsetName,
			int size) throws IOException {
		if (locator instanceof StringLocator) {
			return ((StringLocator) locator).getContent();
		}
		InputStream is = null;
		Reader r = null;
		try {
			is = new BufferedInputStream(locator.getInputStream(), size);
			if (defaultCharsetName == null
					&& locator instanceof ICharsetSupport) {
				defaultCharsetName = ((ICharsetSupport) locator).getCharset();
			}
			r = ReaderTools.createTaggedReader(is, defaultCharsetName, size);
			return StreamTools.toString(r);
		} finally {
			StreamTools.close(r);
			StreamTools.close(is);
		}
	}

	/**
	 * Write bytes to locator.
	 * 
	 * @throws IOException
	 */
	public static void putBytes(ILocator locator, byte[] bytes)
			throws IOException {
		if (locator == null) {
			return;
		}
		OutputStream os = null;
		try {
			os = locator.getOutputStream();
			StreamTools.putBytes(os, bytes);
		} finally {
			StreamTools.close(os);
		}
	}

	/**
	 * Write string to locator.
	 * 
	 * @throws IOException
	 */
	public static void putString(ILocator locator, String string)
			throws IOException {
		if (locator == null) {
			return;
		}
		Writer w = null;
		try {
			w = locator.getWriter();
			w.write(string);
		} finally {
			StreamTools.close(w);
		}
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
			file = new File(file, locator.getTypedName());
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = locator.getInputStream();
			os = new java.io.FileOutputStream(file);
			StreamTools.copyStream(is, false, os, false);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
	}

	private LocatorTools() {
	}
}
