package de.intarsys.tools.monitor;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * VM singleton accesss to a {@link IMonitorRegistry}.
 */
@SingletonProvider
public class MonitorRegistry {

	public static IMonitorRegistry get() {
		return ServiceLocator.get().get(IMonitorRegistry.class);
	}

	private MonitorRegistry() {
	}

}
