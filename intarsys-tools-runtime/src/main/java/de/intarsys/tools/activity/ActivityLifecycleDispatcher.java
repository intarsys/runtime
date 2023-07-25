package de.intarsys.tools.activity;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.EventDispatcher;

public abstract class ActivityLifecycleDispatcher extends ActivityLifecycleMonitor {

	private final EventDispatcher dispatcher;

	protected ActivityLifecycleDispatcher(EventDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}

	@Override
	protected void doActivityChanged(IActivity<?> activity, AttributeChangedEvent event) {
		super.doActivityChanged(activity, event);
		dispatcher.triggerEvent(event);
	}

	@Override
	protected void doActivityFailed(de.intarsys.tools.activity.IActivity<?> activity) {
	}

}
