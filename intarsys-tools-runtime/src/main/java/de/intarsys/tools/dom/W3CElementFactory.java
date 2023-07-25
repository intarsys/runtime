package de.intarsys.tools.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.intarsys.tools.infoset.CommonElementFactory;
import de.intarsys.tools.infoset.IDocument;
import de.intarsys.tools.infoset.IElement;

public class W3CElementFactory extends CommonElementFactory {

	@Override
	public IDocument createDocument() {
		Document document = DOMTools.createDocument();
		return new DocumentDocumentAdapter(document);
	}

	@Override
	public IElement createElement(String name) {
		Document document = DOMTools.createDocument();
		Element element = document.createElement(name);
		return new ElementElementAdapter(element);
	}

	@Override
	public IDocument parse(InputStream is) throws IOException {
		Document document;
		try {
			document = DOMTools.parseDocument(is, false);
		} catch (SAXException e) {
			throw new IOException(e);
		}
		return new DocumentDocumentAdapter(document);
	}

	@Override
	public IDocument parse(Reader r) throws IOException {
		Document document;
		try {
			document = DOMTools.parseDocument(r);
		} catch (SAXException e) {
			throw new IOException(e);
		}
		return new DocumentDocumentAdapter(document);
	}

}
