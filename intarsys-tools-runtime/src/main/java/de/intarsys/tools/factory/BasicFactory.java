package de.intarsys.tools.factory;

import java.util.Set;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.event.StartedEvent;
import de.intarsys.tools.event.StopRequestedEvent;
import de.intarsys.tools.event.StoppedEvent;
import de.intarsys.tools.reflect.IClassLoaderSupport;

/**
 * Some very basic features to implement {@link IFactory}.
 * 
 * @param <T>
 *            The target instance class created by this factory.
 */
public abstract class BasicFactory<T> implements IFactory<T>, IContextSupport, IClassLoaderSupport, IStartStop {

	static {
		new CanonicalFromFactoryConverter();
	}

	private String id;

	private Object context;

	protected final Object lock = new Object();

	private boolean started;

	private final INotificationListener<StartedEvent> listenContextStarted = new INotificationListener<StartedEvent>() {
		@Override
		public void handleEvent(StartedEvent event) {
			basicStart();
		}
	};

	private final INotificationListener<StoppedEvent> listenContextStopped = new INotificationListener<StoppedEvent>() {
		@Override
		public void handleEvent(StoppedEvent event) {
			basicStop();
		}
	};

	private final INotificationListener<StopRequestedEvent> listenContextStopRequested = new INotificationListener<StopRequestedEvent>() {
		@Override
		public void handleEvent(StopRequestedEvent event) {
			if (!basicStopRequested(event.getVisited())) {
				event.veto();
			}
		}
	};

	/**
	 * If the context we are created is {@link INotificationSupport}, we will
	 * get informed about its start.
	 * 
	 * This is the moment when the IOC container is fully initialized and the
	 * application lifecycle begins.
	 */
	protected void basicStart() {
	}

	/**
	 * If the context we are created is {@link INotificationSupport}, we will
	 * get informed about its stop.
	 * 
	 * This is the moment when the IOC container is shut down.
	 */
	protected void basicStop() {
	}

	/**
	 * If the context we are created is {@link INotificationSupport}, we will
	 * get informed about its intention to stop.
	 * 
	 * This is the moment when the IOC container is requested to shut down.
	 * 
	 * @param visited
	 *            The set of already visited objects so far (to avoid recursion)
	 */
	protected boolean basicStopRequested(Set visited) {
		return true;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (getContext() instanceof IClassLoaderSupport) {
			return ((IClassLoaderSupport) getContext()).getClassLoader();
		}
		return getDefaultClassLoader();
	}

	public Object getContext() {
		return context;
	}

	protected ClassLoader getDefaultClassLoader() {
		return getClass().getClassLoader();
	}

	protected String getDefaultId() {
		return getClass().getName();
	}

	@Override
	public String getId() {
		if (id == null) {
			return getDefaultId();
		}
		return id;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
		if (context instanceof INotificationSupport) {
			((INotificationSupport) context).addNotificationListener(StartedEvent.ID, listenContextStarted);
			((INotificationSupport) context).addNotificationListener(StopRequestedEvent.ID, listenContextStopRequested);
			((INotificationSupport) context).addNotificationListener(StoppedEvent.ID, listenContextStopped);
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void start() {
		if (started) {
			return;
		}
		started = true;
		basicStart();
	}

	@Override
	public void stop() {
		if (!started) {
			return;
		}
		started = false;
		basicStop();
	}

	@Override
	public boolean stopRequested(Set visited) {
		return basicStopRequested(visited);
	}

}
