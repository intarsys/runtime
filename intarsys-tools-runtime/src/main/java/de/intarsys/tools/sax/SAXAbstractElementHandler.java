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
package de.intarsys.tools.sax;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An abstract superclass for implementing {@link ISAXElementHandler}
 * 
 */
public abstract class SAXAbstractElementHandler implements ISAXElementHandler {

	/** the context that created this handler */
	private SAXContextBasedHandler contextHandler;

	/** The parent handler on the event stack */
	private ISAXElementHandler parent;

	/** The local name part of the element */
	private String localName = "";

	/** The complete uri of the path */
	private String uri = "";

	/** The collected characters */
	private char[] chars;

	/** The number of characters so far. */
	private int charCount;

	/** data is a derived value from chars */
	private String data;

	/** The line where this element started */
	private int line;

	/** The column where this element started */
	private int column;

	/** The line where this element ended */
	private int endline;

	/** The column where this element ended */
	private int endcolumn;

	/** Flag if this node is a leaf node. */
	private boolean leaf = true;

	/** an optional representation of all attributes as a map */
	private Map parameters;

	protected SAXAbstractElementHandler() {
		super();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (length == 0) {
			return;
		}
		if (!isCharacterDataAllowed()) {
			if (isWhitespaceAllowed()) {
				if (new String(ch, start, length).trim().length() == 0) {
					return;
				}
			}
			throw new SAXParseException("unexpected character data in <" + getLocalName() + ">", getLocator());
		}
		char[] newChars = new char[charCount + length];
		if (charCount > 0) {
			System.arraycopy(chars, 0, newChars, 0, charCount);
		}
		System.arraycopy(ch, start, newChars, charCount, length);
		charCount += length;
		chars = newChars;
		data = null;
	}

	@Override
	public void endChildElement(ISAXElementHandler elementHandler) throws SAXException {
		// do nothing
	}

	@Override
	public void endDocumentElement() throws SAXException {
		// do nothing
	}

	@Override
	public void endElement(String pUri, String local, String name) throws SAXException {
		// do nothing
	}

	public char[] getChars() {
		return chars;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public SAXContextBasedHandler getContextHandler() {
		return contextHandler;
	}

	public java.lang.String getData() {
		if (data == null) {
			if (charCount > 0) {
				data = new String(chars);
			} else {
				data = "";
			}
		}
		return data;
	}

	public int getEndColumn() {
		return endcolumn;
	}

	public int getEndLine() {
		return endline;
	}

	public int getLine() {
		return line;
	}

	@Override
	public java.lang.String getLocalName() {
		return localName;
	}

	public Locator getLocator() {
		return getContextHandler().getLocator();
	}

	public int getNesting() {
		return getContextHandler().getNesting();
	}

	public Map getParameters() {
		return parameters;
	}

	@Override
	public ISAXElementHandler getParent() {
		return parent;
	}

	@SuppressWarnings("java:S1643")
	public String getPath() {
		String result = ((getLocalName() == null) ? "?" : getLocalName());
		ISAXElementHandler current = getParent();
		while (current != null) {
			result = current.getLocalName() + "/" + result;
			current = current.getParent();
		}
		return result;
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public java.lang.String getURI() {
		return uri;
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// do nothing
	}

	@Override
	public void initialize(String pUri, String local, String /* raw */ name, Attributes attrs) throws SAXException {
		String localOrName;

		// parsers are namespace-aware by default
		// if for some reason our parser's not, local will be null
		if (local == null) {
			localOrName = name;
		} else {
			localOrName = local;
		}
		setLocalName(localOrName);
		setURI(pUri);
	}

	@Override
	public void initializeDocumentElement() throws SAXException {
		setLocalName("document");
	}

	public boolean isCharacterDataAllowed() {
		return true;
	}

	@Override
	public boolean isDocumentHandler() {
		return getContextHandler().getDocumentElement() == this;
	}

	public boolean isLeaf() {
		return leaf;
	}

	@Override
	public boolean isRootElementHandler() {
		return (getParent() != null) && getParent().isDocumentHandler();
	}

	public boolean isWhitespaceAllowed() {
		return true;
	}

	@Override
	public void markEndLocation(int newLine, int newColumn) {
		endline = newLine;
		endcolumn = newColumn;
	}

	@Override
	public void markStartLocation(int newLine, int newColumn) {
		line = newLine;
		column = newColumn;
	}

	public void processAttribute(String name, String value) throws org.xml.sax.SAXException {
		parameters.put(name, value);
	}

	@Override
	public void processAttributes(Attributes attrs) throws org.xml.sax.SAXException {
		if ((attrs != null) && (attrs.getLength() > 0)) {
			parameters = new HashMap();
			int cntAttrs = attrs.getLength();
			for (int i = 0; i < cntAttrs; i++) {
				String attrName = attrs.getLocalName(i);
				String attrValue = attrs.getValue(i);
				processAttribute(attrName, attrValue);
			}
		}
	}

	public void reset() {
		// reset the object so it can be reused in the state stack
		localName = "";
		uri = "";
		data = null;
		chars = null;
		charCount = 0;
		line = 0;
		column = 0;
		leaf = true;
	}

	public void setChars(char[] newChars) {
		chars = newChars;
	}

	public void setColumn(int newColumn) {
		column = newColumn;
	}

	@Override
	public void setContextHandler(SAXContextBasedHandler newContextHandler) {
		contextHandler = newContextHandler;
	}

	public void setLine(int newLine) {
		line = newLine;
	}

	@Override
	public void setLocalName(java.lang.String newLocalName) {
		localName = newLocalName;
	}

	@Override
	public void setParent(ISAXElementHandler newParent) {
		parent = newParent;
	}

	@Override
	public void setURI(java.lang.String newUri) {
		uri = newUri;
	}

	@Override
	public void startChildElement(ISAXElementHandler element) throws SAXException {
		this.leaf = false;
	}

	@Override
	public void started() throws SAXException {
		// do nothing
	}

	@Override
	public ISAXElementHandler startElement(String pUri, String local, String /* raw */ name, Attributes attrs)
			throws SAXException {
		if (getParent() == null) {
			throw (new SAXParseException("tag <" + local + "> is not a supported root tag", getLocator()));
		} else {
			throw (new SAXParseException("tag <" + local + "> in <" + getLocalName() + "> not supported",
					getLocator()));
		}
	}
}
