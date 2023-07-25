package de.intarsys.tools.concurrent;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * Singleton access to an {@link IDeferredExecutor}
 */
@SingletonProvider
public class EagerInitializer {

	public static IDeferredExecutor get() {
		return ServiceLocator.get().get(IDeferredExecutor.class);
	}

	private EagerInitializer() {
	}

}
