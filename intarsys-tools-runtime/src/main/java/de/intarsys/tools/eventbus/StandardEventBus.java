package de.intarsys.tools.eventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;

/**
 * A simple standard implementation for the {@link IEventBus}.
 * 
 */
public class StandardEventBus implements IEventBus {

	private final List<StandardEventSubscription> subscriptions = new CopyOnWriteArrayList<>();

	private final List<INotificationSupport> sources = new ArrayList<>();

	private final INotificationListener listenNotification = new INotificationListener() {
		@Override
		public void handleEvent(Event event) {
			onNotification(event);
		}
	};

	private final Object lock = new Object();

	@Override
	public void handleEvent(Event event) {
		for (StandardEventSubscription subscription : subscriptions) {
			if (subscription.getSourcePredicate().accepts(event.getSource())) {
				if (event.getEventType() == subscription.getEventType()
						|| subscription.getEventType() == EventType.ALWAYS) {
					subscription.getListener().handleEvent(event);
				}
			}
		}
	}

	protected void onNotification(Event event) {
		handleEvent(event);
	}

	@Override
	public void register(INotificationSupport ns) {
		synchronized (lock) {
			sources.add(ns);
			ns.addNotificationListener(EventType.ALWAYS, listenNotification);
		}
	}

	@Override
	public void subscribe(EventSourcePredicate predicate, EventType type, INotificationListener listener) {
		StandardEventSubscription subscription = new StandardEventSubscription();
		subscription.setEventType(type);
		subscription.setListener(listener);
		subscription.setSourcePredicate(predicate);
		subscriptions.add(subscription);
	}

	@Override
	public void unregister(INotificationSupport ns) {
		synchronized (lock) {
			sources.remove(ns);
			ns.removeNotificationListener(EventType.ALWAYS, listenNotification);
		}
	}

}
