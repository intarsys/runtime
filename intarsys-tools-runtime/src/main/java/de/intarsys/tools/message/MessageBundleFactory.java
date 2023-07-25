package de.intarsys.tools.message;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for accessing {@link IMessageBundleFactory}.
 * 
 */
@SingletonProvider
public class MessageBundleFactory {

	public static IMessageBundleFactory get() {
		return ServiceLocator.get().get(IMessageBundleFactory.class);
	}

	private MessageBundleFactory() {
	}

}
