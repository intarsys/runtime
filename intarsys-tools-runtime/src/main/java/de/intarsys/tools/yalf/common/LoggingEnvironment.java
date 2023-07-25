package de.intarsys.tools.yalf.common;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

@SingletonProvider
public class LoggingEnvironment {

	public static ILoggingEnvironment get() {
		return ServiceLocator.get().get(ILoggingEnvironment.class);
	}

	private LoggingEnvironment() {
	}

}
