package de.intarsys.tools.factory;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class TestInstanceSpec {

	@Test
	public void testBasic() throws ObjectCreationException {
		InstanceSpec spec;
		Object instance;
		IArgs args;
		IFactory factory;
		//
		spec = InstanceSpec.createFromFactory(null, null, null);
		try {
			instance = spec.createInstance();
			Assert.fail();
		} catch (Exception e) {
			//
		}
		// by class
		factory = FactoryTools.toFactory(Schneuff.class);
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schneuff);
		//
		factory = FactoryTools.toFactory(SchneuffFactory.class);
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schneuff);
		//
		factory = FactoryTools.toFactory(Diedel.class);
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Diedel);
		//
		factory = FactoryTools.toFactory(Doedel.class);
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Diedel);
		//
		factory = FactoryTools.toFactory(Schniff.class);
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schniff);

		// by name
		factory = FactoryTools.toFactory(Schneuff.class.getName());
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schneuff);
		//
		factory = FactoryTools.toFactory(SchneuffFactory.class.getName());
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schneuff);
		//
		factory = FactoryTools.toFactory(Diedel.class.getName());
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Diedel);
		//
		factory = FactoryTools.toFactory(Doedel.class.getName());
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Diedel);
		//
		factory = FactoryTools.toFactory(Schniff.class.getName());
		spec = InstanceSpec.createFromFactory(null, factory, null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schniff);

		// by instance
		//
		spec = InstanceSpec.createFromFactory(null, new SchneuffFactory(), null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Schneuff);
		//
		spec = InstanceSpec.createFromFactory(null, new Doedel(), null);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Diedel);

		// by args (instance spec)
		args = Args.create();
		args.put("value", new Doedel());
		spec = InstanceSpec.createFromArgs(null, args);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Doedel);

		args = Args.create();
		args.put("factory", DoedelFactory.class);
		spec = InstanceSpec.createFromArgs(null, args);
		instance = spec.createInstance();
		Assert.assertTrue(instance instanceof Doedel);
	}

	@Test
	public void testGetInstanceEmbeddedNoArgs() throws ObjectCreationException {
		IArgs args;
		Object instance;
		Object ref;
		Object defaultValue;
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class);
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Schniff.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class);
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Schneuff.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", new SchneuffFactory());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", new SchneuffFactory());
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", new SchneuffFactory());
		instance = InstanceSpec.getAsInstance(args, "diedel", Schneuff.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
	}

	@Test
	public void testGetInstanceEmbeddedWithArgs() throws ObjectCreationException {
		IArgs args;
		Object instance;
		Object ref;
		Object defaultValue;
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class);
		args.put("diedelArgs", "a=b");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class);
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", new SchneuffFactory());
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
	}

	@Test
	public void testGetInstanceExplicit() throws ObjectCreationException {
		IArgs args;
		Object instance;
		Object ref;
		Object defaultValue;
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", Schniff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", Schniff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", Schneuff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", Schneuff.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", SchneuffFactory.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		ArgTools.putPath(args, "diedel.factory", SchneuffFactory.class);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
	}

	@Test
	public void testGetInstanceObject() throws ObjectCreationException {
		IArgs args;
		Object instance;
		Object ref;
		Object defaultValue;
		//
		Outlet.get().clear();
		args = null;
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertNull(instance);
		//
		Outlet.get().clear();
		args = null;
		defaultValue = new Object();
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, defaultValue);
		Assert.assertSame(instance, defaultValue);
		//
		Outlet.get().clear();
		args = Args.create();
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertNull(instance);
		//
		Outlet.get().clear();
		args = Args.create();
		defaultValue = new Object();
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, defaultValue);
		Assert.assertSame(instance, defaultValue);
		//
		Outlet.get().clear();
		args = Args.create();
		ref = new Object();
		args.put("diedel", ref);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertSame(instance, ref);
		//
		Outlet.get().clear();
		args = Args.create();
		ref = Integer.valueOf(42);
		args.put("diedel", ref);
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertSame(instance, ref);
		//
		Outlet.get().clear();
		args = Args.create();
		ref = Integer.valueOf(42);
		args.put("diedel", ref);
		instance = InstanceSpec.getAsInstance(args, "diedel", Integer.class, null);
		Assert.assertSame(instance, ref);
		//
		Outlet.get().clear();
		args = Args.create();
		ref = new Object();
		args.put("diedel", ref);
		try {
			instance = InstanceSpec.getAsInstance(args, "diedel", Integer.class, null);
			Assert.fail("class cast exception expected");
		} catch (Exception e) {
			//
		}
	}

	@Test
	public void testGetInstanceString() throws ObjectCreationException {
		IArgs args;
		Object instance;
		Object defaultValue;
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class.getName());
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schniff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Schniff.class, null);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class.getName());
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Schneuff.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", SchneuffFactory.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", SchneuffFactory.class.getName());
		instance = InstanceSpec.getAsInstance(args, "diedel", Schneuff.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", SchneuffFactory.class.getName());
		args.put("diedelArgs", "");
		instance = InstanceSpec.getAsInstance(args, "diedel", Object.class, null);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		args = Args.create();
		args.put("diedel", Schneuff.class.getName());
		try {
			instance = InstanceSpec.getAsInstance(args, "diedel", Integer.class, null);
			Assert.fail("class cast exception expected");
		} catch (Exception e) {
			//
		}
	}

	@Test
	public void testHealArgs() throws ObjectCreationException {
		IArgs args;
		InstanceSpec spec;
		//
		args = Args.create();
		args.put("a", "schnick");
		spec = InstanceSpec.get(args, "a", Object.class, null);
		spec.getArgs().put("foo", "bar");
		Assert.assertTrue(ArgTools.getArgs(args, "a", null) != null);
		Assert.assertTrue(ArgTools.getArgs(args, "a.args", null) != null);
		Assert.assertTrue(ArgTools.getString(args, "a.args.foo", null).equals("bar"));
	}

	@Test
	public void testRolePlainArgs() throws ObjectCreationException {
		IArgs args;
		InstanceSpec spec;
		IArgs specArgs;
		IArgs newArgs;
		IFactory factory;
		//
		args = Args.create();
		specArgs = Args.create();
		specArgs.put("foo", "bar");
		args.put("aArgs", specArgs);
		Assert.assertTrue(args.get("aArgs") == specArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("args") == specArgs);
		//
		args = Args.create();
		args.put("a", "gnu");
		specArgs = Args.create();
		specArgs.put("foo", "bar");
		args.put("aArgs", specArgs);
		Assert.assertTrue(args.get("aArgs") == specArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("args") == specArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("factory").equals("gnu"));
		//
		factory = FactoryTools.toFactory("java.lang.String");
		spec = InstanceSpec.createFromFactory(Object.class, factory, Args.create());
		args = Args.create();
		args.put("a", spec);
		specArgs = Args.create();
		specArgs.put("foo", "bar");
		args.put("aArgs", specArgs);
		Assert.assertTrue(args.get("aArgs") == specArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("args") == specArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("factory") == factory);
		//
		args = Args.create();
		specArgs = Args.create();
		specArgs.put("foo", "bar");
		args.put("aArgs", specArgs);
		newArgs = Args.create();
		newArgs.put("x", "x");
		args.put("aArgs", newArgs);
		Assert.assertTrue(args.get("aArgs") == newArgs);
		Assert.assertTrue(((IArgs) args.get("a")).get("args") == newArgs);
	}

	@Test
	public void testRolePlainString() throws ObjectCreationException {
		IArgs args;
		InstanceSpec spec;
		IArgs specArgs;
		IFactory factory;
		//
		args = Args.create();
		args.put("aArgs", "foo=bar;");
		Assert.assertTrue(args.get("aArgs").equals("foo=bar;"));
		Assert.assertTrue(((IArgs) args.get("a")).get("args").equals("foo=bar;"));
		//
		args = Args.create();
		args.put("a", "gnu");
		args.put("aArgs", "foo=bar;");
		Assert.assertTrue(args.get("aArgs").equals("foo=bar;"));
		Assert.assertTrue(((IArgs) args.get("a")).get("factory").equals("gnu"));
		Assert.assertTrue(((IArgs) args.get("a")).get("args").equals("foo=bar;"));
		//
		factory = FactoryTools.toFactory("java.lang.String");
		spec = InstanceSpec.createFromFactory(Object.class, factory, Args.create());
		args = Args.create();
		args.put("a", spec);
		args.put("aArgs", "foo=bar;");
		Assert.assertTrue(args.get("aArgs").equals("foo=bar;"));
		Assert.assertTrue(((IArgs) args.get("a")).get("factory") == factory);
		Assert.assertTrue(((IArgs) args.get("a")).get("args").equals("foo=bar;"));
		//
		args = Args.create();
		args.put("aArgs", "foo=bar;");
		args.put("aArgs", "x=y;");
		Assert.assertTrue(args.get("aArgs").equals("x=y;"));
		Assert.assertTrue(((IArgs) args.get("a")).get("args").equals("x=y;"));
	}

}
