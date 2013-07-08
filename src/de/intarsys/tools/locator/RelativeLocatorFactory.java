package de.intarsys.tools.locator;

import java.io.IOException;

/**
 * Create an {@link ILocator} as a child of a given parent {@link ILocator}.
 * 
 */
public class RelativeLocatorFactory extends CommonLocatorFactory {

	final private ILocator locator;

	public RelativeLocatorFactory(ILocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		return getLocator().getChild(location);
	}

	public ILocator getLocator() {
		return locator;
	}

}
