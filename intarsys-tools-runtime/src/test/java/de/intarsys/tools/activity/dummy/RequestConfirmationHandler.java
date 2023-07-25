package de.intarsys.tools.activity.dummy;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.IActivityHandler;
import de.intarsys.tools.concurrent.IPromise;

public class RequestConfirmationHandler implements IActivityHandler {

	@Override
	public <R> void activityEnter(IActivity<R> activity) {
		((IPromise) activity).finish(null);
	}

}
