package de.intarsys.tools.component;

import de.intarsys.tools.servicelocator.ServiceLocator;

@SingletonProvider
public class ExceptionHandler {

	public static IExceptionHandler get() {
		return ServiceLocator.get().get(IExceptionHandler.class);
	}

	private ExceptionHandler() {
	}
}
