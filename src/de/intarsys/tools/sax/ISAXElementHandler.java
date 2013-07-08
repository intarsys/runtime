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
 * An object that is able to handle all SAX events that can happen while a SAX
 * element context (an open tag) is active.
 * 
 * It is the object representation for this element while it is active on the
 * parser stack. Every SAX event is delegated by the SAXContextHandler to the
 * active {@link ISAXElementHandler} object.
 * 
 */
public interface ISAXElementHandler {
	/**
	 * this message is sent when characters are available to the receiver
	 * 
	 * @param ch
	 *            the array of characters
	 * @param start
	 *            start index of first char to read
	 * @param length
	 *            number of chars to read
	 * 
	 * @exception SAXException
	 *                if error
	 * 
	 * @see
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException;

	/**
	 * this message is sent when a child element has been terminated
	 * 
	 * @param elementHandler
	 *            the child element previously created by the receiver that is
	 *            terminated now
	 * 
	 * @exception SAXException
	 *                if error
	 * 
	 * @see
	 */
	public void endChildElement(ISAXElementHandler elementHandler)
			throws SAXException;

	/**
	 * this message is sent when the element that is ended is the document
	 * itself
	 * 
	 * @exception SAXException
	 *                if error
	 * 
	 * @see
	 */
	public void endDocumentElement() throws SAXException;

	/**
	 * this message is sent when the receiver is about to end
	 * 
	 * @param uri
	 *            the SAX namspace uri
	 * @param local
	 *            the SAX local name
	 * @param name
	 *            the SAX qualified name
	 * 
	 * @exception SAXException
	 *                if error
	 * 
	 * @see org.xml.sax
	 */
	public void endElement(String uri, String local, String name)
			throws SAXException;

	/**
	 * return the context
	 * 
	 * @return docme
	 */
	public SAXContextBasedHandler getContextHandler();

	/**
	 * return the local name of the receiver valid after initialize()
	 * 
	 * @return docme
	 */
	public String getLocalName();

	/**
	 * get the parent element of the receiver if any available valid with call
	 * to initialize()
	 * 
	 * @return docme
	 */
	public ISAXElementHandler getParent();

	/**
	 * An optional result object to be returned when this element is finished.
	 * 
	 * @return An optional result object:
	 */
	public Object getResult();

	/**
	 * return the uri of the receiver valid after initialize()
	 * 
	 * @return docme
	 */
	public String getURI();

	/**
	 * this message is sent when ignorable whitespace is available to the
	 * receiver
	 * 
	 * @param ch
	 *            the array of characters
	 * @param start
	 *            start index of first char to read
	 * @param length
	 *            number of chars to read
	 * 
	 * @exception SAXException
	 *                if error
	 * 
	 * @see
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException;

	/**
	 * this message is sent when - the receiver was created - it was pushed on
	 * the stack. - markStartLocation() was called
	 */
	public void initialize(String uri, String local, String /* raw */name,
			Attributes attrs) throws SAXException;

	/**
	 * this message is sent when - the document was started - the receiver
	 * represents a document element
	 */
	public void initializeDocumentElement() throws SAXException;

	/**
	 * an handler is a document handler when it handles the startDocument() and
	 * endDocument() messages
	 * 
	 * @return docme
	 */
	public boolean isDocumentHandler();

	/**
	 * a handler is a root handler when it is the element immediately below a
	 * document handler ther can be only a single root element in a document
	 * element.
	 * 
	 * @return docme
	 */
	public boolean isRootElementHandler();

	/**
	 * 
	 */
	public void markEndLocation(int line, int column);

	/**
	 * this message is sent when - the receiver was created - it was pushed on
	 * the stack.
	 */
	public void markStartLocation(int line, int column);

	/**
	 * this message is sent - after is is initialized()
	 */
	public void processAttributes(Attributes attrs) throws SAXException;

	/**
	 * this message is sent when - the receiver is pushed on the stack. - before
	 * initialize()
	 */
	public void setContextHandler(SAXContextBasedHandler contextHandler);

	/**
	 * this message is sent - while the receiver is in initialize()
	 */
	public void setLocalName(String name);

	/**
	 * this message is sent when - the receiver is pushed on the stack. - before
	 * initialize()
	 */
	public void setParent(ISAXElementHandler parentHandler);

	/**
	 * this message is sent - while the receiver is in initialize()
	 */
	public void setURI(String name);

	/**
	 * this message is sent when a child element has been created
	 * 
	 * @param elementHandler
	 *            the child element created
	 * 
	 * @exception SAXException
	 *                if error
	 */
	public void startChildElement(ISAXElementHandler elementHandler)
			throws SAXException;

	/**
	 * this message is sent - after all houskeeping with the element is done -
	 * before any other SAX event can reach the receiver
	 */
	public void started() throws SAXException;

	/**
	 * this message is sent - when the receiver is requested to create a child
	 * element
	 * 
	 * @return docme
	 */
	public ISAXElementHandler startElement(String uri, String local,
			String /* raw */name, Attributes attrs) throws SAXException;
}
