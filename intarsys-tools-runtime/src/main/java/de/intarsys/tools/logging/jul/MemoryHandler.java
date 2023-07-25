package de.intarsys.tools.logging.jul;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;

/**
 * A {@link Handler} that keeps its {@link LogRecord}s in memory.
 * 
 * If the maximum number of {@link LogRecord} instances is published, each new
 * publish will overwrite the oldest log record in the buffer.
 * 
 */
public class MemoryHandler extends Handler implements INotificationSupport {

	private static final int DEFAULT_SIZE = 1000;

	private final int size;

	private final String id;

	private final LogRecord[] buffer;

	private int start;

	private int count;

	private EventDispatcher dispatcher;

	public MemoryHandler(String id) {
		this.size = DEFAULT_SIZE;
		this.id = id;
		setFormatter(new SimpleFormatter());
		buffer = new LogRecord[size];
		start = 0;
		count = 0;
	}

	public MemoryHandler(String id, int size) {
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		this.size = size;
		this.id = id;
		setFormatter(new SimpleFormatter());
		buffer = new LogRecord[size];
		start = 0;
		count = 0;
	}

	@Override
	public synchronized void addNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			dispatcher = new EventDispatcher(this);
		}
		dispatcher.addNotificationListener(type, listener);
	}

	public synchronized void clear() {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = null;
		}
		start = 0;
		count = 0;
		triggerChanged(null, null, null);
	}

	@Override
	public void close() throws SecurityException {
		setLevel(java.util.logging.Level.OFF);
	}

	@Override
	public void flush() {
		// not required
	}

	public String getId() {
		return id;
	}

	public synchronized LogRecord[] getLogRecords() {
		LogRecord[] result = new LogRecord[count];
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			result[i] = buffer[ix];
		}
		return result;
	}

	public synchronized int getSize() {
		return size;
	}

	public synchronized String getString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			try {
				sb.append(getFormatter().format(buffer[ix]));
			} catch (Exception ex) {
				//
			}
		}
		return sb.toString();
	}

	@Override
	public synchronized void publish(LogRecord event) {
		if (!isLoggable(event)) {
			return;
		}
		int ix = (start + count) % buffer.length;
		buffer[ix] = event;
		if (count < buffer.length) {
			count++;
		} else {
			start++;
			start %= buffer.length;
		}
		triggerChanged(null, null, event);
	}

	/**
	 * Push all records from the buffer to the target {@link Handler} and clear
	 * the buffer afterwards.
	 * 
	 * @param target
	 */
	public synchronized void publishTo(Handler target) {
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			target.publish(buffer[ix]);
		}
		clear();
	}

	/**
	 * Push all records from the buffer to the target {@link Logger} and clear
	 * the buffer afterwards.
	 * 
	 * @param target
	 */
	public synchronized void publishTo(Logger target) {
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			target.log(buffer[ix]);
		}
		clear();
	}

	@Override
	public synchronized void removeNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.removeNotificationListener(type, listener);
		if (dispatcher.isEmpty()) {
			dispatcher = null;
		}
	}

	protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		Event event = new AttributeChangedEvent(this, attribute, oldValue, newValue);
		triggerEvent(event);
	}

	protected void triggerEvent(Event event) {
		if (dispatcher == null) {
			return;
		}
		try {
			dispatcher.triggerEvent(event);
		} catch (RuntimeException e) {
			// ignore
		}
	}

}
