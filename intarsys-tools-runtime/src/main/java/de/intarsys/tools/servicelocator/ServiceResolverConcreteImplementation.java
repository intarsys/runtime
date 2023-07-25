package de.intarsys.tools.servicelocator;

import java.lang.reflect.Modifier;

import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singletons by trying to instantiate if lookupClass is concrete class.
 * 
 * @param <T>
 */
public class ServiceResolverConcreteImplementation<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		try {
			if (!Modifier.isAbstract(lookupClass.getModifiers())) {
				log.trace("ServiceResolver {} resolve {}", this, lookupClass);
				T object = ObjectTools.createObject(lookupClass, lookupClass);
				ObjectTools.initObject(object);
				return object;
			}
			return null;
		} catch (ObjectCreationException e) {
			throw new ServiceCreationException("service creation for " + lookupClass + " failed", e);
		}
	}

}
