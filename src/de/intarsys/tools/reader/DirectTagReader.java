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
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Reader} that is aware of embedded tags. An example is processing a
 * JSP page, where java is embededd using "<%...%>". After recognizing such a
 * tag, the associated {@link IDirectTagHandler} is informed to handle the tag.
 * After handling, the result of the {@link IDirectTagHandler} is streamd as a
 * replacement for the tag itself. After streaming the processed tag content,
 * reading the input continues as normal.
 * 
 */
public class DirectTagReader extends FilterReader {
	static private class NullLocationProvider implements ILocationProvider {
		public int getColumn() {
			return 0;
		}

		public int getLine() {
			return 0;
		}

		public int getPosition() {
			return 0;
		}
	}

	private static final char[] defaultEndTag = "}".toCharArray();

	public static final Map DefaultEscapeMap = new HashMap();

	private static final char[] defaultStartTag = "${".toCharArray();

	public static final char ESCAPE_CHARACTER = '\\';

	static private ILocationProvider nullLocationProvider = new NullLocationProvider();

	private static final Object UNUSED = new Object();

	static {
		DefaultEscapeMap.put(new Character('\\'), new Character('\\'));
		DefaultEscapeMap.put(new Character('n'), new Character('\n'));
		DefaultEscapeMap.put(new Character('r'), new Character('\r'));
		DefaultEscapeMap.put(new Character('t'), new Character('\t'));
		DefaultEscapeMap.put(new Character('$'), new Character('$'));
		DefaultEscapeMap.put(new Character('}'), new Character('}'));
		DefaultEscapeMap.put(new Character('\n'), null);
		DefaultEscapeMap.put(new Character('\r'), null);
		DefaultEscapeMap.put(new Character('\t'), null);
		DefaultEscapeMap.put(new Character(' '), null);
	}

	static public String escape(String value) {
		return value.replaceAll("\\$\\{", "\\$\\{\\$\\{\\}");
	}

	private int bufferLength = 0;

	private int checkLength = 0;

	private Object context;

	private char[] endTag = defaultEndTag;

	private boolean forceToString = true;

	private IDirectTagHandler handler;

	private char[] readBuffer = new char[100];

	private Object resolvedObject = UNUSED;

	private char[] startTag = defaultStartTag;

	private StringBuilder tagBuffer = new StringBuilder();

	private UnEscapeReader unescapeReader;

	public DirectTagReader(Reader pReader, IDirectTagHandler handler,
			Object context) {
		this(pReader, handler, context, true);
	}

	public DirectTagReader(Reader pReader, IDirectTagHandler handler,
			Object context, boolean escape) {
		super(pReader);
		if (escape) {
			this.in = new UnEscapeReader(pReader, ESCAPE_CHARACTER,
					DefaultEscapeMap);
			unescapeReader = (UnEscapeReader) this.in;
		}
		this.handler = handler;
		this.context = context;
		if (pReader instanceof ILocationProvider) {
			handler.setLocationProvider((ILocationProvider) pReader);
		} else {
			handler.setLocationProvider(nullLocationProvider);
		}
	}

	/**
	 * Read from either the read buffer or the underlying stream.
	 * 
	 * @return The next character available frm the read buffer or underlying
	 *         stream.
	 * @throws IOException
	 */
	protected int basicRead() throws IOException {
		if (bufferLength > 0) {
			// consume "prepared" content - this is read ahead or
			// the result from a tag handling callback
			return readBuffer[--bufferLength];
		}
		checkLength = Integer.MAX_VALUE;
		return super.read();
	}

	protected IDirectTagHandler getHandler() {
		return handler;
	}

	public Object getResolvedObject() {
		if (resolvedObject == UNUSED) {
			return null;
		}
		return resolvedObject;
	}

	public boolean hasResolvedObject() {
		return this.resolvedObject != UNUSED;
	}

	public boolean isForceToString() {
		return forceToString;
	}

