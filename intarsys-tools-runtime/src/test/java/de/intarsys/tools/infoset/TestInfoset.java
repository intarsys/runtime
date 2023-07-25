package de.intarsys.tools.infoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.MapResolver;
import junit.framework.TestCase;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestInfoset extends TestCase {

	protected void check(String filename) throws IOException {
		IDocument iDoc;
		IDocument iDocClone;
		String asXml;
		StringReader r;
		InputStream is;
		//
		is = getClass().getResourceAsStream(filename);
		iDoc = ElementFactory.get().parse(is);
		asXml = iDoc.asXML();
		r = new StringReader(asXml);
		iDocClone = ElementFactory.get().parse(r);
		assertTrue(DocumentTools.equals(iDoc, iDocClone));
	}

	public void testAttributeRead() throws Exception {
		IDocument iDoc;
		IElement iRoot;
		IElement iElement;
		String attrName;
		IAttribute iAttr;
		String value;
		InputStream is;
		Iterator<String> itAttrs;
		int count;
		//
		is = getClass().getResourceAsStream("doc_attribute_child_none.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasAttributes());
		assertTrue(!iElement.hasAttribute("gnu"));
		itAttrs = iElement.attributeNames();
		count = 0;
		while (itAttrs.hasNext()) {
			attrName = itAttrs.next();
			count++;
		}
		assertTrue(count == 0);
		try {
			itAttrs.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}

		iAttr = iElement.attribute("gnu");
		assertTrue(iAttr == null);
		value = iElement.attributeValue("gnu", null);
		assertTrue(value == null);
		value = iElement.attributeValue("gnu", "diedel");
		assertTrue(value.equals("diedel"));

		//
		is = getClass().getResourceAsStream("doc_attribute_child_one.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasAttribute("ding"));
		assertTrue(iElement.hasAttributes());
		assertTrue(iElement.hasAttribute("gnu"));
		itAttrs = iElement.attributeNames();
		count = 0;
		while (itAttrs.hasNext()) {
			attrName = itAttrs.next();
			iAttr = iElement.attribute(attrName);
			assertTrue(iAttr != null);
			if (iAttr.getName().equals("gnu")) {
				assertTrue(iAttr.getValue().equals("gnat"));
			} else {
				fail();
			}
			count++;
		}
		assertTrue(count == 1);
		try {
			itAttrs.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}

		iAttr = iElement.attribute("gnu");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getName().equals("gnu"));
		assertTrue(iAttr.getValue().equals("gnat"));
		value = iElement.attributeValue("gnu", null);
		assertTrue(value.equals("gnat"));
		value = iElement.attributeValue("gnu", "diedel");
		assertTrue(value.equals("gnat"));
		//
		is = getClass().getResourceAsStream("doc_attribute_child_multi.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasAttribute("ding"));
		assertTrue(iElement.hasAttributes());
		assertTrue(iElement.hasAttribute("gnu"));
		itAttrs = iElement.attributeNames();

		count = 0;
		while (itAttrs.hasNext()) {
			attrName = itAttrs.next();
			iAttr = iElement.attribute(attrName);
			assertTrue(iAttr != null);
			if (iAttr.getName().equals("gnu")) {
				assertTrue(iAttr.getValue().equals("gnat"));
			} else if (iAttr.getName().equals("foo")) {
				assertTrue(iAttr.getValue().equals("bar"));
			} else if (iAttr.getName().equals("schneder")) {
				assertTrue(iAttr.getValue().equals("pelz"));
			} else {
				fail();
			}
			count++;
		}
		assertTrue(count == 3);
		try {
			itAttrs.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}

		iAttr = iElement.attribute("gnu");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getName().equals("gnu"));
		assertTrue(iAttr.getValue().equals("gnat"));
		value = iElement.attributeValue("gnu", null);
		assertTrue(value.equals("gnat"));
		value = iElement.attributeValue("gnu", "diedel");
		assertTrue(value.equals("gnat"));
		iAttr = iElement.attribute("foo");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getName().equals("foo"));
		assertTrue(iAttr.getValue().equals("bar"));
		value = iElement.attributeValue("foo", null);
		assertTrue(value.equals("bar"));
		value = iElement.attributeValue("foo", "diedel");
		assertTrue(value.equals("bar"));
		iAttr = iElement.attribute("schneder");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getName().equals("schneder"));
		assertTrue(iAttr.getValue().equals("pelz"));
		value = iElement.attributeValue("schneder", null);
		assertTrue(value.equals("pelz"));
		value = iElement.attributeValue("schneder", "diedel");
		assertTrue(value.equals("pelz"));
	}

	public void testElementIO() throws Exception {
		check("doc_attribute_child_multi.xml");
		check("doc_attribute_child_none.xml");
		check("doc_attribute_child_one.xml");
		check("doc_element_child_multi.xml");
		check("doc_element_child_none.xml");
		check("doc_element_child_one.xml");
		check("doc_element_empty.xml");
		check("doc_element_linefeed.xml");
		check("doc_element_whitespace.xml");
		check("doc_root_empty.xml");
		check("doc_root_linefeed.xml");
		check("doc_root_whitespace.xml");
		check("doc_expand.xml");
	}

	public void testElementRead() throws Exception {
		IDocument iDoc;
		IElement iRoot;
		IElement iElement;
		IElement iChild;
		InputStream is;
		Iterator<IElement> itElements;
		//
		is = getClass().getResourceAsStream("doc_root_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		assertTrue(iRoot.getName().equals("diedel"));
		assertTrue(iRoot.getText().equals(""));
		//
		is = getClass().getResourceAsStream("doc_root_linefeed.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		assertTrue(iRoot.getName().equals("diedel"));
		assertTrue(iRoot.getText().equals("\n"));
		//
		is = getClass().getResourceAsStream("doc_root_whitespace.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		assertTrue(iRoot.getName().equals("diedel"));
		assertTrue(iRoot.getText().equals(" \n "));
		//
		is = getClass().getResourceAsStream("doc_element_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(iElement.getName().equals("doedel"));
		assertTrue(iRoot.elementText("doedel").equals(""));
		assertTrue(iElement.getText().equals(""));
		//
		is = getClass().getResourceAsStream("doc_element_linefeed.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(iElement.getName().equals("doedel"));
		assertTrue(iRoot.elementText("doedel").equals("\n"));
		assertTrue(iElement.getText().equals("\n"));
		//
		is = getClass().getResourceAsStream("doc_element_whitespace.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(iElement.getName().equals("doedel"));
		assertTrue(iRoot.elementText("doedel").equals(" \n "));
		assertTrue(iElement.getText().equals(" \n "));
		//
		is = getClass().getResourceAsStream("doc_element_child_none.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasElements());
		assertTrue(!iElement.hasElements("child"));
		itElements = iElement.elementIterator();
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		itElements = iElement.elementIterator("child");
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		iChild = iElement.element("child");
		assertTrue(iChild == null);
		assertTrue(iElement.elementText("child") == null);
		//
		is = getClass().getResourceAsStream("doc_element_child_one.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(iElement.hasElements());
		assertTrue(iElement.hasElements("child"));
		itElements = iElement.elementIterator();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		itElements = iElement.elementIterator("child");
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		iChild = iElement.element("child");
		assertTrue(iChild != null);
		assertTrue(iElement.elementText("child").trim().equals("0"));
		//
		is = getClass().getResourceAsStream("doc_element_child_multi.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(iElement.hasElements());
		assertTrue(iElement.hasElements("child"));
		itElements = iElement.elementIterator();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		itElements = iElement.elementIterator("child");
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(itElements.hasNext());
		itElements.next();
		assertTrue(!itElements.hasNext());
		try {
			itElements.next();
			fail("end of iterator");
		} catch (Exception e) {
			// expected
		}
		iChild = iElement.element("child");
		assertTrue(iChild != null);
		assertTrue(iElement.elementText("child").trim().equals("0"));
		assertTrue(iChild.attributeValue("index", null).equals("0"));
	}

	public void testElementWrite() throws Exception {
		IDocument iDoc;
		IElement iRoot;
		IElement iElement;
		IElement iChild;
		IAttribute iAttr;
		InputStream is;
		Iterator<IElement> itElements;
		//
		is = getClass().getResourceAsStream("doc_root_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = ElementFactory.get().createElement("newroot");
		iDoc.setRootElement(iElement);
		iRoot = iDoc.getRootElement();
		assertTrue(iRoot.getName().equals("newroot"));
		assertTrue(iRoot.getText().equals(""));
		//
		is = getClass().getResourceAsStream("doc_element_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		iElement.setName("newname");
		iElement.setText("newtext");
		assertTrue(iElement.getName().equals("newname"));
		assertTrue(iRoot.elementText("newname").equals("newtext"));
		assertTrue(iElement.getText().equals("newtext"));
		//
		is = getClass().getResourceAsStream("doc_element_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasElements());
		iChild = iElement.newElement("child");
		assertTrue(iChild != null);
		assertTrue(iChild.getName().equals("child"));
		assertTrue(!iChild.hasAttributes());
		assertTrue(!iChild.hasElements());
		assertTrue(iChild.getText().equals(""));
		assertTrue(iElement.hasElements());
		assertTrue(iElement.hasElements("child"));
		iChild = iElement.element("child");
		assertTrue(iChild != null);
		assertTrue(iChild.getName().equals("child"));
		assertTrue(!iChild.hasAttributes());
		assertTrue(!iChild.hasElements());
		assertTrue(iChild.getText().equals(""));
		//
		is = getClass().getResourceAsStream("doc_element_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = iRoot.element("doedel");
		assertTrue(!iElement.hasAttributes());
		iElement.setAttributeValue("a", "b");
		assertTrue(iElement.hasAttributes());
		iAttr = iElement.attribute("a");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getName().equals("a"));
		assertTrue(iAttr.getValue().equals("b"));
		assertTrue(iElement.attributeValue("a", null).equals("b"));
		assertTrue(iElement.attributeValue("a", "x").equals("b"));
		assertTrue(iElement.attributeNames().hasNext());
		iElement.setAttributeValue("a", null);
		assertTrue(!iElement.hasAttributes());
		iAttr = iElement.attribute("a");
		assertTrue(iAttr == null);
		assertTrue(iElement.attributeValue("a", null) == null);
		assertTrue(iElement.attributeValue("a", "x").equals("x"));
		assertTrue(!iElement.attributeNames().hasNext());
	}

	public void testExpand() throws Exception {
		IDocument iDoc;
		IElement iRoot;
		IElement iElement;
		IAttribute iAttr;
		String value;
		InputStream is;
		Iterator<IAttribute> itAttrs;
		int count;
		MapResolver resolver;
		//
		resolver = new MapResolver();
		resolver.put("a", "1");
		resolver.put("b", "2");
		resolver.put("c", "3");
		resolver.put("d", "4");
		is = getClass().getResourceAsStream("doc_expand.xml");
		iDoc = ElementFactory.get().parse(is);
		((IStringEvaluatorAccess) iDoc).setStringEvaluator(resolver);
		iRoot = iDoc.getRootElement();
		// read
		iElement = iRoot.element("doedel");
		iAttr = iElement.attribute("a");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getValue().equals("1"));
		value = iElement.attributeValue("a", null);
		assertTrue(value.equals("1"));
		iAttr = iElement.attribute("b");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getValue().equals("${b}"));
		value = iElement.attributeValue("b", null);
		assertTrue(value.equals("${b}"));
		iAttr = iElement.attribute("c");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getValue().equals(" 3 "));
		value = iElement.attributeValue("c", null);
		assertTrue(value.equals(" 3 "));
		// write
		iElement.setAttributeValue("d", "2");
		iAttr = iElement.attribute("d");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getValue().equals("2"));
		value = iElement.attributeValue("d", null);
		assertTrue(value.equals("2"));
		// write template
		iElement.setAttributeValue("d", "${d}");
		iAttr = iElement.attribute("d");
		assertTrue(iAttr != null);
		assertTrue(iAttr.getValue().equals("${d}"));
		value = iElement.attributeValue("d", null);
		assertTrue(value.equals("${d}"));
	}
}
