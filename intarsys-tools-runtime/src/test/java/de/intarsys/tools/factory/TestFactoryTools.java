package de.intarsys.tools.factory;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.reflect.ObjectCreationException;

public class TestFactoryTools {

	@Test
	public void testLookupFactoryClass() throws ObjectCreationException {
		Class clazz;
		IFactory factory;
		Object instance;
		//
		Outlet.get().clear();
		clazz = Schneuff.class;
		factory = FactoryTools.lookupFactory(clazz);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		clazz = SchneuffFactory.class;
		factory = FactoryTools.lookupFactory(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof SchneuffFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		clazz = Schniff.class;
		factory = FactoryTools.lookupFactory(clazz);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		clazz = Diedel.class;
		factory = FactoryTools.lookupFactory(clazz);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		clazz = Doedel.class;
		factory = FactoryTools.lookupFactory(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
	}

	@Test
	public void testLookupFactoryFuzzyClass() throws ObjectCreationException {
		Class clazz;
		IFactory factory;
		Object instance;
		//
		Outlet.get().clear();
		clazz = Schneuff.class;
		factory = FactoryTools.lookupFactoryFuzzy(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof ClassBasedFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		clazz = SchneuffFactory.class;
		factory = FactoryTools.lookupFactoryFuzzy(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof SchneuffFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		clazz = Schniff.class;
		factory = FactoryTools.lookupFactoryFuzzy(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof ClassBasedFactory);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		clazz = Diedel.class;
		factory = FactoryTools.lookupFactoryFuzzy(clazz);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
	}

	@Test
	public void testLookupFactoryFuzzyId() throws ObjectCreationException {
		String id;
		IFactory factory;
		Object instance;
		//
		Outlet.get().clear();
		id = "";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "foo.bar.Gnu";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Schneuff";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof ClassBasedFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.SchneuffFactory";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof SchneuffFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Schniff";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof ClassBasedFactory);
		Assert.assertTrue(instance instanceof Schniff);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Diedel";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
		//
		Outlet.get().clear();
		Outlet.get().clear();
		Outlet.get().registerFactory("ooops", new Doedel("ooops"));
		id = "ooops";
		factory = FactoryTools.lookupFactoryFuzzy(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
	}

	@Test
	public void testLookupFactoryId() throws ObjectCreationException {
		String id;
		IFactory factory;
		Object instance;
		//
		Outlet.get().clear();
		id = "";
		factory = FactoryTools.lookupFactory(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "foo.bar.Gnu";
		factory = FactoryTools.lookupFactory(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Schneuff";
		factory = FactoryTools.lookupFactory(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.SchneuffFactory";
		factory = FactoryTools.lookupFactory(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof SchneuffFactory);
		Assert.assertTrue(instance instanceof Schneuff);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Schniff";
		factory = FactoryTools.lookupFactory(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Diedel";
		factory = FactoryTools.lookupFactory(id, null);
		Assert.assertTrue(factory == null);
		//
		Outlet.get().clear();
		id = "de.intarsys.tools.factory.Doedel";
		factory = FactoryTools.lookupFactory(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
		//
		Outlet.get().clear();
		Outlet.get().registerFactory("ooops", new Doedel("ooops"));
		id = "ooops";
		factory = FactoryTools.lookupFactory(id, null);
		instance = factory.createInstance(Args.create());
		Assert.assertTrue(factory != null);
		Assert.assertTrue(factory instanceof Doedel);
		Assert.assertTrue(instance instanceof Diedel);
	}
}
