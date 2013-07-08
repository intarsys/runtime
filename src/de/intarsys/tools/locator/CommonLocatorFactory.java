package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for implementing an {@link ILocatorFactory}.
 * 
 */
abstract public class CommonLocatorFactory implements ILocatorFactory {

	abstract protected ILocator basicCreateLocator(String location)
			throws IOException;

	@Override
	final public ILocator createLocator(String location) throws IOException {
		if (StringTools.isEmpty(location)) {
			return null;
		}
		return basicCreateLocator(location);
	}

}
