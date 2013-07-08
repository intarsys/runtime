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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.SAXParserFactory;

import de.intarsys.tools.encoding.Base64;

/**
 * DOCUMENT ME!
 * 
 * @author tpi To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class SAXTools {
	private static SAXParserFactory parserFactory;

	public static SAXParserFactory createParserFactory() {
		// try speed up parser instantiation
		String parserFactoryName = System
				.getProperty("javax.xml.parsers.SAXParserFactory");
		if (parserFactoryName == null) {
			System.setProperty("javax.xml.parsers.SAXParserFactory",
					"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		}
		SAXParserFactory result = SAXParserFactory.newInstance();
		return result;
	}

	/**
	 * docme
	 * 
	 * @param ch
	 *            docme
	 * 
	 * @return docme
	 */
	public static Object deserialize(char[] ch) {
		return deserialize(ch, 0, ch.length);
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
	 * @return docme
	 */
	public static Object deserialize(char[] ch, int start, int length) {
		return deserialize(new String(ch, start, length));
	}

	/**
	 * docme
	 * 
	 * @param value
	 *            docme
	 * 
	 * @return docme
	 */
	public static Object deserialize(String value) {
		byte[] decodedValue;
		ByteArrayInputStream byteArrayStream;
		ObjectInputStream objectStream;
		Object object;

		try {
			decodedValue = Base64.decode(value.getBytes());
			byteArrayStream = new ByteArrayInputStream(decodedValue);
			objectStream = new ObjectInputStream(byteArrayStream);
			object = objectStream.readObject();
			objectStream.close();
		} catch (ClassNotFoundException ignore) {
			// don't know what we should do here anyway
			return null;
		} catch (IOException ignore) {
			// don't know what we should do here anyway
			return null;
		}
		return object;
	}

	public static SAXParserFactory getParserFactory() {
		if (parserFactory == null) {
			parserFactory = createParserFactory();
			parserFactory.setNamespaceAware(true);
			parserFactory.setValidating(false);
		}
		return parserFactory;
	}

	/**
	 * docme
	 * 
	 * @param object
	 *            docme
	 * 
	 * @return docme
	 */
	public static char[] serializeToCharArray(Object object) {
		return serializeToString(object).toCharArray();
	}

	/**
	 * docme
	 * 
	 * @param object
	 *            docme
	 * 
	 * @return docme
	 */
	public static String serializeToString(Object object) {
		ByteArrayOutputStream byteArrayStream;
		ObjectOutputStream objectStream;

		try {
			objectStream = new ObjectOutputStream(
					byteArrayStream = new ByteArrayOutputStream());
			objectStream.writeObject(object);
			objectStream.close();
		} catch (IOException ignore) {
			// don't know what we should do here anyway
			return null;
		}
		byte[] byteValue = byteArrayStream.toByteArray();
		return new String(Base64.encode(byteValue));
	}
}
