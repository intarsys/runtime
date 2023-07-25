package de.intarsys.tools.infoset;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.Test;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestElementTools {

	@Test
	public void testGetPathElement() throws IOException {
		IDocument iDoc;
		IElement iRoot;
		IElement iElement;
		InputStream is;
		//
		is = getClass().getResourceAsStream("doc_element_empty.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = ElementTools.getPathElement(iRoot, "doedel");
		assertTrue(iElement.getName().equals("doedel"));
		iElement = ElementTools.getPathElement(iRoot, "doedel.diedel");
		assertTrue(iElement == null);
		iElement = ElementTools.getPathElement(iRoot, "doedel.diedel.schneder");
		assertTrue(iElement == null);
		//
		is = getClass().getResourceAsStream("doc_element_child_one.xml");
		iDoc = ElementFactory.get().parse(is);
		iRoot = iDoc.getRootElement();
		iElement = ElementTools.getPathElement(iRoot, "doedel");
		assertTrue(iElement.getName().equals("doedel"));
		iElement = ElementTools.getPathElement(iRoot, "doedel.child");
		assertTrue(iElement.getName().equals("child"));
		iElement = ElementTools.getPathElement(iRoot, "doedel.child.schneder");
		assertTrue(iElement == null);
	}

	@Test
	// @formatter:off
	public void testToElement() {
		IArgs args;
		IArgs listArgs;
		IArgs nestedArgs;
		IElement element;
		IElement child;
		Iterator<IElement> nodes;
		//
		args = Args.create();
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == false);
		assertTrue(element.hasElements() == false);
		// this is an unused corner case
		args = Args.create();
		args.put(0, "test");
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == false);
		assertTrue(element.hasElements() == false);
		//
		// {
		// a -> a.a
		// }
		args = Args.create();
		args.put("a", "a.a");
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == true);
		assertTrue("a.a".equals(element.attributeValue("a", null)));
		assertTrue(element.hasElements() == false);
		//
		// {
		// a -> a.a
		// b -> {
		// }
		// }
		args = Args.create();
		args.put("a", "a.a");
		nestedArgs = Args.create();
		args.put("b", nestedArgs);
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == true);
		assertTrue("a.a".equals(element.attributeValue("a", null)));
		assertTrue(element.hasElements() == false);
		//
		// {
		// a -> a.a
		// b -> {
		// b -> b.a
		// }
		// c -> {
		// c -> c.a
		// }
		// }
		args = Args.create();
		args.put("a", "a.a");
		nestedArgs = Args.create();
		nestedArgs.put("b", "b.a");
		args.put("b", nestedArgs);
		nestedArgs = Args.create();
		nestedArgs.put("c", "c.a");
		args.put("c", nestedArgs);
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == true);
		assertTrue("a.a".equals(element.attributeValue("a", null)));
		assertTrue(element.hasElements() == true);
		nodes = element.elementIterator("b");
		child = nodes.next();
		assertTrue(child.hasAttributes() == true);
		assertTrue("b.a".equals(child.attributeValue("b", null)));
		assertTrue(child.hasElements() == false);
		nodes = element.elementIterator("c");
		child = nodes.next();
		assertTrue(child.hasAttributes() == true);
		assertTrue("c.a".equals(child.attributeValue("c", null)));
		assertTrue(child.hasElements() == false);
		// {
		// a -> a.a
		// x -> {
		// {
		// b -> b.a
		// },
		// {
		// c -> c.a
		// }
		// }
		// }
		args = Args.create();
		args.put("a", "a.a");
		listArgs = Args.create();
		nestedArgs = Args.create();
		nestedArgs.put("b", "b.a");
		listArgs.add(nestedArgs);
		nestedArgs = Args.create();
		nestedArgs.put("c", "c.a");
		listArgs.add(nestedArgs);
		args.put("x", listArgs);
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == true);
		assertTrue("a.a".equals(element.attributeValue("a", null)));
		assertTrue(element.hasElements() == true);
		nodes = element.elementIterator("x");
		child = nodes.next();
		assertTrue(child.hasAttributes() == true);
		assertTrue("b.a".equals(child.attributeValue("b", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.hasAttributes() == true);
		assertTrue("c.a".equals(child.attributeValue("c", null)));
		assertTrue(child.hasElements() == false);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.b.c.x", "1");
		ArgTools.putPath(args, "a.b.c.y", "2");
		ArgTools.putPath(args, "a.b.d.x", "3");
		ArgTools.putPath(args, "a.b.e.x", "4");
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == false);
		assertTrue(element.hasElements() == true);
		child = element.element("a").element("b");
		nodes = child.elementIterator();
		child = nodes.next();
		assertTrue(child.getName().equals("c"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("1".equals(child.attributeValue("x", null)));
		assertTrue("2".equals(child.attributeValue("y", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("d"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("3".equals(child.attributeValue("x", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("e"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("4".equals(child.attributeValue("x", null)));
		assertTrue(child.hasElements() == false);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.b.0.c.x", "1");
		ArgTools.putPath(args, "a.b.0.c.y", "2");
		ArgTools.putPath(args, "a.b.0.d.x", "3");
		ArgTools.putPath(args, "a.b.0.e.x", "4");
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == false);
		assertTrue(element.hasElements() == true);
		child = element.element("a").element("b");
		nodes = child.elementIterator();
		child = nodes.next();
		assertTrue(child.getName().equals("c"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("1".equals(child.attributeValue("x", null)));
		assertTrue("2".equals(child.attributeValue("y", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("d"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("3".equals(child.attributeValue("x", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("e"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("4".equals(child.attributeValue("x", null)));
		assertTrue(child.hasElements() == false);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.device.0.x", "1");
		ArgTools.putPath(args, "a.device.0.y", "2");
		ArgTools.putPath(args, "a.device.1.x", "3");
		ArgTools.putPath(args, "a.device.1.y", "4");
		ArgTools.putPath(args, "a.device.2.x", "5");
		ArgTools.putPath(args, "a.device.2.y", "6");
		element = ElementTools.toElement(args);
		assertTrue(element.hasAttributes() == false);
		assertTrue(element.hasElements() == true);
		child = element.element("a");
		nodes = child.elementIterator("device");
		child = nodes.next();
		assertTrue(child.getName().equals("device"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("1".equals(child.attributeValue("x", null)));
		assertTrue("2".equals(child.attributeValue("y", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("device"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("3".equals(child.attributeValue("x", null)));
		assertTrue("4".equals(child.attributeValue("y", null)));
		assertTrue(child.hasElements() == false);
		child = nodes.next();
		assertTrue(child.getName().equals("device"));
		assertTrue(child.hasAttributes() == true);
		assertTrue("5".equals(child.attributeValue("x", null)));
		assertTrue("6".equals(child.attributeValue("y", null)));
		assertTrue(child.hasElements() == false);
	}
}
