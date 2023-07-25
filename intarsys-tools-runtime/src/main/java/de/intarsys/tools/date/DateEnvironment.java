package de.intarsys.tools.date;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * A VM singleton for {@link IDateEnvironment}.
 * 
 */
@SingletonProvider
public class DateEnvironment {

	public static IDateEnvironment get() {
		return ServiceLocator.get().get(IDateEnvironment.class);
	}

	private DateEnvironment() {
	}

}
