package de.intarsys.tools.factory;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for the {@link IOutlet}.
 * 
 */
@SingletonProvider
public class Outlet {

	public static IOutlet get() {
		return ServiceLocator.get().get(IOutlet.class);
	}

	private Outlet() {
	}

}
