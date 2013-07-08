package de.intarsys.tools.locator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create an {@link ILocator} using a lookup table of alias names.
 * 
 */
public class MappedLocatorLookup extends CommonLocatorFactory {

	private Map<String, ILocator> locators = new HashMap<String, ILocator>();

	public void addMap(String name, ILocator locator) {
		locators.put(name, locator);
	}

	@Override
	protected ILocator basicCreateLocator(String location)
			throws IOException {
		ILocator lookup = locators.get(location);
		if (lookup != null) {
			return lookup;
		}
		throw new FileNotFoundException("locator '" + location + "' not found");
	}

	public Map<String, ILocator> getMaps() {
		return new HashMap<String, ILocator>(locators);
	}

	public void removeMap(String name) {
		locators.remove(name);
	}

}
