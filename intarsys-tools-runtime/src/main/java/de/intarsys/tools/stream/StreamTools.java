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
package de.intarsys.tools.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A tool class for the handling of streams.
 *
 */
public class StreamTools {

	public static final int MAX_BUFFER = 100000;

	/**
	 * Closes the given object and silently ignores any {@code IOException}.
	 *
	 * @param closeable the object to close
	 *
	 * @deprecated Use try-with-resources instead.
	 */
	@Deprecated(since = "4.25.0")
	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ex) {
			// ignore
		}
	}

	/**
	 * Copy an input stream completely to an output stream.
	 *
	 * @param source
	 *            Input stream
	 * @param destination
	 *            Output stream
	 *
	 * @throws IOException if an error occurs
	 * @deprecated Use {@code InputStream.transferTo(OutputStream)} instead.
	 */
	@Deprecated(since = "4.25.0")
	public static void copy(InputStream source, OutputStream destination) throws IOException {
		try {
			byte[] b = new byte[MAX_BUFFER];
			int i = source.read(b);
			while (i != -1) {
				destination.write(b, 0, i);
				i = source.read(b);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")", e);
		}
	}

	public static void copy(InputStream source, String sourceEncoding, OutputStream destination,
			String destinationEncoding) throws IOException {
		if ((sourceEncoding == null) || (destinationEncoding == null) || sourceEncoding.equals(destinationEncoding)) {
			copy(source, destination);
			return;
		}
		InputStreamReader reader = null;
		OutputStreamWriter writer = null;
		try {
			reader = new InputStreamReader(source, sourceEncoding);
			writer = new OutputStreamWriter(destination, destinationEncoding);
			char[] c = new char[MAX_BUFFER];
			int i = reader.read(c);
			while (i != -1) {
				writer.write(c, 0, i);
				i = reader.read(c);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")");
		}
	}

	/**
	 * @deprecated Use {@code Reader.transferTo(Writer)} instead.
	 */
	@Deprecated(since = "4.25.0")
	public static void copy(Reader reader, Writer writer) throws IOException {
		try {
			char[] c = new char[MAX_BUFFER];
			int i = reader.read(c);
			while (i != -1) {
				writer.write(c, 0, i);
				i = reader.read(c);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")");
		}
	}

	/**
	 * The complete content of the {@link InputStream} as a byte array. The input
	 * stream is closed.
	 *
	 * @param is
	 * @return The complete content of the {@link InputStream} as a byte array.
	 * @throws IOException
	 *
	 * @deprecated Use {@code InputStream.readAllBytes()} instead.
	 */
	@Deprecated(since = "4.25.0")
	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			copy(is, os);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		return os.toByteArray();
	}

	public static String getString(InputStream is, Charset charset) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			copy(is, os);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		return os.toString(charset.name());
	}

	/**
	 * The complete content of the {@link InputStream} as a {@link String},
	 * converted using the specified encoding. If no encoding is specified, the
	 * default encoding is used. The input stream is closed.
	 *
	 * @param is
	 * @param encoding
	 * @return The complete content of the {@link InputStream} as a {@link String}
	 * @throws IOException
	 */
	public static String getString(InputStream is, String encoding) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			copy(is, os);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		return os.toString(encoding);
	}

	/**
	 * The complete content of the {@link Reader} as a {@link String}. The reader is
	 * closed.
	 *
	 * @param r
	 * @return The complete content of the {@link Reader} as a {@link String}.
	 * @throws IOException
	 */
	public static String getString(Reader r) throws IOException {
		try {
			StringWriter w = new StringWriter();
			copy(r, w);
			return w.toString();
		} finally {
			StreamTools.close(r);
		}
	}

	/**
	 * Consider using {@code InputStream.readNBytes(byte[], int, int)} or {@code InputStream.readNBytes(int)} instead.
	 */
	public static int read(InputStream is, byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException("len is negative");
		}
		int total = 0;
		while (total < len) {
			int result = is.read(b, off + total, len - total);
			if (result == -1) {
				break;
			}
			total += result;
		}
		return total == 0 ? -1 : total;
	}

	public static int read(IRandomAccess ra, byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException("len is negative");
		}
		int total = 0;
		while (total < len) {
			int result = ra.read(b, off + total, len - total);
			if (result == -1) {
				break;
			}
			total += result;
		}
		return total == 0 ? -1 : total;
	}

	public static void reset(BufferedInputStream bufferedStream) {
		try {
			bufferedStream.reset();
		} catch (IOException ex) {
			// we dont expect reset() in a buffered stream to fail
		}
	}

	public static int suggestBufferSize(InputStream is) {
		try {
			return suggestBufferSize(is.available());
		} catch (IOException e) {
			return suggestBufferSize(-1);
		}
	}

	public static int suggestBufferSize(long totalSize) {
		int bufferSize;
		if (totalSize < (32 * 1024)) { // < 32kb
			bufferSize = 4024; // 1kb
		} else if (totalSize < (1 * 1024 * 1024)) { // < 1 MB
			bufferSize = 32 * 1024; // 32kb
		} else { // > 1 MB
			bufferSize = 96 * 1024; // 96kb
		}
		return bufferSize;
	}

	/**
	 * The complete content of the {@link InputStream} as a byte array. The input
	 * stream is closed.
	 *
	 * @param is
	 * @return The complete content of the {@link InputStream} as a byte array.
	 * @throws IOException
	 * @deprecated use getBytes()
	 */
	@Deprecated
	public static byte[] toByteArray(InputStream is) throws IOException {
		return getBytes(is);
	}

	/**
	 * @deprecated Use {@link #getString(InputStream,Charset)} instead
	 */
	@Deprecated
	public static String toString(InputStream is, Charset charset) throws IOException {
		return getString(is, charset);
	}

	/**
	 * The complete content of the {@link InputStream} as a {@link String},
	 * converted using the specified encoding. If no encoding is specified, the
	 * default encoding is used. The input stream is closed.
	 *
	 * @param is
	 * @param encoding
	 * @return The complete content of the {@link InputStream} as a {@link String}
	 * @throws IOException
	 * @deprecated Use {@link #getString(InputStream,String)} instead
	 */
	@Deprecated
	public static String toString(InputStream is, String encoding) throws IOException {
		return getString(is, encoding);
	}

	/**
	 * The complete content of the {@link Reader} as a {@link String}. The reader is
	 * closed.
	 *
	 * @param r
	 * @return The complete content of the {@link Reader} as a {@link String}.
	 * @throws IOException
	 * @deprecated Use {@link #getString(Reader)} instead
	 */
	@Deprecated
	public static String toString(Reader r) throws IOException {
		return getString(r);
	}

	private StreamTools() {
		super();
	}
}
