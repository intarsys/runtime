package de.intarsys.tools.infoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class StandardElementFactory extends CommonElementFactory {

	static private SAXParserFactory spf;

	static {
		spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(false);
		spf.setValidating(false);
	}

	@Override
	public IDocument createDocument() {
		return new StandardDocument();
	}

	@Override
	public IElement createElement(String name) {
		StandardElement result = new StandardElement(null, null, name);
		return result;
	}

	@Override
	public IDocument parse(InputStream is) throws IOException {
		try {
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			StandardContentHandler handler = new StandardContentHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(is));
			return handler.getDocument();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	@Override
	public IDocument parse(Reader r) throws IOException {
		try {
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			StandardContentHandler handler = new StandardContentHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(r));
			return handler.getDocument();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

}
