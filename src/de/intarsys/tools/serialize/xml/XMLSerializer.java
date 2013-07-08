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
package de.intarsys.tools.serialize.xml;

import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.intarsys.tools.serialize.CommonSerializer;
import de.intarsys.tools.serialize.SerializationContext;

/**
 * An abstract implementation for the ease of creating XML serializer objects.
 * <p>
 * This is not much but a common super class to find all XML serializers easily.
 * <p>
 * todo 1 implement serializing framework or lookup common implementation.
 * 
 */
abstract public class XMLSerializer extends CommonSerializer {

	/** The default encoding of the xml file we will write */
	protected static String DEFAULT_ENCODING = "iso-8859-1";

	/**
	 * A commonly used object representing th empty arguments collection.
	 */
	protected static AttributesImpl ATTRS_EMPTY = new AttributesImpl();

	protected static void addAttribute(AttributesImpl attrs, String name,
			String value) {
		if (value == null) {
			return;
		}
		attrs.addAttribute("", name, name, null, value);
	}

	/**
	 * @param value
	 * @return
	 */
	protected static String normalizeAttributeValue(String value) {
		// value = value.replaceAll("\\\\", "\\\\");
		// value = value.replaceAll("\\r", "");
		// value = value.replaceAll("\\n", "\\\\n");
		return value;
	}

	/**
	 * Flag if we serialize a complete document or only a fragment (a single XML
	 * element node)
	 */
	private boolean createDocument = false;

	public XMLSerializer(SerializationContext context) {
		super(context);
	}

	public XMLSerializer(SerializationContext context, boolean createDocument) {
		super(context);
		this.createDocument = createDocument;
	}

	protected void doCharacters(ContentHandler handler, char[] chars)
			throws SAXException {
		if (chars == null) {
			return;
		}
		handler.characters(chars, 0, chars.length);
	}

	protected void doCharacters(ContentHandler handler, String string)
			throws SAXException {
		if (string == null) {
			return;
		}
		char[] chars = string.toCharArray();
		handler.characters(chars, 0, chars.length);
	}

	protected void doElement(ContentHandler handler, String name,
			AttributesImpl attrs, String value) throws SAXException,
			IOException {
		if ((value == null) || (value.length() == 0)) {
			return;
		}
		handler.startElement("", name, name, attrs);
		char[] chars = value.toCharArray();
		handler.characters(chars, 0, chars.length);
		handler.endElement("", name, name);
	}

	protected void doElement(ContentHandler handler, String name, String value)
			throws SAXException, IOException {
		if ((value == null) || (value.length() == 0)) {
			return;
		}
		handler.startElement("", name, name, ATTRS_EMPTY);
		char[] chars = value.toCharArray();
		handler.characters(chars, 0, chars.length);
		handler.endElement("", name, name);
	}

	protected void doEndElement(ContentHandler handler, String name)
			throws SAXException {
		handler.endElement("", name, name);
	}

	protected void doStartElement(ContentHandler handler, String name,
			AttributesImpl attrs) throws SAXException {
		handler.startElement("", name, name, attrs);
	}

	public boolean isCreateDocument() {
		return createDocument;
	}

	final public void serialize(Object object) throws IOException {
		XMLSerializationContext xmlContext = (XMLSerializationContext) getContext();
		try {
			createContentHandler(xmlContext).startDocument();
			serialize(object, createContentHandler(xmlContext));
			createContentHandler(xmlContext).endDocument();
		} catch (SAXException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}
	}

	protected ContentHandler createContentHandler(
			XMLSerializationContext xmlContext) {
		return xmlContext.getContentHandler();
	}

	abstract protected void serialize(Object object, ContentHandler handler)
			throws SAXException, IOException;

	protected void serialize(Object object, OutputStream os,
			boolean createDocument) throws IOException {
		ContentHandler handler = XMLSerializationContext.createContentHandler(
				os, createDocument);
		try {
			handler.startDocument();

			serialize(object, handler);

			handler.endDocument();
		} catch (SAXException e) {
			IOException ioe = new IOException("sax exception ("
					+ e.getMessage() + ")");
			ioe.initCause(e);
			throw ioe;
		}
	}

	public void setCreateDocument(boolean createDocument) {
		this.createDocument = createDocument;
	}

}
