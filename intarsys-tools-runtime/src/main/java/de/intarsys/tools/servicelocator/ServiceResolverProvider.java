package de.intarsys.tools.servicelocator;

import java.util.Iterator;

import de.intarsys.tools.provider.ProviderTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Resolve singleton from provider framework,
 * 
 * @param <T>
 */
public class ServiceResolverProvider<T> implements IServiceResolver<T> {

	private final ILogger log = LogTools.getLogger(getClass());

	@Override
	public T apply(Class<T> lookupClass) {
		return findFirstProvider(lookupClass);
	}

	protected T findFirstProvider(Class<T> lookupClass) {
		try {
			log.trace("ServiceResolver {} resolve {}", this, lookupClass);
			Iterator<T> ps = ProviderTools.providers(lookupClass);
			while (ps.hasNext()) {
				return ps.next();
			}
		} catch (Throwable e) {
			throw new ServiceCreationException("service creation for " + lookupClass + " failed", e);
		}
		return null;
	}

}
