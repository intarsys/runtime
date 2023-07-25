package de.intarsys.tools.locator;

import de.intarsys.tools.component.Singleton;

/**
 * 
 */
@Singleton
public final class BasicLocatorFactory {

	private static ILocatorFactory ACTIVE = new FileLocatorFactory();

	public static ILocatorFactory get() {
		return ACTIVE;
	}

	public static void set(ILocatorFactory factory) {
		ACTIVE = factory;
	}

	private BasicLocatorFactory() {
	}

}
