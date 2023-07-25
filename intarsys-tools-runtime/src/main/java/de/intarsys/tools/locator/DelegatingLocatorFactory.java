package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.string.PrettyPrinter;

/**
 * An abstract superclass for implementing a delegation model on the
 * {@link ILocatorFactory}.
 * 
 */
public abstract class DelegatingLocatorFactory extends CommonLocatorFactory {

	private final ILocatorFactory factory;

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

	@Override
	public void toString(PrettyPrinter printer) {
		printer.appendMember("type", getClass().getName(), null);
		printer.appendMember("factory", factory, null);
	}

}
