package de.intarsys.tools.servicelocator;

import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singletons by looking up an associated default implementation class in {@link ServiceImplementation}
 * annotation.
 * 
 * @param <T>
 */
public class ServiceResolverDefaultImplementation<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		ServiceImplementation implementation = lookupClass.getAnnotation(ServiceImplementation.class);
		if (implementation != null) {
			Class<T> clazz = implementation.value();
			if (clazz == void.class) {
				clazz = implementation.defaultImplementation();
			}
			if (clazz != void.class) {
				try {
					log.trace("ServiceResolver {} resolve {}", this, lookupClass);
					T object = ObjectTools.createObject(clazz, lookupClass);
					ObjectTools.initObject(object);
					return object;
				} catch (ObjectCreationException e) {
					throw new ServiceCreationException("service creation for " + lookupClass + " failed", e);
				}
			}
		}
		return null;
	}

}
