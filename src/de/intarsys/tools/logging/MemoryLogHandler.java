package de.intarsys.tools.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

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
public class MemoryLogHandler extends Handler implements INotificationSupport {

	private final static int DEFAULT_SIZE = 1000;

	final private int size;

	final private String id;

	final private LogRecord buffer[];

	private int start, count;

	private EventDispatcher dispatcher;

	public MemoryLogHandler(String id) {
		this.size = DEFAULT_SIZE;
		this.id = id;
		setFormatter(new SimpleFormatter());
		buffer = new LogRecord[size];
		start = 0;
		count = 0;
	}

	public MemoryLogHandler(String id, int size) {
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
	synchronized public void addNotificationListener(EventType type,
			INotificationListener listener) {
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
		setLevel(Level.OFF);
	}

	@Override
	public void flush() {
	}

	public String getId() {
		return id;
	}

	synchronized public LogRecord[] getLogRecords() {
		LogRecord[] result = new LogRecord[count];
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			result[i] = buffer[ix];
		}
		return result;
	}

	synchronized public int getSize() {
		return size;
	}

	synchronized public String getString() {
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
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		int ix = (start + count) % buffer.length;
		buffer[ix] = record;
		if (count < buffer.length) {
			count++;
		} else {
			start++;
			start %= buffer.length;
		}
		triggerChanged(null, null, record);
	}

	/**
	 * Push all records from the buffer to target and clear the buffer
	 * afterwards.
	 * 
	 * @param target
	 */
	public synchronized void push(Handler target) {
		for (int i = 0; i < count; i++) {
			int ix = (start + i) % buffer.length;
			target.publish(buffer[ix]);
		}
		clear();
	}

	@Override
	synchronized public void removeNotificationListener(EventType type,
			INotificationListener listener) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.removeNotificationListener(type, listener);
		if (dispatcher.isEmpty()) {
			dispatcher = null;
		}
	}

	protected void triggerChanged(Object attribute, Object oldValue,
			Object newValue) {
		Event event = new AttributeChangedEvent(this, attribute, oldValue,
				newValue);
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
