package de.intarsys.tools.factory;

/**
 * A VM singleton for the {@link IOutlet}.
 * 
 */
public class Outlet {

	private static IOutlet ACTIVE = new StandardOutlet();

	static public IOutlet get() {
		return ACTIVE;
	}

	static public void set(IOutlet active) {
		ACTIVE = active;
	}
}
