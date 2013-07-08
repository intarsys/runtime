package de.intarsys.tools.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.collection.Entry;
import de.intarsys.tools.string.StringTools;

/**
 * Tool class for common {@link Reader} related tasks.
 * 
 */
public class ReaderTools {

	/**
	 * Try to detect the unicode transformation format (UTF encoding) from the
	 * BOM. If no BOM is detected, null is returned and the input buffer is
	 * reset.
	 * <p>
	 * The {@link InputStream} is must support the mark operation!
	 * 
	 * For BOM marker bytes, see http://unicode.org/faq/utf_bom.html
	 * 
	 * <pre>
	 * Bytes 				Encoding Form 
	 * 00 00 FE FF 			UTF-32, big-endian 
	 * FF FE 00 00 			UTF-32, little-endian 
	 * FE FF 				UTF-16, big-endian 
	 * FF FE 				UTF-16, little-endian 
	 * EF BB BF 			UTF-8
	 * </pre>
	 * 
	 * @param is
	 * @return An {@link InputStreamReader} with the correct encoding
	 * @throws IOException
	 */
	static public InputStreamReader createReaderScanBom(InputStream is)
			throws IOException {
		String encoding = null;
		int i;
		is.mark(4);
		i = is.read();
		if (i == 0x00) {
			i = is.read();
			if (i == 0x00) {
				i = is.read();
				if (i == 0xfe) {
					i = is.read();
					if (i == 0xff) {
						// UTF-32, big endian
						encoding = "UTF-32BE";
					}
				}
			}
		} else if (i == 0xff) {
			i = is.read();
			if (i == 0xfe) {
				is.mark(4);
				i = is.read();
				if (i == 0x00) {
					i = is.read();
					if (i == 0x00) {
						// UTF-32, little endian
						encoding = "UTF-32LE";
					}
				}
				if (encoding == null) {
					is.reset();
					// UTF-16, little endian
					encoding = "UTF-16LE";
				}
			}
		} else if (i == 0xfe) {
			i = is.read();
			if (i == 0xff) {
				// UTF-16, big endian
				encoding = "UTF-16BE";
			}
		} else if (i == 0xef) {
			i = is.read();
			if (i == 0xbb) {
				i = is.read();
				if (i == 0xbf) {
					// UTF-8
					encoding = "UTF-8";
				}
			}
		}
		if (encoding == null) {
			is.reset();
			return null;
		}
		return new InputStreamReader(is, encoding);
	}

