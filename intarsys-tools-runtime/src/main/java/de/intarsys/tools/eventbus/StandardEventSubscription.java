package de.intarsys.tools.eventbus;

import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;

/**
 * The {@link StandardEventBus} internal representation of a single
 * subscription.
 * 
 */
public class StandardEventSubscription {

	private EventSourcePredicate sourcePredicate;

	private EventType eventType;

	private INotificationListener listener;

	public EventType getEventType() {
		return eventType;
	}

	public INotificationListener getListener() {
		return listener;
	}

	public EventSourcePredicate getSourcePredicate() {
		return sourcePredicate;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void setListener(INotificationListener listener) {
		this.listener = listener;
	}

	public void setSourcePredicate(EventSourcePredicate sourcePredicate) {
		this.sourcePredicate = sourcePredicate;
	}

}
