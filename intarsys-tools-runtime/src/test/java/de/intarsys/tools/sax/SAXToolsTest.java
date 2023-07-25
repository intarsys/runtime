package de.intarsys.tools.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXToolsTest {
	@Test
	public void prohibitsExternalEntities() {
		SAXParseException exception = assertThrows(SAXParseException.class, () -> parse("""
			<!DOCTYPE example [
				<!ELEMENT root ANY>
				<!ENTITY internalEntity "some value">
				<!ENTITY externalEntity SYSTEM "file:///etc/passwd">
			]>
			<root>
			    &internalEntity;
			    &externalEntity;
			</root>
			"""));
		assertEquals("expected error when external entity is dereferenced", 8, exception.getLineNumber());
	}

	@Test
	public void prohibitsExternalParameterEntities() {
		SAXParseException exception = assertThrows(SAXParseException.class, () -> parse("""
			<!DOCTYPE example [
				<!ENTITY % externalParameterEntity SYSTEM "file:///etc/passwd">
				%externalParameterEntity;
			]>
			<root/>
			"""));
		assertEquals("expected error when external entity is dereferenced", 3, exception.getLineNumber());
	}

	@Test
	public void prohibitsExternalDtd() {
		SAXParseException exception = assertThrows(SAXParseException.class, () -> parse("""
			<!DOCTYPE example SYSTEM "file:///etc/passwd">
			<root/>
			"""));
		assertEquals("expected error in DOCTYPE declaration", 1, exception.getLineNumber());
	}

	private void parse(String xml) throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = SAXTools.createSecureParserFactory().newSAXParser();
		InputSource input = new InputSource(new StringReader(xml));
		parser.parse(input, new DefaultHandler());
	}
}
