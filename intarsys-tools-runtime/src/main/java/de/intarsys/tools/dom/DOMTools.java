/*
 * Copyright (c) 2012, intarsys GmbH
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
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import de.intarsys.tools.xml.TransformerTools;

public class DOMTools {
	public static final Element[] NO_ELEMENTS = new Element[0];

	private static DocumentBuilderFactory sharedDocumentBuilderFactory = createSecureDocumentBuilderFactory();

	/**
	 * Creates a {@code DocumentBuilderFactory} that does not resolve external DTDs and schemas to prevent XML
	 * external entity injections (XXE).
	 *
	 * @see <a href="https://rules.sonarsource.com/java/RSPEC-2755">Sonar rules: XML parsers should not be vulnerable to XXE attacks</a>
	 */
	public static DocumentBuilderFactory createSecureDocumentBuilderFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		return factory;
	}

	/**
	 * Appends a new child element to {@code parent} using the {@code namespaceUri} and {@code localName}. If no prefix
	 * is yet defined, {@code defaultPrefix} is used.
	 *
	 * @param parent
	 *            the node to which to append the new element
	 * @param namespaceUri
	 *            the new element's namespace URI
	 * @param localName
	 *            the new element's local name
	 * @return the new element
	 */
	public static Element createChild(Node parent, String namespaceUri, String defaultPrefix, String localName) {
		Document document = parent.getOwnerDocument();
		String prefix = parent.lookupPrefix(namespaceUri);
		Element child = createElement(document, namespaceUri, prefix == null ? defaultPrefix : prefix, localName);
		parent.appendChild(child);
		return child;
	}

	/**
	 * Appends a new child element of the given parent with the given local name
	 * and namespace URI. If {@code context} defines a prefix for the
	 * given namespace URI, it will be used to qualify the element's name.
	 *
	 * @param context
	 * @param parent
	 *            the node to which to append the new element
	 * @param namespaceUri
	 *            the new element's namespace URI
	 * @param localName
	 *            the new element's local name
	 *
	 * @return the new element
	 */
	public static Element createChild(XMLCryptoContext context, Node parent, String namespaceUri, String localName) {
		Document document = parent.getOwnerDocument();
		Element child = createElement(context, document, namespaceUri, localName);
		parent.appendChild(child);
		return child;
	}

	public static String createDerivedId(String id, String suffix) {
		return id + "-" + suffix;
	}

	public static Document createDocument() {
		try {
			return sharedDocumentBuilderFactory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Shared factory should be able to create a document builder", exception);
		}
	}

	/**
	 * Creates an element with {@code namespaceUri} and {@code qualifiedName}.
	 *
	 * @param document
	 *            the owner document
	 * @param namespaceUri
	 *            the new element's namespace URI
	 * @param qualifiedName
	 *            the new element's qualified name
	 * @return the new element
	 */
	public static Element createElement(Document document, String namespaceUri, String qualifiedName) {
		return document.createElementNS(namespaceUri, qualifiedName);
	}

	/**
	 * Creates an element with {@code namespaceUri}, {@code prefix} and
	 * {@code localName}.
	 *
	 * @param document
	 *            the owner document
	 * @param namespaceUri
	 *            the new element's namespace URI
	 * @param prefix
	 *            the default prefix to use if none defined
	 * @param localName
	 *            the new element's local name
	 * @return the new element
	 */
	public static Element createElement(Document document, String namespaceUri, String prefix, String localName) {
		String qualifiedName = prefix == null ? localName : prefix + ':' + localName;
		return createElement(document, namespaceUri, qualifiedName);
	}

	/**
	 * Creates an element with the given local name and namespace URI. If
	 * {@link XMLCryptoContext#getNamespacePrefix(String, String)} defines a prefix
	 * for the given namespace URI, it will be used to qualify the element's name.
	 *
	 * @param context
	 * @param document     the owner document
	 * @param namespaceUri the new element's namespace URI
	 * @param localName    the new element's local name
	 *
	 * @return the new element
	 */
	public static Element createElement(XMLCryptoContext context, Document document, String namespaceUri,
			String localName) {
		String prefix = context.getNamespacePrefix(namespaceUri, null);
		String qualifiedName = prefix == null ? localName : prefix + ':' + localName;
		return document.createElementNS(namespaceUri, qualifiedName);
	}

	public static String createId(String prefix) {
		return prefix + "-" + UUID.randomUUID().toString();
	}

	public static Iterator<Attr> getAttributeIterator(Element element) {
		final NamedNodeMap nodes = element.getAttributes();
		return new Iterator<>() {
			private int i;

			@Override
			public boolean hasNext() {
				return i < nodes.getLength();
			}

			@Override
			public Attr next() {
				if (i >= nodes.getLength()) {
					throw new NoSuchElementException();
				}
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

	public static String getAttrString(Element elem, String name, String defaultValue) {
		String value = elem.getAttribute(name);
		if (StringTools.isEmpty(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static Element getDirectChild(Node parent, String name) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (name.equals(child.getLocalName())) {
					return child;
				}
			}
		}
		return null;
	}

	public static Element[] getDirectChildren(Node parent) {
		NodeList children = parent.getChildNodes();
		if (children.getLength() == 0) {
			return NO_ELEMENTS;
		}
		return toElementArray(children);
	}

	public static Element[] getDirectChildren(Node parent, String name) {
		Element[] children = getDirectChildren(parent);
		if (children.length == 0) {
			return children;
		}
		ArrayList<Element> result = new ArrayList<>();
		for (Element child : children) {
			if (child.getLocalName().equals(name)) {
				result.add(child);
			}
		}
		return ArrayTools.toArray(Element.class, result);
	}

	public static Element getElement(Element element, String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			NodeList nodes = element.getElementsByTagNameNS("*", segments[0]); //$NON-NLS-1$
			if (nodes.getLength() == 0) {
				nodes = element.getElementsByTagName(segments[0]);
			}
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
		NodeList nodes = element.getElementsByTagNameNS("*", "*"); //$NON-NLS-1$
		if (nodes.getLength() == 0) {
			nodes = element.getElementsByTagName("*"); //$NON-NLS-1$
		}
		final NodeList finalNodes = nodes;
		return new Iterator<>() {
			private int i;

			@Override
			public boolean hasNext() {
				return i < finalNodes.getLength();
			}

			@Override
			public Element next() {
				if (i >= finalNodes.getLength()) {
					throw new NoSuchElementException();
				}
				return (Element) finalNodes.item(i++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static Iterator<Element> getElementIterator(Element element, String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			NodeList nodes = element.getElementsByTagNameNS("*", segments[0]); //$NON-NLS-1$
			if (nodes.getLength() == 0) {
				nodes = element.getElementsByTagName(segments[0]);
			}
			final NodeList finalNodes = nodes;
			return new Iterator<>() {
				private int i;

				@Override
				public boolean hasNext() {
					return i < finalNodes.getLength();
				}

				@Override
				public Element next() {
					if (i >= finalNodes.getLength()) {
						throw new NoSuchElementException();
					}
					return (Element) finalNodes.item(i++);
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
		if (nodes.getLength() == 0) {
			nodes = element.getElementsByTagName("*"); //$NON-NLS-1$
		}
		return toElementArray(nodes);
	}

	public static Element[] getElements(Element element, String name) {
		String[] segments = name.split("\\."); //$NON-NLS-1$
		if (segments.length == 1) {
			NodeList nodes = element.getElementsByTagNameNS("*", segments[0]); //$NON-NLS-1$
			if (nodes.getLength() == 0) {
				nodes = element.getElementsByTagName(segments[0]);
			}
			return toElementArray(nodes);
		} else {
			for (int i = 0; i < segments.length - 1; i++) {
				element = getElement(element, segments[i]);
				if (element == null) {
					return null; // NOSONAR
				}
			}
			return getElements(element, segments[segments.length - 1]);
		}
	}

	public static Element getFirstDirectChild(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return (Element) children.item(i);
			}
		}
		return null;
	}

	public static Element getFirstElementByTagNameNS(Element parent, String namespace, String name) {
		NodeList nodeList = parent.getElementsByTagNameNS(namespace, name); // $NON-NLS-1$
		if (nodeList.getLength() == 0) {
			return null;
		}
		return (Element) nodeList.item(0);
	}

	public static Element getLastElementByTagNameNS(Element parent, String namespace, String name) {
		NodeList nodeList = parent.getElementsByTagNameNS(namespace, name); // $NON-NLS-1$
		if (nodeList.getLength() == 0) {
			return null;
		}
		return (Element) nodeList.item(nodeList.getLength() - 1);
	}

	/**
	 * Scans all namespace declarations
	 *
	 * @param element
	 * @return All declared namespaces
	 */
	public static Map<String, String> getNamespaceDeclarations(Element element) {
		Map<String, String> namespaces = new HashMap<>(10);
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

	/**
	 * Lookup a child with {@code localName} within {@code parent} or create if not
	 * exists.
	 *
	 * @param parent       the node to which to append the new element
	 * @param namespaceUri the new element's namespace URI
	 * @param prefix
	 * @param localName    the new element's local name
	 *
	 * @return the new element
	 */
	public static Element getOrCreateChild(
			Node parent,
			String namespaceUri,
			String prefix,
			String localName) {
		Element child = getDirectChild(parent, localName);
		if (child == null) {
			child = createChild(parent, namespaceUri, prefix, localName);
		}
		return child;
	}

	/**
	 * Lookup a child with {@code localName} within {@code parent} or create if
	 * not exists.
	 *
	 * @param context
	 * @param parent
	 *            the node to which to append the new element
	 * @param namespaceUri
	 *            the new element's namespace URI
	 * @param localName
	 *            the new element's local name
	 *
	 * @return the new element
	 */
	public static Element getOrCreateChild(XMLCryptoContext context, Node parent, String namespaceUri,
			String localName) {
		Element child = getDirectChild(parent, localName);
		if (child == null) {
			Document document = parent.getOwnerDocument();
			child = createElement(context, document, namespaceUri, localName);
			parent.appendChild(child);

		}
		return child;
	}

	public static Document parseDocument(byte[] value) throws IOException, SAXException {
		InputSource source = new InputSource(new ByteArrayInputStream(value));
		return parseDocument(source);
	}

	public static Document parseDocument(File file) throws IOException, SAXException {
		InputStream is = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is, 10000);
		return parseDocument(bis, true);
	}

	protected static Document parseDocument(InputSource source) throws IOException, SAXException {
		try {
			return sharedDocumentBuilderFactory
					.newDocumentBuilder()
					.parse(source);
		} catch (ParserConfigurationException e) {
			throw new IOException("parser configuration error", e);
		}
	}

	public static Document parseDocument(InputStream is, boolean close) throws IOException, SAXException {
		try {
			InputSource source = new InputSource(is);
			return parseDocument(source);
		} finally {
			if (close) {
				StreamTools.close(is);
			}
		}
	}

	public static Document parseDocument(Reader r) throws IOException, SAXException {
		InputSource source = new InputSource(r);
		return parseDocument(source);
	}

	public static Document parseDocument(String value) throws IOException, SAXException {
		InputSource source = new InputSource(new StringReader(value));
		return parseDocument(source);
	}

	public static Element parseElement(byte[] value) throws IOException, SAXException {
		return parseDocument(value).getDocumentElement();
	}

	public static Element parseElement(File file) throws IOException, SAXException {
		return parseDocument(file).getDocumentElement();
	}

	protected static Element parseElement(InputSource source) throws IOException, SAXException {
		return parseDocument(source).getDocumentElement();
	}

	public static Element parseElement(InputStream is, boolean close) throws IOException, SAXException {
		return parseDocument(is, close).getDocumentElement();
	}

	public static Element parseElement(String value) throws IOException, SAXException {
		return parseDocument(value).getDocumentElement();
	}

	public static void serialize(Document doc, OutputStream stream)
			throws TransformerFactoryConfigurationError, TransformerException {
		serialize(doc, new StreamResult(stream));
	}

	public static void serialize(Document doc, Writer writer)
			throws TransformerFactoryConfigurationError, TransformerException {
		serialize(doc, new StreamResult(writer));
	}

	private static void serialize(Document document, Result result) throws TransformerException {
		Transformer transformer = TransformerTools
				.createSecureTransformerFactory()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		TransformerTools.setIndentAmount(transformer, 2);
		transformer.transform(new DOMSource(document), result);
	}

	public static void setIdAttribute(Element element, String qualifiedName, String value) {
		element.setAttribute(qualifiedName, value);
		element.setIdAttribute(qualifiedName, true);
	}

	public static void setIdAttributeNS(Element element, String namespaceURI, String qualifiedName, String value) {
		element.setAttributeNS(namespaceURI, qualifiedName, value);
		element.setIdAttributeNS(namespaceURI, qualifiedName, true);
	}

	public static Element[] toElementArray(NodeList nodes) {
		ArrayList<Element> result = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element) nodes.item(i));
			}
		}
		return ArrayTools.toArray(Element.class, result);
	}

	public static String toString(Document doc) {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			serialize(doc, writer);
			return writer.toString();
		} catch (TransformerException e) {
			return "<error>";
		} finally {
			StreamTools.close(writer);
		}
	}

	private DOMTools() {
		//
	}
}
