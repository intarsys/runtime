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

/**
 * A handler to forward SAX events to registered sub-handler. This is a good
 * starting point for a filter-like implementation.
 */
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ForwardHandler implements EntityResolver, DTDHandler,
		ContentHandler, ErrorHandler {

	private ContentHandler contentHandler;

	private DTDHandler dtdHandler;

	private EntityResolver entityResolver;

	private ErrorHandler errorHandler;

	public ForwardHandler() {
		super();
		DefaultHandler defaultHandler = new DefaultHandler();
		contentHandler = defaultHandler;
		dtdHandler = defaultHandler;
		entityResolver = defaultHandler;
		errorHandler = defaultHandler;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contentHandler.characters(ch, start, length);
	}

	public void endDocument() throws SAXException {
		contentHandler.endDocument();
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		contentHandler.endElement(namespaceURI, localName, qName);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		contentHandler.endPrefixMapping(prefix);
	}

	public void error(SAXParseException exception) throws SAXException {
		errorHandler.error(exception);
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		errorHandler.fatalError(exception);
	}

	public org.xml.sax.ContentHandler getContentHandler() {
		return contentHandler;
	}

	public org.xml.sax.DTDHandler getDtdHandler() {
		return dtdHandler;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		contentHandler.ignorableWhitespace(ch, start, length);
	}

	public void notationDecl(java.lang.String name, java.lang.String publicId,
			java.lang.String systemId) throws org.xml.sax.SAXException {
		dtdHandler.notationDecl(name, publicId, systemId);
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		contentHandler.processingInstruction(target, data);
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		return entityResolver.resolveEntity(publicId, systemId);
	}

	public void setContentHandler(org.xml.sax.ContentHandler newContentHandler) {
		contentHandler = newContentHandler;
	}

	public void setDocumentLocator(Locator locator) {
		contentHandler.setDocumentLocator(locator);
	}

	public void setDtdHandler(org.xml.sax.DTDHandler newDtdHandler) {
		dtdHandler = newDtdHandler;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void skippedEntity(String name) throws SAXException {
		contentHandler.skippedEntity(name);
	}

	public void startDocument() throws SAXException {
		contentHandler.startDocument();
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		contentHandler.startElement(namespaceURI, localName, qName, atts);
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		contentHandler.startPrefixMapping(prefix, uri);
	}

	public void unparsedEntityDecl(java.lang.String name,
			java.lang.String publicId, java.lang.String systemId,
			java.lang.String notationName) throws org.xml.sax.SAXException {
		dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
	}

	public void warning(SAXParseException exception) throws SAXException {
		errorHandler.warning(exception);
	}
}
