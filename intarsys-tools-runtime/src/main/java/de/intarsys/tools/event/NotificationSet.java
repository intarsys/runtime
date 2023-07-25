package de.intarsys.tools.event;

import java.util.HashSet;
import java.util.Set;

/**
 * Implement a plugin component for the common pattern where we are dependent on
 * a set of ever changing event sources.
 * 
 * This implementation helps housekeeping the set of all observed sources we are
 * interested in.
 *
 */
public class NotificationSet {

	static class NotificationEntry {
		protected INotificationSupport object;
		protected EventType type;
		protected INotificationListener listener;

		public void arm() {
			object.addNotificationListener(type, listener);
		}

		public void disarm() {
			object.removeNotificationListener(type, listener);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NotificationEntry)) {
				return false;
			}
			NotificationEntry other = (NotificationEntry) obj;
			return object.equals(other.object) && type.equals(other.type) && listener.equals(other.listener);
		}

		@Override
		public int hashCode() {
			return object.hashCode() * 43 + listener.hashCode();
		}
	}

	private final Set<NotificationEntry> armed = new HashSet<>();

	private final Set<NotificationEntry> remaining = new HashSet<>();

	public synchronized void arm(INotificationSupport object, EventType type, INotificationListener listener) {
		NotificationEntry newEntry = new NotificationEntry();
		newEntry.object = object;
		newEntry.type = type;
		newEntry.listener = listener;
		remaining.remove(newEntry);
		if (armed.contains(newEntry)) {
			return;
		}
		armed.add(newEntry);
		newEntry.arm();
	}

	public synchronized void disarm() {
		for (NotificationEntry entry : armed) {
			entry.disarm();
		}
		remaining.clear();
		armed.clear();
	}

	public synchronized void disarm(INotificationSupport object, EventType type, INotificationListener listener) {
		NotificationEntry newEntry = new NotificationEntry();
		newEntry.object = object;
		newEntry.type = type;
		newEntry.listener = listener;
		armed.remove(newEntry);
		remaining.remove(newEntry);
		newEntry.disarm();
	}

	public synchronized void disarmRemaining() {
		for (NotificationEntry entry : remaining) {
			entry.disarm();
			armed.remove(entry);
		}
		remaining.clear();
		remaining.addAll(armed);
	}

}
