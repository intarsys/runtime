package de.intarsys.tools.servicelocator;

/**
 * Resolve to null.
 * 
 * @param <T>
 */
public class ServiceResolverNull<T> implements IServiceResolver<T> {

	@Override
	public T apply(Class<T> lookupClass) {
		return null;
	}

}
