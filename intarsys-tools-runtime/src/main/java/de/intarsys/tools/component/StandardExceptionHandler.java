package de.intarsys.tools.component;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

public class StandardExceptionHandler implements IExceptionHandler {

	private static final ILogger Log = PACKAGE.Log;

	@Override
	public void handleException(Exception e) {
		Log.log(Level.SEVERE, "unhandled exception", e);
	}
}
