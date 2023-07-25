package de.intarsys.tools.action;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

@SingletonProvider
public final class ActionRegistry {

	public static IActionRegistry get() {
		return ServiceLocator.get().get(IActionRegistry.class);
	}

	private ActionRegistry() {
	}
}
