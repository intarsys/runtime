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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.stream.StreamTools;

public final class SAXTools {
	private static final String PROP_VALUE = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
	private static final String PROP_JAVAX_XML_PARSERS_SAX_PARSER_FACTORY = "javax.xml.parsers.SAXParserFactory";

	/**
	 * Creates a factory for namespace-aware and non-validating SAX parsers for which access to external entities
	 * and DTDs is disabled.
	 *
	 * @return a factory for SAX parsers
	 * @throws SAXException if the created parser factory does not support secure processing
	 * @throws ParserConfigurationException if the parser factory cannot be created
	 */
	public static SAXParserFactory createSecureParserFactory() throws SAXException, ParserConfigurationException {
		// try speed up parser instantiation
		String parserFactoryName = System.getProperty(PROP_JAVAX_XML_PARSERS_SAX_PARSER_FACTORY);
		if (parserFactoryName == null) {
			System.setProperty(PROP_JAVAX_XML_PARSERS_SAX_PARSER_FACTORY, PROP_VALUE);
		}

		//NOSONAR JDK's default implementation disables all protocols for external entities when secure processing is enabled.
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setNamespaceAware(true);
		factory.setValidating(false);

		return factory;
	}

	/**
	 * Calls {@link #createSecureParserFactory()} for backwards compatibility.
	 *
	 * @deprecated Use {@link #createSecureParserFactory()} instead
	 */
	@Deprecated(since = "4.25", forRemoval = true)
	public static SAXParserFactory getParserFactory() {
		try {
			return createSecureParserFactory();
		} catch (SAXException | ParserConfigurationException exception) {
			throw new RuntimeException("Failed to create SAXParserFactory", exception);
		}
	}

	public static Object deserialize(char[] ch) throws IOException {
		return deserialize(ch, 0, ch.length);
	}

	public static Object deserialize(char[] ch, int start, int length) throws IOException {
		return deserialize(new String(ch, start, length));
	}

	public static Object deserialize(String value) throws IOException {
		byte[] decodedValue;
		ByteArrayInputStream byteArrayStream;
		ObjectInputStream objectStream = null;
		Object object;

		try {
			decodedValue = Base64.decode(value);
			byteArrayStream = new ByteArrayInputStream(decodedValue);
			objectStream = new ObjectInputStream(byteArrayStream);
			object = objectStream.readObject();
			objectStream.close();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			StreamTools.close(objectStream);
		}
		return object;
	}

	public static char[] serializeToCharArray(Object object) {
		return serializeToString(object).toCharArray();
	}

	public static String serializeToString(Object object) {
		ByteArrayOutputStream byteArrayStream;
		ObjectOutputStream objectStream;

		try {
			byteArrayStream = new ByteArrayOutputStream();
			objectStream = new ObjectOutputStream(byteArrayStream);
			objectStream.writeObject(object);
			objectStream.close();
		} catch (IOException ignore) {
			// don't know what we should do here anyway
			return "";
		}
		byte[] byteValue = byteArrayStream.toByteArray();
		return new String(Base64.encode(byteValue));
	}

	private SAXTools() {
		super();
	}
}
