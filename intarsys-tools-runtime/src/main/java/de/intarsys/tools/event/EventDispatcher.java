/*
 * Copyright (c) 2007, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Pluggable helper object for management and dispatching of events.
 * <p>
 * Events are forwarded immediately on "handleEvent" to all listeners in the
 * thread of the caller.
 * 
 */
public class EventDispatcher implements INotificationSupport, INotificationListener {

	static class Subscription {

		protected EventType type;
		protected INotificationListener listener;

		public Subscription(EventType<?> type, INotificationListener<?> listener) {
			super();
			this.type = type;
			this.listener = listener;
		}
	}

	private final Object owner;

	private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();

	public EventDispatcher(Object pOwner) {
		super();
		owner = pOwner;
	}

	@Override
	public <E extends Event> void addNotificationListener(EventType<? extends E> type,
			INotificationListener<E> listener) {
		if (listener == null) {
			throw new NullPointerException("listener may not be null");
		}
		subscriptions.add(new Subscription(type, listener));
	}

	/**
	 * Attach all local subscriptions to support.
	 * 
	 * @param support
	 */
	public void attach(INotificationSupport support) {
		for (Subscription subscription : subscriptions) {
			support.addNotificationListener(subscription.type, subscription.listener);
		}
	}

	/**
	 * Clear all subscriptions.
	 */
	public void clear() {
		subscriptions.clear();
	}

	/**
	 * Detach all local subscriptions from support.
	 * 
	 * @param support
	 */
	public void detach(INotificationSupport support) {
		for (Subscription subscription : subscriptions) {
			support.removeNotificationListener(subscription.type, subscription.listener);
		}
	}

	public Object getOwner() {
		return owner;
	}

	@Override
	public void handleEvent(Event event) {
		for (Subscription subscription : subscriptions) {
			Object type = event.getEventType();
			if (subscription.type == type || subscription.type == EventType.ALWAYS) {
				subscription.listener.handleEvent(event);
			}
		}
	}

	public boolean hasListener() {
		return !isEmpty();
	}

	public boolean isEmpty() {
		return subscriptions.isEmpty();
	}

	@Override
	public <E extends Event> void removeNotificationListener(EventType<? extends E> type,
			INotificationListener<E> listener) {
		for (Subscription subscription : subscriptions) {
			if (subscription.type == type && subscription.listener == listener) {
				subscriptions.remove(subscription);
			}
		}
	}

	public int size() {
		return subscriptions.size();
	}

	/**
	 * Convenience method for triggering an {@link AttributeChangedEvent}.
	 * 
	 * @param attribute
	 * @param oldValue
	 * @param newValue
	 */
	public void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		AttributeChangedEvent e = new AttributeChangedEvent(getOwner(), attribute, oldValue, newValue);
		triggerEvent(e);
	}

	public void triggerEvent(Event event) {
		handleEvent(event);
	}

}
