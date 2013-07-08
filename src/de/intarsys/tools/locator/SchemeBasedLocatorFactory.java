package de.intarsys.tools.locator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create an {@link ILocator} where the concrete {@link ILocatorFactory} is
 * identified by a scheme prefix in the location.
 * 
 * An example is "file:c:/temp" or "classpath:myresource.txt".
 * 
 */
public class SchemeBasedLocatorFactory extends CommonLocatorFactory {

	public static SchemeBasedLocatorFactory createDefault() {
		SchemeBasedLocatorFactory lf = new SchemeBasedLocatorFactory();
		lf.registerLocatorFactory("file", new FileLocatorFactory());
		lf.registerLocatorFactory("classpath",
				new ClassLoaderResourceLocatorFactory((ClassLoader) null));
		lf.setNoSchemeLocatorFactory(new FileLocatorFactory());
		return lf;
	}

	private ILocatorFactory noSchemeLocatorFactory;

	private ILocatorFactory unknownSchemeLocatorFactory;

	private Map<String, ILocatorFactory> factories = new HashMap<String, ILocatorFactory>();

	public SchemeBasedLocatorFactory() {
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		String[] parts = location.split("\\:", 2);
		String tempLocation = location;
		ILocatorFactory factory = null;
		if (parts.length == 1) {
			factory = getNoSchemeLocatorFactory();
		} else if (parts[0].length() == 1) {
			// assume drive letter prefix
			factory = getNoSchemeLocatorFactory();
		} else {
			factory = lookupLocatorFactory(parts[0]);
			if (factory == null) {
				factory = getUnknownSchemeLocatorFactory();
			} else {
				tempLocation = parts[1];
			}
		}
		if (factory != null) {
			return factory.createLocator(tempLocation);
		}
		throw new FileNotFoundException("'" + location + "' not found");
	}

	public Map<String, ILocatorFactory> getLocatorFactories() {
		return factories;
	}

	public ILocatorFactory getNoSchemeLocatorFactory() {
		return noSchemeLocatorFactory;
	}

	public ILocatorFactory getUnknownSchemeLocatorFactory() {
		return unknownSchemeLocatorFactory;
	}

	public ILocatorFactory lookupLocatorFactory(String format) {
		return factories.get(format);
	}

	public void registerLocatorFactory(String format, ILocatorFactory factory) {
		factories.put(format, factory);
	}

	public void setNoSchemeLocatorFactory(ILocatorFactory factory) {
		this.noSchemeLocatorFactory = factory;
	}

	public void setUnknownSchemeLocatorFactory(ILocatorFactory factory) {
		this.unknownSchemeLocatorFactory = factory;
	}

	public void unregisterLocatorFactory(String format) {
		factories.remove(format);
	}
}
