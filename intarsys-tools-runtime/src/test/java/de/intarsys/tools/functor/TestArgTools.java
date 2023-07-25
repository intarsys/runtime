package de.intarsys.tools.functor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import junit.framework.TestCase;

public class TestArgTools extends TestCase {

	public TestArgTools() {
		super();
	}

	public TestArgTools(String name) {
		super(name);
	}

	public void testExpandDeepArgs() {
		IArgs args;
		MapResolver resolver;
		IStringEvaluator evaluator;
		//
		resolver = new MapResolver();
		resolver.put("x", "y");
		evaluator = TaggedStringEvaluator.decorate(resolver);
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.expandDeep(args, evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.putPath(args, "gnu", "x${x}x");
		ArgTools.expandDeep(args, evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		assertTrue(ArgTools.getString(args, "gnu", null).equals("xyx"));
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.putPath(args, "gnu", "x${x}x");
		ArgTools.putPath(args, "a.b.c", "${x}${x}");
		ArgTools.expandDeep(args, evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		assertTrue(ArgTools.getString(args, "gnu", null).equals("xyx"));
		assertTrue(ArgTools.getString(args, "a.b.c", null).equals("yy"));
	}

	public void testExpandDeepBinding() {
		IArgs args;
		MapResolver resolver;
		IStringEvaluator evaluator;
		//
		resolver = new MapResolver();
		resolver.put("x", "y");
		evaluator = TaggedStringEvaluator.decorate(resolver);
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.expandDeep(args, "gnu", evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.putPath(args, "gnu", "x${x}x");
		ArgTools.putPath(args, "gnat", "x${x}x");
		ArgTools.expandDeep(args, "gnu", evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		assertTrue(ArgTools.getString(args, "gnu", null).equals("xyx"));
		assertTrue(ArgTools.getString(args, "gnat", null).equals("x${x}x"));
		//
		args = Args.create();
		ArgTools.putPath(args, "foo", "bar");
		ArgTools.putPath(args, "gnu", "x${x}x");
		ArgTools.putPath(args, "gnat.a.b.c", "x${x}x");
		ArgTools.expandDeep(args, "gnu", evaluator);
		assertTrue(ArgTools.getString(args, "foo", null).equals("bar"));
		assertTrue(ArgTools.getString(args, "gnu", null).equals("xyx"));
		assertTrue(ArgTools.getString(args, "gnat.a.b.c", null).equals("x${x}x"));
	}

	public void testIsDefined() {
		IArgs args;
		//
		args = Args.create();
		assertTrue(!ArgTools.isDefined(args, "a"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "A");
		assertTrue(ArgTools.isDefined(args, "a"));
		assertTrue(!ArgTools.isDefined(args, "b"));
		//
		args = Args.create();
		ArgTools.putPath(args, "foo.bar", "A");
		assertTrue(!ArgTools.isDefined(args, "a"));
		assertTrue(ArgTools.isDefined(args, "foo"));
		assertTrue(!ArgTools.isDefined(args, "foo.a"));
		assertTrue(ArgTools.isDefined(args, "foo.bar"));
	}

	public void testPutAllArgs() {
		IArgs argsA;
		IArgs argsB;
		//
		argsA = new Args();
		argsB = new Args();
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 0);
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "gnu", "boo");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("boo", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo", "bar");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar", ArgTools.getPath(argsA, "foo"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "diedel.gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals(null, ArgTools.getPath(argsA, "diedel.gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "oops");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.gnu", "gnat");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals(null, ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("gnat", ArgTools.getPath(argsA, "foo.0.gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong0");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putPath(argsB, "foo.2.ding", "dong2");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals(null, ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals("dong2", ArgTools.getPath(argsA, "foo.2.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "boo");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong0");
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putAll(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
	}

	public void testPutAllDeepArgs() {
		IArgs argsA;
		IArgs argsB;
		//
		argsA = new Args();
		argsB = new Args();
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 0);
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "gnu", "boo");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("boo", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo", "bar");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar", ArgTools.getPath(argsA, "foo"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "diedel.gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "diedel.gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "oops");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.gnu", "gnat");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("gnat", ArgTools.getPath(argsA, "foo.0.gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong0");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putPath(argsB, "foo.2.ding", "dong2");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals("dong2", ArgTools.getPath(argsA, "foo.2.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "boo");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong0");
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		//
		argsA = new Args();
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.+.ding", "dong0");
		ArgTools.putAllDeep(argsA, argsB);
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.+.ding", "dong1");
		ArgTools.putAllDeep(argsA, argsB);
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.+.ding", "dong2");
		ArgTools.putAllDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals("dong2", ArgTools.getPath(argsA, "foo.2.ding"));
	}

	public void testPutAllDeepMap() {
		IArgs args;
		IArgs mapArgs;
		Map map;
		//
		args = new Args();
		ArgTools.putPath(args, "a", "x");
		ArgTools.putPath(args, "foo.bar.gnu", "gnat");
		map = new HashMap<>();
		ArgTools.putAllDeep(args, map);

		assertEquals("x", ArgTools.getPath(args, "a"));
		assertTrue(null == ArgTools.getPath(args, "x"));
		assertEquals("gnat", ArgTools.getPath(args, "foo.bar.gnu"));
		//
		args = new Args();
		ArgTools.putPath(args, "a", "x");
		ArgTools.putPath(args, "foo.bar.gnu", "gnat");
		mapArgs = new Args();
		ArgTools.putPath(mapArgs, "x", "y");
		map = ArgTools.toMapDeep(mapArgs);
		ArgTools.putAllDeep(args, map);

		assertEquals("x", ArgTools.getPath(args, "a"));
		assertEquals("y", ArgTools.getPath(args, "x"));
		assertEquals("gnat", ArgTools.getPath(args, "foo.bar.gnu"));
		//
		args = new Args();
		ArgTools.putPath(args, "a", "x");
		ArgTools.putPath(args, "foo.bar.gnu", "gnat");
		mapArgs = new Args();
		ArgTools.putPath(mapArgs, "x.y.z", "1");
		ArgTools.putPath(mapArgs, "foo.bar.bling", "blang");
		map = ArgTools.toMapDeep(mapArgs);
		ArgTools.putAllDeep(args, map);

		assertEquals("x", ArgTools.getPath(args, "a"));
		assertTrue(ArgTools.getPath(args, "x") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "x.y") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "foo") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "foo.bar") instanceof IArgs);
		assertEquals("1", ArgTools.getPath(args, "x.y.z"));
		assertEquals("gnat", ArgTools.getPath(args, "foo.bar.gnu"));
		assertEquals("blang", ArgTools.getPath(args, "foo.bar.bling"));
		//
		args = new Args();
		ArgTools.putPath(args, "a", "x");
		ArgTools.putPath(args, "foo.bar.gnu", "gnat");
		mapArgs = new Args();
		ArgTools.putPath(mapArgs, "x.y.z", Arrays.asList("foo", "bar"));
		map = ArgTools.toMapDeep(mapArgs);
		ArgTools.putAllDeep(args, map);

		assertEquals("x", ArgTools.getPath(args, "a"));
		assertTrue(ArgTools.getPath(args, "x") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "x.y") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "x.y.z") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "foo") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "foo.bar") instanceof IArgs);
		assertEquals("foo", ArgTools.getPath(args, "x.y.z.0"));
		assertEquals("bar", ArgTools.getPath(args, "x.y.z.1"));
		assertEquals("gnat", ArgTools.getPath(args, "foo.bar.gnu"));
	}

	public void testPutAllIfAbsentArgs() {
		IArgs argsA;
		IArgs argsB;
		//
		argsA = new Args();
		argsB = new Args();
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 0);
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "gnu", "boo");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo", "bar");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar", ArgTools.getPath(argsA, "foo"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "diedel.gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "diedel.gnu"));
		assertEquals(null, ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals(null, ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "oops");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("oops", ArgTools.getPath(argsA, "foo.0.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.gnu", "gnat");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals(null, ArgTools.getPath(argsA, "foo.0.gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong0");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putPath(argsB, "foo.2.ding", "dong2");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals(null, ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals(null, ArgTools.getPath(argsA, "foo.2.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "boo");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong0");
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putAllIfAbsent(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("boo", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals(null, ArgTools.getPath(argsA, "foo.1.ding"));
	}

	public void testPutAllIfAbsentDeepArgs() {
		IArgs argsA;
		IArgs argsB;
		//
		argsA = new Args();
		argsB = new Args();
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 0);
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "gnu", "boo");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo", "bar");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar", ArgTools.getPath(argsA, "foo"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "diedel.gnu", "gnat");
		argsB = new Args();
		ArgTools.putPath(argsB, "diedel.foo1", "bar1");
		ArgTools.putPath(argsB, "diedel.foo2", "bar2");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "diedel.gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "oops");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("oops", ArgTools.getPath(argsA, "foo.0.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.gnu", "gnat");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("gnat", ArgTools.getPath(argsA, "foo.0.gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong0");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putPath(argsB, "foo.2.ding", "dong2");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals("dong2", ArgTools.getPath(argsA, "foo.2.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "boo");
		argsB = new Args();
		ArgTools.putPath(argsB, "foo.0.ding", "dong0");
		ArgTools.putPath(argsB, "foo.1.ding", "dong1");
		ArgTools.putAllIfAbsentDeep(argsA, argsB);
		assertTrue(argsA.size() == 1);
		assertEquals("boo", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
	}

	public void testPutAllIfAbsentMap() {
		IArgs argsA;
		Map mapB;
		//
		argsA = new Args();
		mapB = new HashMap();
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 0);
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		mapB = new HashMap();
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		mapB = new HashMap();
		mapB.put("gnu", "boo");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		mapB = new HashMap();
		mapB.put("foo", "bar");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar", ArgTools.getPath(argsA, "foo"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "gnu", "gnat");
		mapB = new HashMap();
		mapB.put("diedel.foo1", "bar1");
		mapB.put("diedel.foo2", "bar2");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 2);
		assertEquals("gnat", ArgTools.getPath(argsA, "gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "diedel.gnu", "gnat");
		mapB = new HashMap();
		mapB.put("diedel.foo1", "bar1");
		mapB.put("diedel.foo2", "bar2");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("gnat", ArgTools.getPath(argsA, "diedel.gnu"));
		assertEquals("bar1", ArgTools.getPath(argsA, "diedel.foo1"));
		assertEquals("bar2", ArgTools.getPath(argsA, "diedel.foo2"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "oops");
		mapB = new HashMap();
		mapB.put("foo.0.ding", "dong");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("oops", ArgTools.getPath(argsA, "foo.0.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong");
		mapB = new HashMap();
		mapB.put("foo.0.gnu", "gnat");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("gnat", ArgTools.getPath(argsA, "foo.0.gnu"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "dong0");
		mapB = new HashMap();
		mapB.put("foo.1.ding", "dong1");
		mapB.put("foo.2.ding", "dong2");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("dong0", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
		assertEquals("dong2", ArgTools.getPath(argsA, "foo.2.ding"));
		//
		argsA = new Args();
		ArgTools.putPath(argsA, "foo.0.ding", "boo");
		mapB = new HashMap();
		mapB.put("foo.0.ding", "dong0");
		mapB.put("foo.1.ding", "dong1");
		ArgTools.putAllIfAbsent(argsA, mapB);
		assertTrue(argsA.size() == 1);
		assertEquals("boo", ArgTools.getPath(argsA, "foo.0.ding"));
		assertEquals("dong1", ArgTools.getPath(argsA, "foo.1.ding"));
	}

	public void testPutDefinition() {
		IArgs args;
		//
		args = Args.create();
		assertTrue(ArgTools.getPath(args, "a") == null);
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a=A");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a").equals("A"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.b=A.B");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b").equals("A.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.b1=A.B1");
		ArgTools.putDefinition(args, "a.b2=A.B2");
		ArgTools.putDefinition(args, "a.b3=A.B3");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b1").equals("A.B1"));
		assertTrue(ArgTools.getPath(args, "a.b2").equals("A.B2"));
		assertTrue(ArgTools.getPath(args, "a.b3").equals("A.B3"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.b.c=A.B.C");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b.c").equals("A.B.C"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "0=0");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "0").equals("0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "5=5");
		assertTrue(args.size() == 6);
		assertTrue(ArgTools.getPath(args, "0") == null);
		assertTrue(ArgTools.getPath(args, "5").equals("5"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.0=A.0");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0").equals("A.0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.0.b=A.0.B");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0.b").equals("A.0.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.b.c=A.B.C;a.b.d=A.B.D;a.x.c=A.X.C;a.x.d=A.X.D");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(((IArgs) ArgTools.getPath(args, "a")).size() == 2);
		assertTrue(ArgTools.getPath(args, "a.b.c").equals("A.B.C"));
		assertTrue(ArgTools.getPath(args, "a.b.d").equals("A.B.D"));
		assertTrue(ArgTools.getPath(args, "a.x.c").equals("A.X.C"));
		assertTrue(ArgTools.getPath(args, "a.x.d").equals("A.X.D"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putDefinition(args, "a.b.c=\"A.B.C\";a.b.d=\"A.B.D\";a.x.c=\"A.X.C\";a.x.d=\"A.X.D\"");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(((IArgs) ArgTools.getPath(args, "a")).size() == 2);
		assertTrue(ArgTools.getPath(args, "a.b.c").equals("A.B.C"));
		assertTrue(ArgTools.getPath(args, "a.b.d").equals("A.B.D"));
		assertTrue(ArgTools.getPath(args, "a.x.c").equals("A.X.C"));
		assertTrue(ArgTools.getPath(args, "a.x.d").equals("A.X.D"));
		assertTrue(ArgTools.getPath(args, ".") == args);
	}

	public void testPutPath() {
		IArgs args;
		//
		args = Args.create();
		assertTrue(ArgTools.getPath(args, "a") == null);
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "A");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a").equals("A"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.b", "A.B");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b").equals("A.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.b1", "A.B1");
		ArgTools.putPath(args, "a.b2", "A.B2");
		ArgTools.putPath(args, "a.b3", "A.B3");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b1").equals("A.B1"));
		assertTrue(ArgTools.getPath(args, "a.b2").equals("A.B2"));
		assertTrue(ArgTools.getPath(args, "a.b3").equals("A.B3"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.b.c", "A.B.C");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b.c").equals("A.B.C"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "0", "0");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "0").equals("0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "5", "5");
		assertTrue(args.size() == 6);
		assertTrue(ArgTools.getPath(args, "0") == null);
		assertTrue(ArgTools.getPath(args, "5").equals("5"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.0", "A.0");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0").equals("A.0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.0.b", "A.0.B");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0.b").equals("A.0.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
	}

	public void testPutPathIfAbsent() {
		IArgs args;
		//
		args = Args.create();
		assertTrue(ArgTools.getPath(args, "a") == null);
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a", "A");
		ArgTools.putPathIfAbsent(args, "a", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a").equals("A"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a.b", "A.B");
		ArgTools.putPathIfAbsent(args, "a.b", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b").equals("A.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a.b1", "A.B1");
		ArgTools.putPathIfAbsent(args, "a.b2", "A.B2");
		ArgTools.putPathIfAbsent(args, "a.b3", "A.B3");
		ArgTools.putPathIfAbsent(args, "a.b1", "x");
		ArgTools.putPathIfAbsent(args, "a.b2", "x");
		ArgTools.putPathIfAbsent(args, "a.b3", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b1").equals("A.B1"));
		assertTrue(ArgTools.getPath(args, "a.b2").equals("A.B2"));
		assertTrue(ArgTools.getPath(args, "a.b3").equals("A.B3"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a.b.c", "A.B.C");
		ArgTools.putPathIfAbsent(args, "a.b.c", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.b.c").equals("A.B.C"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "0", "0");
		ArgTools.putPathIfAbsent(args, "0", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "0").equals("0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "5", "5");
		ArgTools.putPathIfAbsent(args, "5", "x");
		assertTrue(args.size() == 6);
		assertTrue(ArgTools.getPath(args, "0") == null);
		assertTrue(ArgTools.getPath(args, "5").equals("5"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a.0", "A.0");
		ArgTools.putPathIfAbsent(args, "a.0", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0").equals("A.0"));
		assertTrue(ArgTools.getPath(args, ".") == args);
		//
		args = Args.create();
		ArgTools.putPathIfAbsent(args, "a.0.b", "A.0.B");
		ArgTools.putPathIfAbsent(args, "a.0.b", "x");
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.0.b").equals("A.0.B"));
		assertTrue(ArgTools.getPath(args, ".") == args);
	}

	public void testToArgs_List() {
		IArgs args;
		List<?> value;
		//
		value = Arrays.asList("foo", "bar");
		args = ArgTools.toArgs(value);
		assertThat(args.get(0), is("foo"));
		assertThat(args.get(1), is("bar"));
		assertThat(args.size(), is(2));
	}

	public void testToArgs_Map() {
		IArgs args;
		Map value;
		//
		value = new HashMap();
		value.put("x", "foo");
		value.put("y", "bar");
		args = ArgTools.toArgs(value);
		assertThat(args.get("x"), is("foo"));
		assertThat(args.get("y"), is("bar"));
		assertThat(args.size(), is(2));
		//
		value = new HashMap();
		value.put("x", "foo");
		value.put("y", Arrays.asList("zick", "zack"));
		args = ArgTools.toArgs(value);
		assertThat(args.get("x"), is("foo"));
		assertThat(args.get("y"), instanceOf(IArgs.class));
		assertThat(ArgTools.getPath(args, "y.0"), is("zick"));
		assertThat(ArgTools.getPath(args, "y.1"), is("zack"));
		assertThat(args.size(), is(2));
	}

	public void testToArgs_String() {
		IArgs args;
		String value;
		//
		value = "pageRange=\"0\";size=\"300.0x60.0\";position=\"268.9544982910156x212.4153594970703\"";
		args = ArgTools.toArgs(value);
		assertTrue("0".equals(args.get("pageRange")));
		assertTrue("300.0x60.0".equals(args.get("size")));
		assertTrue("268.9544982910156x212.4153594970703".equals(args
				.get("position")));
	}

	public void testToMap() {
		IArgs args;
		IArgs nested;
		Map<String, Object> map;
		//
		args = Args.create();
		map = ArgTools.toMap(args);
		assertTrue(map.isEmpty());
		//
		args = Args.create();
		args.put("a", "b");
		map = ArgTools.toMap(args);
		assertTrue(map.size() == 1);
		assertTrue("b".equals(map.get("a")));
		//
		args = Args.create();
		args.add("x");
		map = ArgTools.toMap(args);
		assertTrue(map.size() == 1);
		assertTrue("x".equals(map.get("0")));
		//
		args = Args.create();
		args.put("a", "b");
		args.put("x", "y");
		map = ArgTools.toMap(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue("y".equals(map.get("x")));
		//
		args = Args.create();
		args.put("a", "b");
		nested = Args.create();
		nested.put("x", "y");
		args.put("nested", nested);
		map = ArgTools.toMap(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue(map.get("nested") instanceof IArgs);
	}

	public void testToMapDeep() {
		IArgs args;
		IArgs nested;
		Map<String, Object> map;
		//
		args = Args.create();
		map = ArgTools.toMapDeep(args);
		assertTrue(map.isEmpty());
		//
		args = Args.create();
		args.put("a", "b");
		map = ArgTools.toMapDeep(args);
		assertTrue(map.size() == 1);
		assertTrue("b".equals(map.get("a")));
		//
		args = Args.create();
		args.add("x");
		map = ArgTools.toMapDeep(args);
		assertTrue(map.size() == 1);
		assertTrue("x".equals(map.get("0")));
		//
		args = Args.create();
		args.put("a", "b");
		args.put("x", "y");
		map = ArgTools.toMapDeep(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue("y".equals(map.get("x")));
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "nested.x", "y");
		map = ArgTools.toMapDeep(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue(map.get("nested") instanceof Map);
		assertTrue("y".equals(((Map) map.get("nested")).get("x")));
	}

	public void testToMapDeepFlat() {
		IArgs args;
		IArgs nested;
		Map<String, Object> map;
		//
		args = Args.create();
		map = ArgTools.toMapDeepFlat(args);
		assertTrue(map.isEmpty());
		//
		args = Args.create();
		args.put("a", "b");
		map = ArgTools.toMapDeepFlat(args);
		assertTrue(map.size() == 1);
		assertTrue("b".equals(map.get("a")));
		//
		args = Args.create();
		args.add("x");
		map = ArgTools.toMapDeepFlat(args);
		assertTrue(map.size() == 1);
		assertTrue("x".equals(map.get("0")));
		//
		args = Args.create();
		args.put("a", "b");
		args.put("x", "y");
		map = ArgTools.toMapDeepFlat(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue("y".equals(map.get("x")));
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "nested.x", "y");
		map = ArgTools.toMapDeepFlat(args);
		assertTrue(map.size() == 2);
		assertTrue("b".equals(map.get("a")));
		assertTrue("y".equals(map.get("nested.x")));
	}

	public void testUndefinePath() {
		IArgs args = Args.create();
		args.put(0, "index_0");
		args.put("key", "value");

		IArgs childArgs = Args.create();
		args.put("child", childArgs);
		childArgs.put(1, "child_index_1");
		childArgs.put("key", "child_value");

		IArgs grandchildArgs = Args.create();
		childArgs.put("grandchild", grandchildArgs);
		grandchildArgs.put(2, "grandchild_index_2");
		grandchildArgs.put("key", "grandchild_value");

		ArgTools.undefinePath(args, "");
		ArgTools.undefinePath(args, null);
		ArgTools.undefinePath(args, "does.not.exist");
		ArgTools.undefinePath(args, ".<-period");
		ArgTools.undefinePath(args, "period->.");

		// all above result in no changes
		assertEquals("index_0", ArgTools.getObject(args, "0", null));
		assertEquals("value", ArgTools.getObject(args, "key", null));
		assertEquals("child_index_1", ArgTools.getObject(args, "child.1", null));
		assertEquals("child_value", ArgTools.getObject(args, "child.key", null));
		assertEquals("grandchild_index_2", ArgTools.getObject(args, "child.grandchild.2", null));
		assertEquals("grandchild_value", ArgTools.getObject(args, "child.grandchild.key", null));

		// undefine all
		ArgTools.undefinePath(args, "child.grandchild.key");
		assertFalse(ArgTools.getArgs(args, "child.grandchild", null).isDefined("key"));
		assertTrue(ArgTools.getArgs(args, "child.grandchild", null).names().contains("key"));

		ArgTools.undefinePath(args, "child.grandchild.2");
		assertNull(ArgTools.getArgs(args, "child.grandchild", null).get(2));
		assertEquals(4, ArgTools.getArgs(args, "child.grandchild", null).size());

		ArgTools.undefinePath(args, "child");
		assertFalse(args.isDefined("child"));
		assertFalse(args.isDefined("child.key"));
		assertNull(ArgTools.getPath(args, "child.1"));
		assertTrue(args.names().contains("child"));
		assertEquals(3, args.size());
	}
}
