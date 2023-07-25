package de.intarsys.tools.message;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for accessing {@link IMessageClassLoaderProvider}.
 * 
 */
@SingletonProvider
public class MessageClassLoaderProvider {

	public static IMessageClassLoaderProvider get() {
		return ServiceLocator.get().get(IMessageClassLoaderProvider.class);
	}

	private MessageClassLoaderProvider() {
	}

}