	/**
	 * Try to detect the input stream encoding from the meta tags "$$$" embedded
	 * in the stream. If no encoding is detected, the input is reset and null is
	 * returned.
	 * <p>
	 * The {@link InputStream} is must support the mark operation!
	 * 
	 * @param is
	 * @return An {@link InputStreamReader} with the correct encoding
	 * @throws IOException
	 */
	static public InputStreamReader createReaderScanMeta(final InputStream is)
			throws IOException {
		// use single byte encoding and read encoding from meta data
		Reader tempReader = new Reader() {

			@Override
			public void close() throws IOException {
			}

			@Override
			public void mark(int readlimit) throws IOException {
				is.mark(readlimit);
			}

			@Override
			public boolean markSupported() {
				return is.markSupported();
			}

			@Override
			public int read() throws IOException {
				return is.read();
			};

			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				// not used
				return -1;
			};

			@Override
			public void reset() throws IOException {
				is.reset();
			};
		};
		String encoding = ReaderTools.readMetaEncoding(tempReader);
		if (encoding != null) {
			return new InputStreamReader(is, encoding);
		}
		return null;
	}

	/**
	 * Create a {@link TaggedReader} and automatically detect the encoding from
	 * different heuristics. First, the BOM markers are checked, then embedded
	 * meta information is scanned.
	 * <p>
	 * If no encoding can be guessed, either the defaultCharsetName or the
	 * platform encoding is used.
	 * <p>
	 * Meta information tags (lines starting with '$$$') are scanned.
	 * 
	 * @param is
	 * @param defaultCharsetName
	 * @return A {@link TaggedReader} with the correct encoding
	 * @throws IOException
	 */
	public static TaggedReader createTaggedReader(InputStream is,
			String defaultCharsetName, int size) throws IOException {
		// try to detect utf byte order marker
		InputStreamReader reader = createReaderScanBom(is);
		if (reader == null) {
			reader = createReaderScanMeta(is);
			if (reader == null) {
				if (defaultCharsetName == null) {
					reader = new InputStreamReader(is);
				} else {
					reader = new InputStreamReader(is, defaultCharsetName);
				}
			}
		}
		// now we must scan meta tags...
		TaggedReader tagged = new TaggedReader(reader, size);
		return tagged;
	}

	/**
	 * Read a Map.Entry object from r. The end of the entry is marked by
	 * delimiter.
	 * <p>
	 * The syntax for an entry is
	 * 
	 * <pre>
	 * ws* key ws* '=' value [delimiter | EOF]
	 * value = string | quoted_string
	 * quoted_string = '"' [ char | escape ]* '"'
	 * escape = '\' escape_char
	 * escape_char = '"' | '\' | 'n' | 'r' | 't' | '\n' | '\r' | '\t'
	 * </pre>
	 * 
	 * @param reader
	 * @param delimiter
	 * @return A single map entry read from the reader.
	 * @throws IOException
	 */
	public static Map.Entry<String, String> readEntry(Reader reader,
			char delimiter) throws IOException {
		String key = ""; //$NON-NLS-1$
		String value = ""; //$NON-NLS-1$
		StringBuffer sb = new StringBuffer();
		boolean readKey = true;
		boolean quoted = false;
		boolean ignore = false;
		while (true) {
			int i = reader.read();
			if (i == '=' && readKey) {
				key = sb.toString().trim();
				sb.setLength(0);
				readKey = false;
			} else if (i == '\\' && quoted) {
				i = reader.read();
				if (i != -1) {
					String mapped = StringTools.ESCAPES.get(Character
							.valueOf((char) i));
					if (mapped != null) {
						sb.append(mapped);
					}
				}
			} else if (i == '"' && !readKey) {
				if (quoted) {
					quoted = false;
					ignore = true;
				} else {
					if (sb.length() == 0) {
						quoted = true;
					} else {
						if (!ignore) {
							sb.append((char) i);
						}
					}
				}
			} else if (i == -1) {
				if (readKey) {
					key = sb.toString().trim();
					value = ""; //$NON-NLS-1$
				} else {
					value = sb.toString();
				}
				if (key.length() > 0) {
					return new Entry(key, value);
				} else if (value.length() > 0) {
					return new Entry(key, value);
				} else {
					return null;
				}
			} else if (i == '\r' && !quoted && delimiter == '\n') {
				continue;
			} else if (i == delimiter && !quoted) {
				if (readKey) {
					key = sb.toString().trim();
					value = ""; //$NON-NLS-1$
				} else {
					value = sb.toString();
				}
				if (key.length() > 0) {
					return new Entry(key, value);
				} else if (value.length() > 0) {
					return new Entry(key, value);
				} else {
					return new Entry(null, null);
				}
			} else {
				if (!ignore) {
					sb.append((char) i);
				}
			}
		}
	}

	/**
	 * Try to detect meta data embedded in the input.
	 * <p>
	 * Meta data lines start with a '$$$' immediately at the line beginning and
	 * end at the line end. Meta data lines are scanned until a line without
	 * meta data is found. Meta data is encoded as entries (as provided in
	 * readEntry method).
	 * <p>
	 * The maximum length for a meta data line is 1024.
	 * <p>
	 * After execution reader is either positioned after the last meta tag. The
	 * reader instance must support the "mark/reset" sequence.
	 * 
	 * @param reader
	 * @return All meta data in the reader as a {@link Map}
	 * @throws IOException
	 */
	public static Map<String, String> readMetaData(Reader reader)
			throws IOException {
		int i;
		Map<String, String> meta = new HashMap<String, String>();
		while (true) {
			reader.mark(1024);
			i = reader.read();
			if (i == '$') {
				i = reader.read();
				if (i == '$') {
					i = reader.read();
					if (i == '$') {
						// meta data found...
						Map.Entry<String, String> entry = ReaderTools
								.readEntry(reader, '\n');
						if (entry.getKey() != null) {
							meta.put(entry.getKey(), entry.getValue());
						}
						continue;
					}
				}
			}
			reader.reset();
			break;
		}
		return meta;
	}

	/**
	 * Try to detect encoding specific meta data embedded in the input. The
	 * first meta data line is read and the value is returned if the meta
	 * information key is "encoding".
	 * <p>
	 * After execution reader is either positioned at the start or after the
	 * "encoding" meta tag. The reader instance must support the "mark/reset"
	 * sequence.
	 * 
	 * <p>
	 * For more information on meta data see readMetaData.
	 * 
	 * @param reader
	 * @return The meta data for reader defining the encoding
	 * @throws IOException
	 */
	public static String readMetaEncoding(Reader reader) throws IOException {
		int i;
		reader.mark(1024);
		i = reader.read();
		if (i == '$') {
			i = reader.read();
			if (i == '$') {
				i = reader.read();
				if (i == '$') {
					// meta data found...
					Map.Entry<String, String> entry = ReaderTools.readEntry(
							reader, '\n');
					if ("encoding".equals(entry.getKey())) {
						reader.reset();
						return entry.getValue();
					}
				}
			}
		}
		reader.reset();
		return null;
	}

	/**
	 * Read a string token from r. The end of the token is marked by delimiter.
	 * The syntax for a token is
	 * 
	 * <pre>
	 * value [delimiter | EOF]
	 * value = string | quoted_string
	 * quoted_string = '"' [ char | escape ]* '"'
	 * escape = '\' escape_char
	 * escape_char = '"' | '\' | 'n' | 'r' | 't' | '\n' | '\r' | '\t'
	 * </pre>
	 * 
	 * @param reader
	 * @param delimiter
	 * @return A single token.
	 * @throws IOException
	 */
	public static String readToken(Reader reader, char delimiter)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		boolean quoted = false;
		boolean ignore = false;
		while (true) {
			int i = reader.read();
			if (i == '\\' && quoted) {
				i = reader.read();
				if (i != -1) {
					String mapped = StringTools.ESCAPES.get(Character
							.valueOf((char) i));
					if (mapped != null) {
						sb.append(mapped);
					}
				}
			} else if (i == '\r' && !quoted && delimiter == '\n') {
				continue;
			} else if (i == '"') {
				if (quoted) {
					quoted = false;
					ignore = true;
				} else {
					if (sb.length() == 0) {
						quoted = true;
					} else {
						if (!ignore) {
							sb.append((char) i);
						}
					}
				}
			} else if (i == -1) {
				if (sb.length() == 0) {
					return null;
				}
				return sb.toString();
			} else if (i == delimiter && !quoted) {
				return sb.toString();
			} else {
				if (!ignore) {
					sb.append((char) i);
				}
			}
		}
	}

}
