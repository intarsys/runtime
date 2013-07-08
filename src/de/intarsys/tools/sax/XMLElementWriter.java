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
package de.intarsys.tools.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * The {@link ISAXElementHandler} for writing out an XML datastructure.
 * 
 * This is an internal class to {@link XMLWriter}
 */
public class XMLElementWriter extends SAXAbstractElementWriter {

	protected String acceptTag(String name) throws SAXException {
		StringBuilder buf = new StringBuilder();
		if ((name != null) && (name.length() > 0)
				&& !mayBeFirstChar(name.charAt(0))) {
			if (isFixTag()) {
				buf.append('_');
			} else {
				throw new SAXException("first character in tag <" + name
						+ "> must be a letter, '_' or ':'");
			}
		}
		buf.append(name);
		for (int i = 0; i < buf.length(); ++i) {
			if (!isNameChar(buf.charAt(i))) {
				if (isFixTag()) {
					buf.setCharAt(i, '_');
				} else {
					throw new SAXException("illegal characters in tag <" + name
							+ ">: " + buf.charAt(i));
				}
			}
		}
		return new String(buf);
	}

	@Override
	public void characters(char[] ch, int start, int len) throws SAXException {
		writeEsc(ch, start, len, false);
		super.characters(ch, start, len);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (!isLeaf() && isPrettyPrint()) {
			writeln();
			writeIndent();
		}
		write("</");
		writeName(uri, localName, qName, true);
		write('>');
		if (getNesting() == 1) {
			writeln();
		}
		super.endElement(uri, localName, qName);
	}

	public int getIndent() {
		return ((XMLWriter) getContextHandler()).getIndent();
	}

	public boolean isFixTag() {
		return ((XMLWriter) getContextHandler()).isFixTag();
	}

	protected boolean isNameChar(char c) {
		return mayBeFirstChar(c) || ((c >= '0') && (c <= '9')) || (c == '.')
				|| (c == '-');
	}

	public boolean isPrettyPrint() {
		return ((XMLWriter) getContextHandler()).isPrettyPrint();
	}

	public boolean isWriteAttributes() {
		return ((XMLWriter) getContextHandler()).isWriteAttributes();
	}

	protected boolean mayBeFirstChar(char c) {
		return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))
				|| (c == '_') || (c == ':');
	}

	@Override
	public ISAXElementHandler startElement(String uri, String localName,
			String qName, Attributes atts) throws SAXException {
		if (isPrettyPrint()) {
			writeln();
			writeIndent();
		}
		write('<');
		writeName(uri, localName, qName, true);
		if (isWriteAttributes()) {
			writeAttributes(atts);
		}
		write('>');
		return new XMLElementWriter();
	}

	protected void writeAttributes(Attributes atts) throws SAXException {
		if (atts == null) {
			return;
		}
		int len = atts.getLength();
		for (int i = 0; i < len; i++) {
			char[] ch = atts.getValue(i).toCharArray();
			write(' ');
			writeName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i),
					false);
			write("=\"");
			writeEsc(ch, 0, ch.length, true);
			write('"');
		}
	}

	protected void writeEsc(char[] ch, int start, int length, boolean isAttVal)
			throws SAXException {
		for (int i = start; i < (start + length); i++) {
			switch (ch[i]) {
			case '&':
				write("&amp;");
				break;
			case '<':
				write("&lt;");
				break;
			case '>':
				write("&gt;");
				break;
			case '\"':
				if (isAttVal) {
					write("&quot;");
				} else {
					write('\"');
				}
				break;
			default:
				if (ch[i] > '\u007f') {
					write("&#");
					write(Integer.toString(ch[i]));
					write(';');
				} else {
					write(ch[i]);
				}
			}
		}
	}

	protected void writeIndent() throws SAXException {
		if ((getIndent() > 0) && (getNesting() > 1)) {
			int n = getIndent() * (getNesting() - 1);
			char[] ch = new char[n];
			for (int i = 0; i < n; i++) {
				ch[i] = ' ';
			}
			write(ch, 0, n);
		}
	}

	protected void writeName(String uri, String localName, String qName,
			boolean isElement) throws SAXException {
		String name = acceptTag(localName);
		write(name);
	}
}
