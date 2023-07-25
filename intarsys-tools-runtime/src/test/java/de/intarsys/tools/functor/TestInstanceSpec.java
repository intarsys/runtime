package de.intarsys.tools.functor;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.factory.ClassBasedFactory;
import de.intarsys.tools.factory.IFactory;
import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.reflect.ObjectCreationException;

public class TestInstanceSpec {

	@Test
	public void testFromArgs() throws ObjectCreationException {
		IArgs specArgs;
		InstanceSpec spec;
		//
		specArgs = null;
		spec = InstanceSpec.createFromArgs(Object.class, specArgs);
		Assert.assertTrue(spec == null);
		//
		specArgs = Args.create();
		spec = InstanceSpec.createFromArgs(Object.class, specArgs);
		Assert.assertTrue(spec == null);
		//
		specArgs = Args.create();
		specArgs.put("args", Args.create());
		spec = InstanceSpec.createFromArgs(Object.class, specArgs);
		Assert.assertTrue(spec.getFactory() == null);
		Assert.assertTrue(spec.getArgs().size() == 0);
		//
		specArgs = Args.create();
		specArgs.put("factory", "java.lang.String");
		spec = InstanceSpec.createFromArgs(Object.class, specArgs);
		Assert.assertTrue(spec.getFactory().equals("java.lang.String"));
		Assert.assertTrue(((ClassBasedFactory) spec.getRealFactory()).getId().equals("java.lang.String"));
	}

	@Test
	public void testGetAsInstanceSpec() throws ObjectCreationException {
		IArgs args;
		IArgs specArgs;
		InstanceSpec spec;
		Object value;

		// empty
		//
		args = Args.create();
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec == null);

		// using args
		//
		args = Args.create();
		specArgs = Args.create();
		args.put("foo", specArgs);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec == null);
		//
		args = Args.create();
		specArgs = Args.create();
		specArgs.put("args", Args.create());
		args.put("foo", specArgs);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory() == null);
		Assert.assertTrue(spec.getArgs().size() == 0);
		//
		args = Args.create();
		specArgs = Args.create();
		specArgs.put("factory", "java.lang.String");
		args.put("foo", specArgs);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory().equals("java.lang.String"));
		Assert.assertTrue(((ClassBasedFactory) spec.getRealFactory()).getId().equals("java.lang.String"));

		// using String
		//
		args = Args.create();
		args.put("foo", "java.lang.String");
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory().equals("java.lang.String"));
		Assert.assertTrue(((ClassBasedFactory) spec.getRealFactory()).getId().equals("java.lang.String"));
		//
		args = Args.create();
		args.put("foo", "java.lang.String");
		spec = InstanceSpec.get(args, "foo", String.class, null);
		Assert.assertTrue(!spec.isInstanceUndefined());
		Assert.assertTrue(spec.createInstance().equals("java.lang.String"));

		// using Class
		//
		args = Args.create();
		args.put("foo", String.class);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory() == String.class);
		Assert.assertTrue(((ClassBasedFactory) spec.getRealFactory()).getId().equals("java.lang.String"));
		//
		args = Args.create();
		args.put("foo", String.class);
		spec = InstanceSpec.get(args, "foo", Class.class, null);
		Assert.assertTrue(!spec.isInstanceUndefined());
		Assert.assertTrue(spec.createInstance() == String.class);

		// using IFactory
		//
		args = Args.create();
		value = new ClassBasedFactory(String.class);
		args.put("foo", value);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory() instanceof ClassBasedFactory);
		Assert.assertTrue(((ClassBasedFactory) spec.getFactory()).getId().equals("java.lang.String"));
		//
		args = Args.create();
		value = new ClassBasedFactory(String.class);
		args.put("foo", value);
		spec = InstanceSpec.get(args, "foo", IFactory.class, null);
		Assert.assertTrue(!spec.isInstanceUndefined());
		Assert.assertTrue(spec.createInstance() == value);

		// using plain value
		//
		args = Args.create();
		value = new Object();
		args.put("foo", value);
		spec = InstanceSpec.get(args, "foo", Object.class, null);
		Assert.assertTrue(spec.getFactory() == null);
		Assert.assertTrue(spec.createInstance() == value);
	}

}
