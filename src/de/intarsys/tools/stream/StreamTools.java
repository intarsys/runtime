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
package de.intarsys.tools.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A tool class for the handling of streams.
 * 
 */
public class StreamTools {
	//
	public static final int MAX_BUFFER = 100000;

	public static void close(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(IRandomAccess ra) {
		try {
			if (ra != null) {
				ra.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(RandomAccessFile ra) {
		try {
			if (ra != null) {
				ra.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}

	public static void copyEncoded(Reader reader, boolean closeInput,
			Writer writer, boolean closeOutput) throws IOException {
		try {
			char[] c = new char[MAX_BUFFER];
			for (int i = reader.read(c); i != -1;) {
				writer.write(c, 0, i);
				i = reader.read(c);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")");
		} finally {
			if (closeInput) {
				StreamTools.close(reader);
			}
			if (closeOutput) {
				StreamTools.close(writer);
			}
		}
	}

	public static void copyEncoded(Reader reader, Writer writer)
			throws IOException {
		copyEncoded(reader, true, writer, true);
	}

	public static void copyEncodedStream(InputStream source,
			String sourceEncoding, OutputStream destination,
			String destinationEncoding) throws IOException {
		if ((sourceEncoding == null) | (destinationEncoding == null)
				| sourceEncoding.equals(destinationEncoding)) {
			copyStream(source, false, destination, false);
		}
		InputStreamReader reader = null;
		OutputStreamWriter writer = null;
		try {
			reader = new InputStreamReader(source, sourceEncoding);
			writer = new OutputStreamWriter(destination, destinationEncoding);
			char[] c = new char[MAX_BUFFER];
			for (int i = reader.read(c); i != -1;) {
				writer.write(c, 0, i);
				i = reader.read(c);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("copying failed (" + e.getMessage() + ")");
		} finally {
			/*
			 * do not close try { reader.close(); } catch (Exception ignore) { }
			 * try { writer.close(); } catch (Exception ignore) { }
			 */
		}
	}

	/**
	 * Kopiert einen Eingabedatenstrom auf einen Ausgabedatenstrom. Anschließend
	 * (finally) werden die Datenströme geschlossen(!), sofern dies in den
	 * Parametern angegeben wurde. Fehler beim Schließen der Datenströme werden
	 * ignoriert.
	 * 
	 * @param source
	 *            Eingabedatenstrom
	 * @param closeInput
	 *            Angabe ob der InputStream nach dem Kopieren geschlossen werden
	 *            soll.
	 * @param destination
	 *            Ausgabedatenstrom
	 * @param closeOutput
	 *            Angabe ob der OutputStream nach dem Kopieren geschlossen
	 *            werden soll.
	 * 
	 * @throws IOException
	 *             Fehler allgemein oder beim Kopieren.
	 */
	public static void copyStream(InputStream source, boolean closeInput,
			OutputStream destination, boolean closeOutput) throws IOException {
		try {
			byte[] b = new byte[MAX_BUFFER];
			for (int i = source.read(b); i != -1;) {
				destination.write(b, 0, i);
				i = source.read(b);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw ExceptionTools.createIOException(
					"copying failed (" + e.getMessage() + ")", e);
		} finally {
			if (closeInput) {
				StreamTools.close(source);
			}
			if (closeOutput) {
				StreamTools.close(destination);
			}
		}
	}

	/**
	 * Kopiert einen Stream. Ruft copyStream(in, true, out, true) auf.
	 * 
	 * @see StreamTools#copyStream(InputStream, boolean, OutputStream, boolean)
	 * 
	 * @param source
	 *            Eingabedatenstrom, der kopiert werden soll.
	 * @param destination
	 *            Ausgabestrom, auf den kopiert werden soll.
	 * 
	 * @throws IOException
	 * 
	 */
	public static void copyStream(InputStream source, OutputStream destination)
			throws IOException {
		copyStream(source, true, destination, true);
	}

	/**
	 * The complete content of the {@link InputStream} as a byte array. The
	 * input stream is closed.
	 * 
	 * @param is
	 * @return The complete content of the {@link InputStream} as a byte array.
	 * @throws IOException
	 */
	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		copyStream(is, os);
		return os.toByteArray();
	}

	/**
	 * Write all bytes to {@link OutputStream}. The stream is closed.
	 * 
	 * @param os
	 * @param bytes
	 * @throws IOException
	 */
	public static void putBytes(OutputStream os, byte[] bytes)
			throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		copyStream(is, os);
	}

	/**
	 * The complete content of the {@link InputStream} as a byte array. The
	 * input stream is closed.
	 * 
	 * @param is
	 * @return The complete content of the {@link InputStream} as a byte array.
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream is) throws IOException {
		return getBytes(is);
	}

	/**
	 * The complete content of the {@link InputStream} as a {@link String},
	 * converted using the specified encoding. If no encoding is specified, the
	 * default encoding is used. The input stream is closed.
	 * 
	 * @param is
	 * @param encoding
	 * @return The complete content of the {@link InputStream} as a
	 *         {@link String}
	 * @throws IOException
	 */
	public static String toString(InputStream is, String encoding)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		copyStream(is, os);
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		return os.toString(encoding);
	}

	/**
	 * The complete content of the {@link Reader} as a {@link String}. The
	 * reader is closed.
	 * 
	 * @param r
	 * @return The complete content of the {@link Reader} as a {@link String}.
	 * @throws IOException
	 */
	public static String toString(Reader r) throws IOException {
		StringWriter w = new StringWriter();
		copyEncoded(r, w);
		return w.toString();
	}

	private StreamTools() {
		super();
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
}
