/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.dom;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.intarsys.tools.collection.ArrayTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;

public class DOMTools {

	private static final Logger Log = PACKAGE.Log;

	private static DocumentBuilderFactory documentBuilderFactory;

	private static DocumentBuilder documentBuilder;

	static {
		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (Exception e) {
			Log.log(Level.WARNING, "no document builder available", e);
		}
	}

	public static final Element[] NO_ELEMENTS = new Element[0];

	public static Document createDocument() {
		return documentBuilder.newDocument();
	}

	public static Iterator<Attr> getAttributeIterator(Element element) {
		final NamedNodeMap nodes = element.getAttributes();
		return new Iterator<Attr>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < nodes.getLength();
			}

			@Override
			public Attr next() {
				return (Attr) nodes.item(i++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static int getAttrInt(Element elem, String name, int defaultValue) {
		String value = elem.getAttribute(name);
		if (StringTools.isEmpty(value)) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value.trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public static String getAttrString(Element elem, String name,
			String defaultValue) {
		String value = elem.getAttribute(name);
		if (StringTools.isEmpty(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static Element getDirectChild(Element element, String name) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Element.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (name.equals(child.getLocalName())) {
					return child;
				}
			}
		}
		return null;
	}

	public static Element[] getDirectChildren(Element element) {
		NodeList children = element.getChildNodes();
		if (children.getLength() == 0) {
			return NO_ELEMENTS;
		}
		return toElementArray(children);
	}

	public static Element[] getDirectChildren(Element element, String name) {
		Element[] children = getDirectChildren(element);
		if (children.length == 0) {
			return children;
		}
		ArrayList<Element> result = new ArrayList<Element>();
		for (Element child : children) {
			if (child.getLocalName().equals(name)) {
				result.add(child);
			}
		}
		return (Element[]) ArrayTools.toArray(Element.class, result);
	}

	public static Element getElement(Element element, String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			NodeList nodes = element.getElementsByTagNameNS("*", segments[0]); //$NON-NLS-1$
			if (nodes.getLength() == 0) {
				return null;
			}
			return (Element) nodes.item(0);
		} else {
			for (int i = 0; i < segments.length; i++) {
				element = getElement(element, segments[i]);
				if (element == null) {
					return null;
				}
			}
			return element;
		}
	}

	public static Iterator<Element> getElementIterator(Element element) {
		final NodeList nodes = element.getElementsByTagNameNS("*", "*"); //$NON-NLS-1$
		return new Iterator<Element>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < nodes.getLength();
			}

			@Override
			public Element next() {
				return (Element) nodes.item(i++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static Iterator<Element> getElementIterator(Element element,
			String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			final NodeList nodes = element.getElementsByTagNameNS(
					"*", segments[0]); //$NON-NLS-1$
			return new Iterator<Element>() {
				private int i = 0;

				@Override
				public boolean hasNext() {
					return i < nodes.getLength();
				}

				@Override
				public Element next() {
					return (Element) nodes.item(i++);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else {
			for (int i = 0; i < segments.length - 1; i++) {
				element = getElement(element, segments[i]);
				if (element == null) {
					return null;
				}
			}
			return getElementIterator(element, segments[segments.length - 1]);
		}
	}

	public static Element[] getElements(Element element) {
		NodeList nodes = element.getElementsByTagNameNS("*", "*"); //$NON-NLS-1$
		return toElementArray(nodes);
	}

	public static Element[] getElements(Element element, String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			NodeList nodes = element.getElementsByTagNameNS("*", segments[0]); //$NON-NLS-1$
			return toElementArray(nodes);
		} else {
			for (int i = 0; i < segments.length - 1; i++) {
				element = getElement(element, segments[i]);
				if (element == null) {
					return null;
				}
			}
			return getElements(element, segments[segments.length - 1]);
		}
	}

	public static Element getFirstDirectChild(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Element.ELEMENT_NODE) {
				return (Element) children.item(i);
			}
		}
		return null;
	}

	/**
	 * Scans all namespace declarations
	 * 
	 * @param element
	 * @return All declared namespaces
	 */
	public static Map<String, String> getNamespaceDeclarations(Element element) {
		Map<String, String> namespaces = new HashMap<String, String>(10);
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			if ("xmlns".equals(node.getPrefix())) { //$NON-NLS-1$
				String nsName = node.getLocalName();
				String nsUri = node.getTextContent();
				namespaces.put(nsName, nsUri);
			}
		}
		return namespaces;
	}

	static public Document parseDocument(byte[] value) throws IOException,
			SAXException {
		InputSource source = new InputSource(new ByteArrayInputStream(value));
		return parseDocument(source);
	}

	static public Document parseDocument(File file) throws IOException,
			SAXException {
		InputStream is = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is, 10000);
		return parseDocument(bis, true);
	}

	static protected Document parseDocument(InputSource source)
			throws IOException, SAXException {
		try {
			DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
			Document doc = db.parse(source);
			return doc;
		} catch (ParserConfigurationException e) {
			throw new IOException("parser configuration error", e);
		}
	}

	static public Document parseDocument(InputStream is, boolean close)
			throws IOException, SAXException {
		try {
			InputSource source = new InputSource(is);
			return parseDocument(source);
		} finally {
			if (close) {
				StreamTools.close(is);
			}
		}
	}

	static public Document parseDocument(Reader r) throws IOException,
			SAXException {
		InputSource source = new InputSource(r);
		return parseDocument(source);
	}

	static public Document parseDocument(String value) throws IOException,
			SAXException {
		InputSource source = new InputSource(new StringReader(value));
		return parseDocument(source);
	}

	static public Element parseElement(byte[] value) throws IOException,
			SAXException {
		return parseDocument(value).getDocumentElement();
	}

	static public Element parseElement(File file) throws IOException,
			SAXException {
		return parseDocument(file).getDocumentElement();
	}

	static protected Element parseElement(InputSource source)
			throws IOException, SAXException {
		return parseDocument(source).getDocumentElement();
	}

	static public Element parseElement(InputStream is, boolean close)
			throws IOException, SAXException {
		return parseDocument(is, close).getDocumentElement();
	}

	static public Element parseElement(String value) throws IOException,
			SAXException {
		return parseDocument(value).getDocumentElement();
	}

	public static Element[] toElementArray(NodeList nodes) {
		ArrayList<Element> result = new ArrayList<Element>();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
				result.add((Element) nodes.item(i));
			}
		}
		return (Element[]) ArrayTools.toArray(Element.class, result);
	}

	private DOMTools() {
		//
	}
}
