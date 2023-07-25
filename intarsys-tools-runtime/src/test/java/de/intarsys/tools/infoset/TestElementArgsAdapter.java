package de.intarsys.tools.infoset;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import junit.framework.TestCase;

public class TestElementArgsAdapter extends TestCase {

	public void testAdapter() {
		String source;
		IElement element;
		IElement childElement;
		IArgs args;
		IArgs childArgs;
		//
		element = ElementFactory.get().createElement("root");
		args = new ElementArgsAdapter(element);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("foo") == null);
		//
		element = ElementFactory.get().createElement("root");
		element.setAttributeValue("foo", "bar");
		args = new ElementArgsAdapter(element);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("foo").equals("bar"));
		//
		element = ElementFactory.get().createElement("root");
		childElement = element.newElement("foo");
		childElement.setAttributeValue("gnu", "gnat");
		args = new ElementArgsAdapter(element);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("foo") instanceof IArgs);
		assertTrue(ArgTools.getString(args, "foo.0.gnu", null).equals("gnat"));
		assertTrue(ArgTools.getString(args, "foo.gnu", null).equals("gnat"));
		//
		element = ElementFactory.get().createElement("root");
		childElement = element.newElement("foo");
		childElement.setAttributeValue("gnu", "gnat");
		childElement = element.newElement("foo");
		childElement.setAttributeValue("gnu", "gnarf");
		childElement = element.newElement("bar");
		childElement = childElement.newElement("diedel");
		childElement.setAttributeValue("x", "y");
		args = new ElementArgsAdapter(element);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("foo") instanceof IArgs);
		assertTrue(ArgTools.getString(args, "foo.0.gnu", null).equals("gnat"));
		assertTrue(ArgTools.getString(args, "foo.1.gnu", null).equals("gnarf"));
		assertTrue(ArgTools.getString(args, "foo.gnu", null).equals("gnat"));
		assertTrue(ArgTools.getString(args, "bar.diedel.x", null).equals("y"));
		assertTrue(ArgTools.getString(args, "poo", null) == null);
		assertTrue(ArgTools.getString(args, "foo.poo", null) == null);
		assertTrue(ArgTools.getString(args, "foo.0.poo", null) == null);
	}
}
