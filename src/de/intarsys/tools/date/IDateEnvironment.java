package de.intarsys.tools.date;

import java.util.Date;

/**
 * A service providing Date instances with difference accuracy.
 * 
 */
public interface IDateEnvironment {

	/**
	 * @return the current date accurate to the current time.
	 */
	Date now();

	/**
	 * @return the current date accurate to the current day.
	 */
	Date today();

}
