package de.intarsys.tools.activity;

import de.intarsys.tools.component.SingletonClass;

@SingletonClass
public class ActivityEnvironment {

	private static final ActivityEnvironment ACTIVE = new ActivityEnvironment();

	public static ActivityEnvironment get() {
		return ACTIVE;
	}

	private IActivity activeActivity;

	public IActivity getActiveActivity() {
		return activeActivity;
	}

	public void setActiveActivity(IActivity activeActivity) {
		this.activeActivity = activeActivity;
	}
}
