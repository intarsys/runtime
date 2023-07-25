package de.intarsys.tools.date;

import java.util.Date;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A service providing Date instances with difference accuracy.
 * 
 */
@ServiceImplementation(StandardDateEnvironment.class)
public interface IDateEnvironment {

	/**
	 * @return the current date accurate to the current time.
	 */
	public Date now();

}
