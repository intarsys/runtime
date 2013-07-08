package de.intarsys.tools.locator;

/**
 * An object that can provide an {@link ILocatorFactory} for local lookup.
 * 
 */
public interface ILocatorFactorySupport {

	/**
	 * An {@link ILocatorFactory} to lookup resources in the context of the
	 * receiver.
	 * 
	 * @return An {@link ILocatorFactory} to lookup resources in the context of
	 *         the receiver.
	 */
	public ILocatorFactory getLocatorFactory();

}
