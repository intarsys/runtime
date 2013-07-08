package de.intarsys.tools.date;

import java.util.Date;

/**
 * Standard IDateEnvironment implementation using the system time to provide
 * date information.
 * 
 */
public class StandardDateEnvironment implements IDateEnvironment {

	public StandardDateEnvironment() {
		super();
	}

	@Override
	public Date now() {
		return new Date();
	}

	@Override
	public Date today() {
		return new Date();
	}

}
