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
package de.intarsys.tools.reader;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.stream.StreamTools;

/**
 * A reader that can unescape character data from a wrapped reader.
 * 
 */
public class UnEscapeReader extends FilterReader implements IUnEscaper {
	public static final char DefaultEscape = '\\';

	private static final Map DefaultEscapeMap = new HashMap();

	static {
		// escape the escape
		DefaultEscapeMap.put(new Character('\\'), new Character('\\'));
		// escape to insert whitespace
		DefaultEscapeMap.put(new Character('n'), new Character('\n'));
		DefaultEscapeMap.put(new Character('r'), new Character('\r'));
		DefaultEscapeMap.put(new Character('t'), new Character('\t'));
		// escape to remove whitespace
		DefaultEscapeMap.put(new Character('\n'), null);
		DefaultEscapeMap.put(new Character('\r'), null);
		DefaultEscapeMap.put(new Character('\t'), null);
		DefaultEscapeMap.put(new Character(' '), null);
	}

	public static String unescape(String in) throws IOException {
		UnEscapeReader reader = new UnEscapeReader(new StringReader(in));
		return StreamTools.toString(reader);
	}

	private char escape = DefaultEscape;

	private Map escapeMap = DefaultEscapeMap;

	private boolean mapped = false;

	private char unicodePrefix = 'u';

	private boolean ignoreUndefinedEscape = false;

	public UnEscapeReader(Reader in) {
		super(in);
	}

	public UnEscapeReader(Reader in, boolean ignoreUndefinedEscape) {
		super(in);
		setIgnoreUndefinedEscape(ignoreUndefinedEscape);
	}

	public UnEscapeReader(Reader in, char escape, Map map) {
		super(in);
		this.escape = escape;
		this.escapeMap = map;
	}

	public UnEscapeReader(Reader in, char escape, Map map,
			boolean ignoreUndefinedEscape) {
		super(in);
		this.escape = escape;
		this.escapeMap = map;
		setIgnoreUndefinedEscape(ignoreUndefinedEscape);
	}

	public UnEscapeReader(Reader in, Map map) {
		super(in);
		this.escapeMap = map;
	}

	public UnEscapeReader(Reader in, Map map, boolean ignoreUndefinedEscape) {
		super(in);
		this.escapeMap = map;
		setIgnoreUndefinedEscape(ignoreUndefinedEscape);
	}

	public void addEscapedCharacter(char key, char value) {
		// this creates a new copy!
		Map tempMap = getEscapeMap();
		tempMap.put(new Character(key), new Character(value));
		setEscapeMap(tempMap);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public char getEscape() {
		return escape;
	}

	public java.util.Map getEscapeMap() {
		return new HashMap(escapeMap);
	}

	public char getUnicodePrefix() {
		return unicodePrefix;
	}

	public final boolean isIgnoreUndefinedEscape() {
		return ignoreUndefinedEscape;
	}

	public boolean isMapped() {
		return mapped;
	}

	@Override
	public int read() throws IOException {
		mapped = false;
		int i = in.read();
		if (i == escape) {
			mapped = true;
			i = readEscaped();
		}
		return i;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int stop = off + len;
		if ((off < 0) || (off >= cbuf.length) || (len < 0)
				|| (stop > cbuf.length)) {
			throw new IndexOutOfBoundsException();
		} else {
			if (len == 0) {
				return 0;
			}
		}
		int pos = off;
		while (pos < stop) {
			int i = read();
			if (i == -1) {
				break;
			}
			cbuf[pos] = (char) i;
			pos++;
		}
		if ((pos == off)) {
			return -1;
		}
		return (pos - off);
	}

	protected int readEscaped() throws IOException {
		int i = in.read();
		if (i == -1) {
			return -1;
		}
		if ((char) i == '\r') {
			int next = read();
			if (((char) next == '\n') && !isMapped()) {
				// escaped CR/LF (Windows), ignore line feed
				return read();
			} else {
				// escaped CR, ignore carriage return
				return next;
			}
		}
		if ((char) i == '\n') {
			// escaped LF (Unix), ignore line feed
			return read();
		}
		if ((char) i == getUnicodePrefix()) {
			return readHex();
		}
		Character key = new Character((char) i);
		Character value = (Character) escapeMap.get(key);
		if (value == null) {
			if (escapeMap.containsKey(key)) {
				// this key is mapped to null, skip from stream and read
				// again
				return read();
			} else if (isIgnoreUndefinedEscape()) {
				return i;
			} else {
				throw new IOException("unrecognized escape sequence: \"\\"
						+ (char) i + "\"");
			}
		} else {
			return value.charValue();
		}
	}

	protected int readHex() throws IOException {
		// todo remove cb handling
		StringBuilder buf = new StringBuilder();
		int ch;
		for (ch = in.read(); ((((char) ch >= '0') && ((char) ch <= '9'))
				|| (((char) ch >= 'A') && ((char) ch <= 'F')) || (((char) ch >= 'a') && ((char) ch <= 'f')))
				&& (buf.length() < 4); ch = in.read()) {
			buf.append((char) ch);
		}
		if (ch == -1) {
			return -1;
		}
		if (buf.length() < 4) {
			throw new IOException("illegal escape sequence: \"\\u"
					+ buf.toString() + "\"");
		}
		String hexString = new String(buf);
		return Integer.parseInt(hexString, 16);
	}

	public void removeEscapedCharacter(char key) {
		Map tempMap = getEscapeMap();
		tempMap.remove(new Character(key));
		setEscapeMap(tempMap);
	}

	public void removeEscapedCharacters() {
		setEscapeMap(new HashMap());
	}

	public void setEscape(char newEscape) {
		if (newEscape == escape) {
			return;
		}
		Map tempMap = getEscapeMap();
		tempMap.remove(new Character(getEscape()));
		tempMap.put(new Character(newEscape), new Character(newEscape));
		setEscapeMap(tempMap);
		escape = newEscape;
	}

	protected void setEscapeMap(java.util.Map newEscapeMap) {
		escapeMap = newEscapeMap;
	}

	public final void setIgnoreUndefinedEscape(boolean ignoreUndefinedEscape) {
		this.ignoreUndefinedEscape = ignoreUndefinedEscape;
	}

	public void setUnicodePrefix(char newUniChar) {
		unicodePrefix = newUniChar;
	}

	@Override
	public long skip(long n) throws IOException {
		if (n < 0) {
			throw new IOException("parameter is negative");
		}
		long actual = 0;
		int ch = 0;
		for (; (actual < n) && (ch != -1); ch = read(), ++actual) {
			// ignore characters
		}
		return actual;
	}
}
