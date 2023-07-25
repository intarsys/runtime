package de.intarsys.tools.activity.silent;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.IActivityHandler;

/**
 * A common superclass for all "silent" (headless) based
 * {@link IActivityHandler} implementations.
 *
 */
public abstract class SilentActivityHandler implements IActivityHandler {

	@Override
	public final <R> void activityEnter(IActivity<R> activity) {
		basicActivityEnter(activity);
	}

	protected abstract <R> void basicActivityEnter(IActivity<R> activity);
}
