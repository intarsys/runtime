package de.intarsys.tools.dom;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TestNamespace {

	@Test
	public void testAddElementNested() throws IOException, SAXException {
		InputStream is = getClass().getResourceAsStream("doc.xml");
		Document document = DOMTools.parseDocument(is, true);
		Element element1 = DOMTools.createElement(document, "http://asdasd", "s", "element1");
		document.getDocumentElement().appendChild(element1);
		Element element2 = DOMTools.createChild(element1, "http://asdasd", "x", "element2");
		//
		String serialized = DOMTools.toString(document);
		assertThat(element1.getPrefix(), Matchers.is("s"));
		assertThat(element2.getPrefix(), Matchers.is("s"));
	}

	@Test
	public void testAddElementSiblings() throws IOException, SAXException {
		InputStream is = getClass().getResourceAsStream("doc.xml");
		Document document = DOMTools.parseDocument(is, true);
		Element element1 = DOMTools.createElement(document, "http://asdasd", "x", "element1");
		document.getDocumentElement().appendChild(element1);
		Element element2 = DOMTools.createElement(document, "http://asdasd", "y", "element2");
		document.getDocumentElement().appendChild(element2);
		//
		String serialized = DOMTools.toString(document);
		assertThat(element1.getPrefix(), Matchers.is("x"));
		assertThat(element2.getPrefix(), Matchers.is("y"));
	}
}
