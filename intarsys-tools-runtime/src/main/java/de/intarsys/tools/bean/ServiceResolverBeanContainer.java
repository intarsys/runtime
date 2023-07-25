package de.intarsys.tools.bean;

import de.intarsys.tools.servicelocator.IServiceResolver;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singleton from {@link IBeanContainer}.
 * 
 * @param <T>
 */
public class ServiceResolverBeanContainer<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		/*
		 * break recursion
		 */
		if (lookupClass != IBeanContainer.class) {
			log.trace("ServiceResolver {} resolve {}", this, lookupClass);
			return BeanContainer.get().lookupBean(lookupClass);
		}
		return null;
	}

}
