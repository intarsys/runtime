package de.intarsys.tools.session;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

@SingletonProvider
public final class SessionRegistry {

	public static ISessionRegistry get() {
		return ServiceLocator.get().get(ISessionRegistry.class);
	}

	private SessionRegistry() {
	}

}
