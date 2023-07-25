package de.intarsys.tools.factory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.CreatedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.lang.Aliases;
import de.intarsys.tools.proxy.IProxy;

public class StandardOutlet implements IOutlet {

	private final EventDispatcher dispatcher = new EventDispatcher(this);

	private final Map<String, IFactory<?>> factories = new LinkedHashMap<>();

	private final Function<IFactory<?>, IFactory<?>> fUnwrap = factory -> {
		if (factory instanceof IProxy) {
			return (IFactory<?>) ((IProxy<?>) factory).getRealized();
		}
		return factory;
	};

	private final INotificationListener<CreatedEvent> listenCreated = new INotificationListener<CreatedEvent>() {

		@Override
		public void handleEvent(CreatedEvent event) {
			onCreated(event);
		}
	};

	@Override
	public <T extends Event> void addNotificationListener(EventType<? extends T> type,
			INotificationListener<T> listener) {
		dispatcher.addNotificationListener(type, listener);
	}

	@Override
	public synchronized void clear() {
		Map<String, IFactory<?>> temp = new HashMap<>(factories);
		for (String id : temp.keySet()) {
			unregisterFactory(id);
		}
	}

	@Override
	public synchronized IFactory[] getFactories() {
		return factories.values().stream().map(fUnwrap).distinct().toArray(IFactory[]::new);
	}

	@Override
	public synchronized IFactory[] lookupFactories(Class type) {
		Predicate<IFactory<?>> accept = factory -> {
			Class<?> resultType = factory.getResultType();
			return resultType != null && type.isAssignableFrom(resultType);
		};
		return factories.values().stream().filter(accept).map(fUnwrap).distinct().toArray(IFactory[]::new);
	}

	@Override
	public synchronized IFactory<?> lookupFactory(String id) {
		IFactory<?> factory = factories.get(Aliases.get().resolve(id));
		return fUnwrap.apply(factory);
	}

	protected void onCreated(Event event) {
		triggerEvent(event);
	}

	@Override
	public synchronized void registerFactory(String id, IFactory<?> factory) {
		IFactory<?> oldFactory = factories.get(id);
		if (oldFactory != null) {
			throw new InvalidRequestException("IFactory '" + id + "' already registered");
		}
		factories.put(id, factory);
		if (factory instanceof INotificationSupport) {
			// just in case we have an alias
			((INotificationSupport) factory).removeNotificationListener(CreatedEvent.ID, listenCreated);
			((INotificationSupport) factory).addNotificationListener(CreatedEvent.ID, listenCreated);
		}
		AttributeChangedEvent event = new AttributeChangedEvent(this, "factories", null, factory); //$NON-NLS-1$
		triggerEvent(event);
	}

	@Override
	public <T extends Event> void removeNotificationListener(EventType<? extends T> type,
			INotificationListener<T> listener) {
		dispatcher.removeNotificationListener(type, listener);
	}

	protected void triggerEvent(Event event) {
		dispatcher.triggerEvent(event);
	}

	@Override
	public synchronized void unregisterFactory(String id) {
		IFactory factory = factories.remove(id);
		if (factory != null) {
			if (factory instanceof INotificationSupport) {
				((INotificationSupport) factory).removeNotificationListener(CreatedEvent.ID, listenCreated);
			}
			AttributeChangedEvent event = new AttributeChangedEvent(this, "factories", null, factory); //$NON-NLS-1$
			triggerEvent(event);
		}
	}
}
