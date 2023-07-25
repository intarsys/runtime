package de.intarsys.tools.factory;

import java.util.List;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.tag.Tag;
import de.intarsys.tools.tag.TagTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

public class FactoryTools {

	private static final ILogger Log = PACKAGE.Log;

	protected static <T extends IFactory<?>> T basicLookupFactory(Class<T> clazz) {
		IFactory<?> factory = null;
		try {
			if (IFactory.class.isAssignableFrom(clazz)) {
				factory = ObjectTools.createObject(clazz, IFactory.class);
				Outlet.get().registerFactory(factory.getId(), factory);
				Log.log(Level.DEBUG, "created default factory '" + clazz + "'");
			}
		} catch (Exception e) {
			// ...
		}
		return (T) factory;
	}

	protected static <T extends IFactory<?>> T basicLookupFactoryFuzzy(Class<T> clazz) {
		IFactory<?> factory = null;
		try {
			FactoredBy factoredBy = clazz.getAnnotation(FactoredBy.class);
			if (factoredBy != null) {
				Class factoryClass = factoredBy.factory();
				factory = lookupFactory(factoryClass);
			} else if (IFactory.class.isAssignableFrom(clazz)) {
				factory = ObjectTools.createObject(clazz, IFactory.class);
				Outlet.get().registerFactory(factory.getId(), factory);
				Log.log(Level.DEBUG, "created default factory '" + clazz + "'");
			} else {
				// we do NOT try to detect a "<name>Factory" clazz by loading,
				// this is too expensive and in nearly all cases not intended
				factory = new ClassBasedFactory<>(clazz);
			}
		} catch (Exception e) {
			// ...
		}
		return (T) factory;
	}

	public static Object cleanup(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof IFactory) {
			return value;
		}
		if (value instanceof String) {
			String name = (String) value;
			if (StringTools.isEmpty(name)) {
				return null;
			}
		}
		return value;
	}

	/**
	 * Create an object derived from the factoryObject with args.
	 * 
	 * @param factoryObject The polymorphic {@link IFactory}
	 * @param args          The {@link IArgs} for the instance creation
	 * @return The newly created object
	 * @throws ObjectCreationException
	 */
	public static Object createInstance(Object factoryObject, IArgs args) throws ObjectCreationException {
		IFactory factory = toFactory(factoryObject);
		if (factory == null) {
			throw new ObjectCreationException("factory '" + factoryObject + "'not found");
		}
		return factory.createInstance(args);
	}

	public static <T extends IFactory<?>> T lookupFactory(Class<? extends IFactory> clazz) {
		String id = clazz.getName();
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			factory = basicLookupFactory(clazz);
		}
		return (T) factory;
	}

	/**
	 * "Fuzzy" lookup the {@link IFactory} instance for an id.
	 * <p>
	 * If no {@link IFactory} is registered and can't be created, null is
	 * returned.
	 * 
	 * 
	 * @param id
	 * @param classLoader
	 * @return the {@link IFactory} instance for an id.
	 */
	public static IFactory<?> lookupFactory(String id, ClassLoader classLoader) {
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			try {
				Class clazz = ClassTools.createClass(id, Object.class, classLoader);
				factory = basicLookupFactory(clazz);
			} catch (ObjectCreationException e) {
				// just not available..
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
	public static <T> IFactory<T> lookupFactoryFor(Class<T> clazz) {
		IFactory<T>[] factories = Outlet.get().lookupFactories(clazz);
		if (factories.length > 0) {
			return factories[0];
		}
		String id = clazz.getName() + "Factory";
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			try {
				factory = ObjectTools.createObject(id, IFactory.class, clazz.getClassLoader());
				Outlet.get().registerFactory(factory.getId(), factory);
				Log.log(Level.DEBUG, "created default factory '" + id + "'");
			} catch (Exception e) {
				// ...
			}
		}
		return (IFactory<T>) factory;
	}

	/**
	 * Lookup the {@link IFactory} instance for target clazz. This method tries
	 * to derive the factory by searching the {@link IFactory} with appropriate
	 * types. The search result is filtered by matching a set of tags with the
	 * tags registered for a factory.
	 * <p>
	 * If no {@link IFactory} is registered matching the specified tag set, null
	 * is returned.
	 * 
	 * @param clazz
	 * @param tags
	 * @return the {@link IFactory} instance for target object class
	 */
	public static <T> IFactory<T> lookupFactoryFor(Class<T> clazz, List<Tag> tags) {
		IFactory<T>[] factories = Outlet.get().lookupFactories(clazz);
		for (IFactory<T> factory : factories) {
			if (TagTools.hasTags(factory, tags)) {
				return factory;
			}
		}
		return null;
	}

	public static <T extends IFactory<?>> T lookupFactoryFuzzy(Class<T> clazz) {
		String id = clazz.getName();
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			String tempId = id + "Factory";
			factory = Outlet.get().lookupFactory(tempId);
			if (factory == null) {
				factory = basicLookupFactoryFuzzy(clazz);
			}
		}
		return (T) factory;
	}

	/**
	 * "Fuzzy" lookup the {@link IFactory} instance for an id.
	 * <p>
	 * If no {@link IFactory} is registered and can't be created, null is
	 * returned.
	 * 
	 * 
	 * @param id
	 * @param classLoader
	 * @return the {@link IFactory} instance for an id.
	 */
	public static IFactory<?> lookupFactoryFuzzy(String id, ClassLoader classLoader) {
		IFactory<?> factory = Outlet.get().lookupFactory(id);
		if (factory == null) {
			String tempId = id + "Factory";
			factory = Outlet.get().lookupFactory(tempId);
			if (factory == null) {
				try {
					Class clazz = ClassTools.createClass(id, Object.class, classLoader);
					factory = basicLookupFactoryFuzzy(clazz);
				} catch (ObjectCreationException e) {
					// just not available..
				}
			}
		}
		return factory;
	}

	/**
	 * Create an {@link IFactory} from an object.
	 * 
	 * The object is either:
	 * <ul>
	 * <li>null &ndash; This will return null</li>
	 * <li>an {@code IFactory} &ndash; This will return the factory</li>
	 * <li>a {@link String} &ndash; This will try to create an {@code IFactory} from
	 * the name</li>
	 * <li>a {@link Class} &ndash; This will try to create an {@code IFactory} from
	 * the class</li>
	 * <li>an {@link IArgs} structure &ndash; This will try to create a new
	 * {@code IFactory} from the {@link InstanceSpec}</li>
	 * </ul>
	 * 
	 * @param value
	 * 
	 * @return An {@link IFactory}
	 * 
	 * @throws ObjectCreationException
	 */
	public static IFactory toFactory(Object value) throws ObjectCreationException {
		if (value == null) {
			return null;
		}
		if (value instanceof IFactory) {
			return (IFactory) value;
		}
		if (value instanceof String) {
			String name = (String) value;
			if (StringTools.isEmpty(name)) {
				return null;
			}
			IFactory factory = FactoryTools.lookupFactoryFuzzy(name, null);
			if (factory != null) {
				return factory;
			}
		}
		if (value instanceof Class) {
			IFactory factory = FactoryTools.lookupFactoryFuzzy((Class) value);
			if (factory != null) {
				return factory;
			}
		}
		throw new ObjectCreationException("can't create factory from " + StringTools.safeString(value));
	}

	private FactoryTools() {
	}

}
