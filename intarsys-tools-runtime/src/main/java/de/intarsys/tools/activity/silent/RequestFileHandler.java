package de.intarsys.tools.activity.silent;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.RequestFile;
import de.intarsys.tools.yalf.api.ILogger;

public class RequestFileHandler extends SilentActivityHandler {

	private static final ILogger Log = PACKAGE.Log;

	@Override
	protected <R> void basicActivityEnter(IActivity<R> activity) {
		RequestFile requester = (RequestFile) activity;
		Log.info("request file '{}', '{}' no GUI, cancel", requester.getTitle(), requester.getMessage());
		requester.cancel(false);
	}

}
