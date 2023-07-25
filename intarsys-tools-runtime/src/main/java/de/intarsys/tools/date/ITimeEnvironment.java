package de.intarsys.tools.date;

import java.time.Instant;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * The system wide time settings.
 * 
 */
@ServiceImplementation(StandardTimeEnvironment.class)
public interface ITimeEnvironment {

	/**
	 * @return the current {@link Instant}
	 */
	public Instant now();

}
