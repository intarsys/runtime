package de.intarsys.tools.event;

/**
 * This listener will reject all notifications that bubble up while handling
 * another notification (recursive).
 * 
 * @param <T>
 */
public abstract class NonReentrantNotificationListener<T extends Event> implements INotificationListener<T> {

	private boolean active;

	protected abstract void basicHandleEvent(T event);

	@Override
	public void handleEvent(T event) {
		if (active) {
			return;
		}
		try {
			active = true;
			basicHandleEvent(event);
		} finally {
			active = false;
		}
	}

}
