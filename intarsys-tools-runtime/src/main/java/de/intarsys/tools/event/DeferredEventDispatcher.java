package de.intarsys.tools.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Pluggable helper object for management and dispatching of events. This
 * utility is quite handsome when you
 * <ul>
 * <li>need to ensure event ordering in multithreaded scenarios</li>
 * <li>need to trigger events that you want to keep out of synchronized
 * code</li>
 * </ul>
 * <p>
 * Events are forwarded upon "flush" to all listeners in the thread of the
 * caller.
 * 
 */
public class DeferredEventDispatcher implements INotificationListener, INotificationSupport {

	private final List<Event> events = new ArrayList<>();

	private final EventDispatcher dispatcher;

	private final Object lock = new Object();

	public DeferredEventDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public DeferredEventDispatcher(Object owner) {
		this.dispatcher = new EventDispatcher(owner);
	}

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.addNotificationListener(type, listener);
	}

	public void flush() {
		List<Event> tempEvents;
		synchronized (lock) {
			tempEvents = new ArrayList<>(events);
			events.clear();
		}
		for (Event event : tempEvents) {
			dispatcher.handleEvent(event);
		}
	}

	@Override
	public void handleEvent(Event event) {
		synchronized (lock) {
			events.add(event);
		}
	}

	public boolean hasListener() {
		return dispatcher.hasListener();
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.removeNotificationListener(type, listener);
	}
}
