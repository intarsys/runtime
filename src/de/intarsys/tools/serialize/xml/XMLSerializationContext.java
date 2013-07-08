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
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;

import de.intarsys.tools.serialize.SerializationContext;

/**
 * 
 */
public class XMLSerializationContext extends SerializationContext {

	static public ContentHandler createContentHandler(OutputStream os,
			boolean createDocument) throws IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		if (factory.getFeature(SAXTransformerFactory.FEATURE)) {
			try {
				factory.setAttribute("indent-number", new Integer(4));
			} catch (IllegalArgumentException ignore) {
				// works for native jre 1.5 XML lib, but not for xalan.
				// But Xalan will respect the "indent-amount" property below,
				// which is ignore by java 1.5
			}
			SAXTransformerFactory saxFactory = (SAXTransformerFactory) factory;
			TransformerHandler transformerHandler;
			try {
				transformerHandler = saxFactory.newTransformerHandler();
				if (!createDocument) {
					transformerHandler.getTransformer().setOutputProperty(
							OutputKeys.OMIT_XML_DECLARATION, "yes");
				}
				transformerHandler.getTransformer().setOutputProperty(
						OutputKeys.INDENT, "yes");
				transformerHandler.getTransformer().setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");
			} catch (TransformerConfigurationException e) {
				IOException ioe = new IOException("unexpected error");
				ioe.initCause(e);
				throw ioe;
			}
			transformerHandler.setResult(new StreamResult(
					new OutputStreamWriter(os, "utf-8")));
			return transformerHandler;
		} else {
			throw new UnsupportedOperationException("compatibility error");
		}
	}

	private ContentHandler contentHandler;

	public XMLSerializationContext(ContentHandler contentHandler) {
		super();
		this.contentHandler = contentHandler;
	}

	public XMLSerializationContext(OutputStream os, boolean createDocument)
			throws IOException {
		super();
		this.contentHandler = createContentHandler(os, createDocument);
	}

	public ContentHandler getContentHandler() {
		return contentHandler;
	}
}
