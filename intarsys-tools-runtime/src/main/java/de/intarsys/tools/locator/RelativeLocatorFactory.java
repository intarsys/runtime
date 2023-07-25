package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.string.PrettyPrinter;

/**
 * Create an {@link ILocator} relative to a given {@link ILocator} or
 * {@link ILocatorFactory}.
 * 
 */
public class RelativeLocatorFactory extends CommonLocatorFactory {

	private final ILocatorFactory locatorFactory;

	private final String path;

	private final ILocator locator;

	public RelativeLocatorFactory(ILocator locator) {
		super();
		this.locatorFactory = null;
		this.path = null;
		this.locator = locator;
	}

	public RelativeLocatorFactory(ILocatorFactory locatorFactory, String path) {
		super();
		this.locatorFactory = locatorFactory;
		path = path.replace("\\", "/");
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		this.path = path;
		this.locator = null;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		if (locatorFactory != null) {
			return locatorFactory.createLocator(path + location);
		}
		return getLocator().getChild(location);
	}

	public ILocator getLocator() {
		return locator;
	}

	@Override
	public void toString(PrettyPrinter printer) {
		printer.appendMember("type", getClass().getName(), null);
		printer.appendMember("locatorFactory", locatorFactory, null);
		printer.appendMember("path", path, null);
		printer.appendMember("locator", locator, null);
	}
}
