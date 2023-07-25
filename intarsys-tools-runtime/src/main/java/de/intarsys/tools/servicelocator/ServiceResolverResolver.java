package de.intarsys.tools.servicelocator;

import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singleton by applying the list of {@link IServiceResolver} classes annotated for the lookupClass.
 * 
 * @param <T>
 */
public class ServiceResolverResolver<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		ServiceImplementation implementation = lookupClass.getAnnotation(ServiceImplementation.class);
		if (implementation != null) {
			Class<? extends IServiceResolver>[] resolverClasses = implementation.resolver();
			for (Class<? extends IServiceResolver> resolverClass : resolverClasses) {
				try {
					IServiceResolver resolver = ObjectTools.createObject(resolverClass, IServiceResolver.class);
					log.trace("ServiceResolver {} resolve {} with {}", this, lookupClass, resolverClass);
					T object = (T) resolver.apply(lookupClass);
					if (object != null) {
						return object;
					}
				} catch (ObjectCreationException e) {
					throw new ServiceCreationException("service creation for " + lookupClass + " failed", e);
				}
			}
		}
		return null;
	}

}
