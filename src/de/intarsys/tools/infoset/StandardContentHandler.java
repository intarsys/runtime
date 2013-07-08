package de.intarsys.tools.infoset;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A {@link ContentHandler} to create the standard infoset implementation.
 * 
 */
public class StandardContentHandler extends DefaultHandler {

	private StringBuilder sb = new StringBuilder();

	private StandardDocument document;

	private StandardElement element;

	private Locator documentLocator = null;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		if (element != null) {
			throw new SAXException("document has open elements");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		element.setText(sb.toString());
		sb.setLength(0);
		element = element.getParent();
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		super.error(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		super.fatalError(e);
	}

	public StandardDocument getDocument() {
		return document;
	}

	public Locator getDocumentLocator() {
		return documentLocator;
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.documentLocator = locator;
	}

	@Override
	public void startDocument() throws SAXException {
		document = new StandardDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		StandardElement parent = element;
		if (parent == null) {
			element = new StandardElement(document, parent, qName);
			document.setRootElement(element);
		} else {
			element = (StandardElement) parent.newElement(qName);
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			element.setAttributeTemplate(attributes.getLocalName(i),
					attributes.getValue(i));
		}
		sb.setLength(0);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		super.warning(e);
	}

}
