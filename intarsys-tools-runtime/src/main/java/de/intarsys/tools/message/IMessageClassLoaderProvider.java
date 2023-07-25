package de.intarsys.tools.message;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A provider for a {@link ClassLoader} to be used for message bundle lookup.
 * 
 * This is invented to allow manual declaration of locations to lookup bundles (without adding to the real classpath).
 * 
 */
@ServiceImplementation(NullMessageClassLoaderProvider.class)
public interface IMessageClassLoaderProvider {

	/**
	 * Get the {@link ClassLoader} to be used for loading message bundles.
	 * 
	 * @return
	 */
	public ClassLoader getClassLoader();

}
