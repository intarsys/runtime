package de.intarsys.tools.eventbus;

import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A simple publish / subscribe bus.
 * 
 */
@ServiceImplementation(StandardEventBus.class)
public interface IEventBus extends INotificationListener {

	/**
	 * Any object wanting to propagate its own notifications via the
	 * {@link IEventBus} can simply register himself. Every notification issued
	 * by ns will be forwarded to the {@link IEventBus}.
	 * 
	 * @param ns
	 */
	public void register(INotificationSupport ns);

	/**
	 * Indicate that listener should be informed whenever a notification of
	 * {@link EventType} type is issued by a source accepted by predicate.
	 * 
	 * @param predicate
	 * @param type
	 * @param listener
	 */
	public void subscribe(EventSourcePredicate predicate, EventType type, INotificationListener listener);

	/**
	 * Stop forwarding events from ns to the {@link IEventBus}.
	 * 
	 * @param ns
	 */
	public void unregister(INotificationSupport ns);

}
