package de.intarsys.tools.date;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for {@link ITimeEnvironment}.
 * 
 */
@SingletonProvider
public class TimeEnvironment {

	public static ITimeEnvironment get() {
		return ServiceLocator.get().get(ITimeEnvironment.class);
	}

	private TimeEnvironment() {
	}

}
