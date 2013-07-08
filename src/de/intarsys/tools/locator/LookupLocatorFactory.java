package de.intarsys.tools.locator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Try to create an {@link ILocator} from a list of {@link ILocatorFactory}
 * instances.
 * 
 * The first {@link ILocatorFactory} to create a valid result will return.
 * 
 */
public class LookupLocatorFactory extends CommonLocatorFactory {

	private List<ILocatorFactory> factories = new ArrayList<ILocatorFactory>();

	public boolean addLocatorFactory(ILocatorFactory factory) {
		if (factory == this) {
			// common mistake
			throw new IllegalArgumentException("can not delegate to myself");
		}
		return factories.add(factory);
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		ILocator current = null;
		for (Iterator<ILocatorFactory> it = factories.iterator(); it.hasNext();) {
			ILocatorFactory factory = it.next();
			try {
				current = factory.createLocator(location);
				if (current != null && current.exists()) {
					return current;
				}
			} catch (FileNotFoundException e) {
				// search on
			} catch (IOException e) {
				// we tried to load but failed
				throw e;
			} catch (Exception e) {
				// we tried to load but failed for unknown reason
				throw new IOException("resource lookup '" + location
						+ "' failed ('" + e.getMessage() + ")");
			}
		}
		if (current != null) {
			// return the last one found anyway
			return current;
		}
		throw new FileNotFoundException("'" + location + "' not found");
	}

	public void clear() {
		factories.clear();
	}

	public List<ILocatorFactory> getLocatorFactories() {
		return new ArrayList<>(factories);
	}

	public boolean removeLocatorFactory(ILocatorFactory factory) {
		return factories.remove(factory);
	}
}