	protected boolean isSpecialTag(String tag) {
		if (tag.length() == startTag.length) {
			for (int index = 0; index < startTag.length; index++) {
				if (tag.charAt(index) != startTag[index]) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Read a character until we encounter a tag.
	 * 
	 * @see java.io.Reader#read()
	 */
	@Override
	public int read() throws IOException {
		int i = basicRead();
		if ((checkLength > bufferLength) && (i == startTag[0])
				&& (unescapeReader == null || !unescapeReader.isMapped())) {
			return scanTag();
		}
		// reading unchanged from source means: force string conversion on tags
		forceToString = true;
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len)
			throws IOException {
		for (int i = 0; i < len; i++) {
			final int ch = read();
			if (ch == -1) {
				if (i == 0) {
					return -1;
				} else {
					return i;
				}
			}
			cbuf[off + i] = (char) ch;
		}
		return len;
	}

	protected int scanEndTag() throws IOException {
		int tagIndex = 0;
		int i = endTag[0];
		while (i == endTag[tagIndex]) {
			tagIndex++;
			if (tagIndex == endTag.length) {
				return -1;
			}
			i = basicRead();
		}
		if (i != -1) {
			unread(i);
		}
		unread(endTag, 0, tagIndex);
		// consume buffer content
		return basicRead();
	}

	/**
	 * Scan the stream for tagged content. We check the presence of the start
	 * tag. If found the stream is read until the presence of the end tag. If
	 * the start tag is not completely found, we return the literal stream
	 * content.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected int scanTag() throws IOException {
		int tagIndex = 0;
		int i = startTag[0];
		while (i == startTag[tagIndex]) {
			tagIndex++;
			if (tagIndex == startTag.length) {
				handler.startTag();
				return scanTagContent();
			}
			i = basicRead();
		}
		if (i != -1) {
			unread(i);
		}
		unread(startTag, 0, tagIndex);
		// consume buffer content
		return basicRead();
	}

	/**
	 * Scan the content between start and end tag and process the result.
	 * 
	 * @throws IOException
	 */
	protected int scanTagContent() throws IOException {
		tagBuffer.setLength(0);
		int i = tagRead();
		while (i != -1) {
			tagBuffer.append((char) i);
			i = tagRead();
		}
		String tag = tagBuffer.toString();
		if (isSpecialTag(tag)) {
			resolvedObject = tag;
		} else {
			// now process tag content detected
			resolvedObject = getHandler().endTag(tag, context);
		}
		if (!forceToString && !(resolvedObject instanceof String)) {
			i = basicRead();
			if (i != -1) {
				unread(i);
			} else {
				return -1;
			}
		}
		forceToString = true;
		if (resolvedObject != null) {
			String tempString = String.valueOf(resolvedObject);
			// we do not check for tags in result recursive
			checkLength = bufferLength;
			unread(tempString.toCharArray(), 0, tempString.length());
		}

		// we make a full read to enable processing for an immediate new tag
		return read();
	}

	public void setEndTag(String tag) {
		if (endTag != null && unescapeReader != null) {
			unescapeReader.removeEscapedCharacter(endTag[0]);
		}
		endTag = tag.toCharArray();
		if (unescapeReader != null) {
			unescapeReader.addEscapedCharacter(endTag[0], endTag[0]);
		}
	}

	public void setForceToString(boolean forceToString) {
		this.forceToString = forceToString;
	}

	public void setResolvedObject(Object resolvedObject) {
		this.resolvedObject = resolvedObject;
	}

	public void setStartTag(String tag) {
		if (startTag != null && unescapeReader != null) {
			unescapeReader.removeEscapedCharacter(startTag[0]);
		}
		startTag = tag.toCharArray();
		if (unescapeReader != null) {
			unescapeReader.addEscapedCharacter(startTag[0], startTag[0]);
		}
	}

	/**
	 * Read the underlying stream until the end tag is encountered. When we find
	 * the end tag, we return -1, anything else is IOException.
	 * 
	 * @return next char from stream between tags
	 * @throws IOException
	 */
	protected int tagRead() throws IOException {
		int i = super.read();
		if (i == -1) {
			throw new IOException("end tag '" + new String(endTag)
					+ "' missing");
		}
		if ((i == endTag[0])
				&& (unescapeReader == null || !unescapeReader.isMapped())) {
			return scanEndTag();
		}
		return i;
	}

	protected void unread(char[] chars, int start, int len) {
		if (readBuffer.length < (bufferLength + len)) {
			char[] newBuffer = new char[(bufferLength + len) * 2];
			System.arraycopy(readBuffer, 0, newBuffer, 0, bufferLength);
			readBuffer = newBuffer;
		}
		for (int i = (start + len) - 1; i >= start; i--) {
			readBuffer[bufferLength++] = chars[i];
		}
	}

	protected void unread(int c) {
		if (readBuffer.length < (bufferLength + 1)) {
			char[] newBuffer = new char[(bufferLength + 1) * 2];
			System.arraycopy(readBuffer, 0, newBuffer, 0, bufferLength);
			readBuffer = newBuffer;
		}
		readBuffer[bufferLength++] = (char) c;
	}
}
