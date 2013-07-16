/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pluggable helper object for management and dispatching of events.
 * <p>
 * Events are fowarded immediately on "handleEvent" to all listeners in the
 * thread of the caller.
 * 
 */
public class EventDispatcher implements INotificationSupport,
		INotificationListener, Serializable {

	private static final long serialVersionUID = 1L;

	private final Object owner;

	private EventType[] types = new EventType[4];

	private INotificationListener[] listeners = new INotificationListener[4];

	final private Object lockTrigger = new Object();

	public EventDispatcher(Object pOwner) {
		super();
		owner = pOwner;
	}

	@Override
	public synchronized void addNotificationListener(EventType type,
			INotificationListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener may not be null");
		}
		int length = listeners.length;
		int i = 0;
		while (i < length) {
			if (listeners[i] == null) {
				break;
			}
			i++;
		}
		if (i >= length) {
			INotificationListener[] tempListeners = new INotificationListener[length + 4];
			System.arraycopy(listeners, 0, tempListeners, 0, length);
			listeners = tempListeners;
			EventType[] tempIds = new EventType[length + 4];
			System.arraycopy(types, 0, tempIds, 0, length);
			types = tempIds;
		}
		types[i] = type;
		listeners[i] = listener;
	}

	synchronized public void attach(INotificationSupport support) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			EventType tempType = types[i];
			if (tempType != null) {
				support.addNotificationListener(tempType, listeners[i]);
			}
		}
	}

	public synchronized void clear() {
		types = new EventType[4];
		listeners = new INotificationListener[4];
	}

	synchronized public void detach(INotificationSupport support) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			EventType tempType = types[i];
			if (tempType != null) {
				support.removeNotificationListener(tempType, listeners[i]);
			}
		}
	}

	synchronized public INotificationListener[] getListeners() {
		List<INotificationListener> tempListeners = new ArrayList<INotificationListener>();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null) {
				tempListeners.add(listeners[i]);
			}
		}
		return tempListeners.toArray(new INotificationListener[tempListeners
				.size()]);
	}

	public Object getOwner() {
		return owner;
	}

	synchronized public EventType[] getTypes() {
		List<EventType> temp = new ArrayList<EventType>();
		for (int i = 0; i < types.length; i++) {
			if (types[i] != null) {
				temp.add(types[i]);
			}
		}
		return temp.toArray(new EventType[temp.size()]);
	}

	@Override
	public void handleEvent(Event event) {
		List<INotificationListener> triggered;
		synchronized (this) {
			triggered = new ArrayList<>(listeners.length);
			Object typeId = event.getEventType();
			int length = listeners.length;
			for (int i = 0; i < length; i++) {
				Object id = types[i];
				if (id != typeId && id != EventType.ALWAYS) {
					continue;
				}
				triggered.add(listeners[i]);
			}
		}
		// do not hold lock on this when calling out
		for (INotificationListener trigger : triggered) {
			trigger.handleEvent(event);
		}
	}

	public boolean hasListener() {
		return !isEmpty();
	}

	protected boolean hasListener(EventType type, INotificationListener listener) {
		int length = listeners.length;
		int i = 0;
		while (i < length) {
			if (types[i] == type && listeners[i] == listener) {
				return true;
			}
			i++;
		}
		return false;
	}

	public synchronized boolean isEmpty() {
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized void removeNotificationListener(EventType type,
			INotificationListener listener) {
		int length = listeners.length;
		int i = 0;
		while (i < length) {
			if (types[i] == type && listeners[i] == listener) {
				types[i] = null;
				listeners[i] = null;
				break;
			}
			i++;
		}
	}

	public void triggerEvent(Event event) {
		handleEvent(event);
	}

	public void triggerEventReverse(Event event) {
		List<INotificationListener> triggered;
		synchronized (this) {
			triggered = new ArrayList<>(listeners.length);
			Object typeId = event.getEventType();
			for (int i = listeners.length; i >= 0; i--) {
				Object id = types[i];
				if (id != typeId && id != EventType.ALWAYS) {
					continue;
				}
				triggered.add(listeners[i]);
			}
		}
		// do not hold lock on this when calling out
		for (INotificationListener trigger : triggered) {
			trigger.handleEvent(event);
		}
	}
}
