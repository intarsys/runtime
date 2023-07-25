package de.intarsys.tools.activity.silent;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.RequestPassword;
import de.intarsys.tools.yalf.api.ILogger;

public class RequestPasswordHandler extends SilentActivityHandler {

	private static final ILogger Log = PACKAGE.Log;

	@Override
	protected <R> void basicActivityEnter(IActivity<R> activity) {
		RequestPassword<?> requester = (RequestPassword<?>) activity;
		Log.info("request password '{}', '{}' no GUI, cancel", requester.getTitle(), requester.getMessage());
		requester.cancel(false);
	}

}
