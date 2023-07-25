package de.intarsys.tools.eventbus;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for accessing a global {@link IEventBus}.
 * 
 */
@SingletonProvider
public class EventBus {

	public static IEventBus get() {
		return ServiceLocator.get().get(IEventBus.class);
	}

	private EventBus() {
	}

}
