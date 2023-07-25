package de.intarsys.tools.functor;

import de.intarsys.tools.factory.InstanceSpec;
import junit.framework.TestCase;

public class TestArgs extends TestCase {

	public void testCopy() {
		IArgs args;
		IArgs copy;
		//
		args = Args.create();
		copy = args.copy();
		assertTrue(copy != null);
		assertTrue(copy != args);
		assertTrue(copy.size() == args.size());
		//
		args = Args.create();
		args.put("a", "b");
		copy = args.copy();
		assertTrue(copy != null);
		assertTrue(copy != args);
		assertTrue(copy.size() == args.size());
		assertTrue(copy.get("a").equals("b"));
		//
		args = Args.create();
		args.put("a", "b");
		args.put("nested", Args.createNamed("x", "y"));
		copy = args.copy();
		assertTrue(copy != null);
		assertTrue(copy != args);
		assertTrue(copy.size() == args.size());
		assertTrue(copy.get("a").equals("b"));
		assertTrue(copy.get("nested") != args.get("nested"));
		assertTrue(((IArgs) copy.get("nested")).get("x").equals("y"));
	}

	public void testInstanceSpecLegacy() throws Exception {
		IArgs args;
		InstanceSpec spec;
		//
		args = Args.create();
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
		//
		args = Args.create();
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
		//
		args = Args.create();
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		args.declare("x");
		args.declare("xArgs");
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
		//
		args = Args.create();
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		args.declare("xArgs");
		args.declare("x");
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
	}

}
