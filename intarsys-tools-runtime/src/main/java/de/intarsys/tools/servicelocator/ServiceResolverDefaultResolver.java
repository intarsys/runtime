package de.intarsys.tools.servicelocator;

import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singleton by applying the defaultResolver {@link IServiceResolver} annotated for the lookupClass.
 * 
 * @param <T>
 */
public class ServiceResolverDefaultResolver<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		ServiceImplementation implementation = lookupClass.getAnnotation(ServiceImplementation.class);
		if (implementation != null) {
			Class<? extends IServiceResolver> resolverClass = implementation.defaultResolver();
			if (resolverClass != ServiceResolverNull.class) {
				try {
					log.trace("ServiceResolver {} resolve {}", this, lookupClass);
					IServiceResolver resolver = ObjectTools.createObject(resolverClass, IServiceResolver.class);
					return (T) resolver.apply(lookupClass);
				} catch (ObjectCreationException e) {
					throw new ServiceCreationException("service creation for " + lookupClass + " failed", e);
				}
			}
		}
		return null;
	}

}
