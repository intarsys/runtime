package de.intarsys.tools.exception;

import javax.annotation.PostConstruct;

import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

@SuppressWarnings("java:S2176")
public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

	private static final ILogger Log = LogTools.getLogger(UncaughtExceptionHandler.class);

	@PostConstruct
	public void install() {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.severe("Exception in thread '{}'", t.getName(), e);
	}

}
