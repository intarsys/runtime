package de.intarsys.tools.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.reflect.ObjectTools;

public class FactoryTools {

	private static final Logger Log = PACKAGE.Log;

	/**
	 * Lookup the {@link IFactory} instance for a factory class. This tool
	 * method assumes the {@link IFactory} instance is registered with its class
	 * name.
	 * <p>
	 * If no such {@link IFactory} is registered, this method tries to create
	 * and register a new one.
	 * <p>
	 * If no {@link IFactory} is registered and can't be created, null is
	 * returned.
	 * 
	 * @param clazz
	 * @return the {@link IFactory} instance for a factory class
	 */
	static public <T extends IFactory<?>> T lookupFactory(Class<T> clazz) {
		String id = clazz.getName();
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			try {
				factory = ObjectTools.createObject(clazz, IFactory.class);
				Outlet.get().registerFactory(factory);
				Log.log(Level.INFO, "created default factory '" + id + "'");
			} catch (Exception e) {
				// ...
			}
		}
		return (T) factory;
	}

	/**
	 * Lookup the {@link IFactory} instance for an id.
	 * <p>
	 * If no such {@link IFactory} is registered, this method tries to create
	 * and register a new one by interpreting the id as a class name.
	 * <p>
	 * If no {@link IFactory} is registered and can't be created, null is
	 * returned.
	 * 
	 * 
	 * @param id
	 * @param classLoader
	 * @return the {@link IFactory} instance for an id.
	 */
	static public IFactory<?> lookupFactory(String id, ClassLoader classLoader) {
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			try {
				factory = ObjectTools.createObject(id, IFactory.class,
						classLoader);
				Outlet.get().registerFactory(factory);
				Log.log(Level.INFO, "created default factory '" + id + "'");
			} catch (Exception e) {
				// ...
			}
		}
		return factory;
	}

	/**
	 * Lookup the {@link IFactory} instance for target clazz. This method tries
	 * to derive the factory by searching the {@link IFactory} with appropriate
	 * types. If not available it tries to lookup an {@link IFactory} by
	 * appending "Factory" to the clazz name and using it as an id.
	 * <p>
	 * If no such {@link IFactory} is registered, this method tries to create
	 * and register a new one by interpreting the id as a class name.
	 * <p>
	 * If no {@link IFactory} is registered and can't be created, null is
	 * returned.
	 * 
	 * 
	 * @param clazz
	 * @return the {@link IFactory} instance for target object class
	 */
	static public <T> IFactory<T> lookupFactoryFor(Class<T> clazz) {
		IFactory<T>[] factories = Outlet.get().lookupFactories(clazz);
		if (factories.length > 0) {
			return factories[0];
		}
		String id = clazz.getName() + "Factory";
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			try {
				factory = ObjectTools.createObject(id, IFactory.class,
						clazz.getClassLoader());
				Outlet.get().registerFactory(factory);
				Log.log(Level.INFO, "created default factory '" + id + "'");
			} catch (Exception e) {
				// ...
			}
		}
		return (IFactory<T>) factory;
	}
}
