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
	private int charCount = 0;

	/** data is a derived value from chars */
	private String data;

	/** The line where this element started */
	private int line = 0;

	/** The column where this element started */
	private int column = 0;

	/** The line where this element ended */
	private int endline = 0;

	/** The column where this element ended */
	private int endcolumn = 0;

	/** Flag if this node is a leaf node. */
	private boolean leaf = true;

	/** an optional representation of all attributes as a map */
	private Map parameters;

	/**
	 * Create a SAXAbstractElementHandler
	 */
	public SAXAbstractElementHandler() {
		super();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (length == 0) {
			return;
		}
		if (!isCharacterDataAllowed()) {
			if (isWhitespaceAllowed()) {
				if (new String(ch, start, length).trim().length() == 0) {
					return;
				}
			}
			throw new SAXParseException("unexpected character data in <"
					+ getLocalName() + ">", getLocator());
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

	public void endChildElement(ISAXElementHandler elementHandler)
			throws SAXException {
		// do nothing
	}

	public void endDocumentElement() throws SAXException {
		// do nothing
	}

	public void endElement(String pUri, String local, String name)
			throws SAXException {
		// do nothing
	}

	public char[] getChars() {
		return chars;
	}

	public int getColumn() {
		return column;
	}

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

	/**
	 * Insert the method's description here. Creation date: (17.05.2001
	 * 12:10:34)
	 * 
	 * @return int
	 */
	public int getEndColumn() {
		return endcolumn;
	}

	/**
	 * Insert the method's description here. Creation date: (17.05.2001
	 * 12:10:34)
	 * 
	 * @return int
	 */
	public int getEndLine() {
		return endline;
	}

	/**
	 * Insert the method's description here. Creation date: (17.05.2001
	 * 12:10:34)
	 * 
	 * @return int
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Insert the method's description here. Creation date: (02.05.2002
	 * 16:06:44)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getLocalName() {
		return localName;
	}

	/**
	 * Die Beschreibung der Methode hier eingeben. Erstellungsdatum: (26.05.00
	 * 13:01:38)
	 * 
	 * @return de.intarsys.tools.sax.SAXContextBasedHandler
	 */
	public Locator getLocator() {
		return getContextHandler().getLocator();
	}

	/**
	 * Die Beschreibung der Methode hier eingeben. Erstellungsdatum: (26.05.00
	 * 13:01:38)
	 * 
	 * @return de.intarsys.tools.sax.SAXContextBasedHandler
	 */
	public int getNesting() {
		return getContextHandler().getNesting();
	}

	/**
	 * @return Returns the parameters.
	 */
	public Map getParameters() {
		return parameters;
	}

	/**
	 * Insert the method's description here. Creation date: (03.07.00 11:33:13)
	 * 
	 * @return de.intarsys.tools.sax.ISAXElementHandler
	 */
	public ISAXElementHandler getParent() {
		return parent;
	}

	/**
	 * Insert the method's description here. Creation date: (03.07.00 11:33:13)
	 * 
	 * @return de.intarsys.tools.sax.ISAXElementHandler
	 */
	public String getPath() {
		String result;
		result = ((getLocalName() == null) ? "?" : getLocalName());
		ISAXElementHandler current = getParent();
		while (current != null) {
			result = current.getLocalName() + "/" + result;
			current = current.getParent();
		}
		return result;
	}

	public Object getResult() {
		return null;
	}

	/**
	 * Insert the method's description here. Creation date: (02.05.2002
	 * 16:06:44)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getURI() {
		return uri;
	}

	/**
	 * docme
	 * 
	 * @param ch
	 *            docme
	 * @param start
	 *            docme
	 * @param length
	 *            docme
	 * 
	 * @throws SAXException
	 *             docme
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// do nothing
	}

	/**
	 * docme
	 * 
	 * @param uri
	 *            docme
	 * @param local
	 *            docme
	 * @param name
	 *            docme
	 * @param attrs
	 *            docme
	 * 
	 * @throws SAXException
	 *             docme
	 */
	public void initialize(String pUri, String local, String /* raw */name,
			Attributes attrs) throws SAXException {
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

	/**
	 * docme
	 * 
	 * @throws SAXException
	 *             docme
	 */
	public void initializeDocumentElement() throws SAXException {
		setLocalName("document");
	}

	/**
	 * docme
	 * 
	 * @return docme
	 */
	public boolean isCharacterDataAllowed() {
		return true;
	}

	/**
	 * docme
	 * 
	 * @return docme
	 */
	public boolean isDocumentHandler() {
		return getContextHandler().getDocumentElement() == this;
	}

	/**
	 * docme
	 * 
	 * @return
	 */
	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * docme
	 * 
	 * @return docme
	 */
	public boolean isRootElementHandler() {
		return (getParent() != null) && getParent().isDocumentHandler();
	}

	public boolean isWhitespaceAllowed() {
		return true;
	}

	public void markEndLocation(int newLine, int newColumn) {
		endline = newLine;
		endcolumn = newColumn;
	}

	public void markStartLocation(int newLine, int newColumn) {
		line = newLine;
		column = newColumn;
	}

	public void processAttribute(String name, String value)
			throws org.xml.sax.SAXException {
		parameters.put(name, value);
	}

	public void processAttributes(Attributes attrs)
			throws org.xml.sax.SAXException {
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

	public void setContextHandler(SAXContextBasedHandler newContextHandler) {
		contextHandler = newContextHandler;
	}

	public void setLine(int newLine) {
		line = newLine;
	}

	public void setLocalName(java.lang.String newLocalName) {
		localName = newLocalName;
	}

	public void setParent(ISAXElementHandler newParent) {
		parent = newParent;
	}

	public void setURI(java.lang.String newUri) {
		uri = newUri;
	}

	public void startChildElement(ISAXElementHandler element)
			throws SAXException {
		this.leaf = false;
	}

	public void started() throws SAXException {
		// do nothing
	}

	public ISAXElementHandler startElement(String pUri, String local,
			String /* raw */name, Attributes attrs) throws SAXException {
		if (getParent() == null) {
			throw (new SAXParseException("tag <" + local
					+ "> is not a supported root tag", getLocator()));
		} else {
			throw (new SAXParseException("tag <" + local + "> in <"
					+ getLocalName() + "> not supported", getLocator()));
		}
	}
}
