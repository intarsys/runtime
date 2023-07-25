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
	 * It's preferred for the implementation that *only* local lookup is
	 * performed by the {@link ILocatorFactory} returned (only in the namespace
	 * of the receiver) to allow for efficient search implementation.
	 * 
	 * @return An {@link ILocatorFactory} to lookup resources in the context of
	 *         the receiver.
	 */
	public ILocatorFactory getLocatorFactory();

}
