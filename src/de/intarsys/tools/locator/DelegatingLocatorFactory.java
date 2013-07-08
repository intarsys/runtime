package de.intarsys.tools.locator;

import java.io.IOException;

/**
 * An abstract superclass for implementing a delegation model on the
 * {@link ILocatorFactory}.
 * 
 */
abstract public class DelegatingLocatorFactory extends CommonLocatorFactory {

	final private ILocatorFactory factory;

	protected DelegatingLocatorFactory(ILocatorFactory factory) {
		super();
		this.factory = factory;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		return factory.createLocator(location);
	}

	protected ILocatorFactory getFactory() {
		return factory;
	}

}
