package de.intarsys.tools.message;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A factory for {@link IMessageBundle} instances.
 * 
 * This is invented to allow injecting message bundles that are "non static",
 * e.g. allow for adding/removing/updating existing entries programmatically.
 * 
 */
@ServiceImplementation(BasicMessageBundleFactory.class)
public interface IMessageBundleFactory {

	/**
	 * Get the {@link IMessageBundle} "name" via classloader.
	 * 
	 * @param name
	 * @param classloader
	 * @return
	 */
	public IMessageBundle getMessageBundle(String name, ClassLoader classloader);

}
