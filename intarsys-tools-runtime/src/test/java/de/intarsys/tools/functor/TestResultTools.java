package de.intarsys.tools.functor;

import java.util.Map;

import junit.framework.TestCase;

public class TestResultTools extends TestCase {

	public void test() {
		IArgs args;
		//
		args = Args.create();
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertFalse(ResultTools.isPropertyReturn(args, "foo", false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", "return=true");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.isPropertyReturn(args, "foo", false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", "return=true;");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.isPropertyReturn(args, "foo", false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", " return = true ; ");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.isPropertyReturn(args, "foo", false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo.return", "true");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.isPropertyReturn(args, "foo", false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", " return = true ; style=literal;");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.getPropertyStyle(args, "foo", EnumStyle.REFERENCE) == EnumStyle.LITERAL);
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", " return = true ; style=reference;");
		assertTrue(ResultTools.isPropertyReturn(args, "foo", true));
		assertTrue(ResultTools.getPropertyStyle(args, "foo", EnumStyle.LITERAL) == EnumStyle.REFERENCE);
	}

	public void testPath() {
		IArgs args;
		Map<String, IArgs> descriptors;
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", "");
		ArgTools.putPath(args, "result.property.bar", "");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar"), false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo", "return=true");
		ArgTools.putPath(args, "result.property.bar", "return=false");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar"), true));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo.return", "true");
		ArgTools.putPath(args, "result.property.bar.return", "false");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar"), true));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo.diedel", "");
		ArgTools.putPath(args, "result.property.bar.diedel", "");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo.diedel"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar.diedel"), false));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo.diedel", "return=true");
		ArgTools.putPath(args, "result.property.bar.diedel", "return=false");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo.diedel"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar.diedel"), true));
		//
		args = Args.create();
		ArgTools.putPath(args, "result.property.foo.diedel.return", "true");
		ArgTools.putPath(args, "result.property.bar.diedel.return", "false");
		descriptors = ResultTools.getPropertyDescriptors(args);
		assertTrue(descriptors.size() == 2);
		assertTrue(ResultTools.isPropertyReturn(descriptors.get("foo.diedel"), true));
		assertTrue(!ResultTools.isPropertyReturn(descriptors.get("bar.diedel"), true));
	}
}
