package de.intarsys.tools.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.concurrent.ThreadTools;

/**
 * A tool component that will keep an eye on all registered resources and detect
 * expiration timeouts.
 * 
 */
public class ExpirationWatchdog {

	private ScheduledExecutorService cleanupExecutor;

	final private List<IExpirationSupport> resources = new ArrayList<IExpirationSupport>();

	final private Object lock = new Object();

	final private Object owner;

	final private Runnable command = new Runnable() {
		public void run() {
			cleanup();
		}
	};

	private long cleanupInterval = 60000;

	private static final Logger Log = PACKAGE.Log;

	public ExpirationWatchdog(Object owner) {
		this.owner = owner;
	}

	protected void cleanup() {
		Log.log(Level.FINER, "expiration watchdog cleanup");
		List<IExpirationSupport> tempResources;
		synchronized (lock) {
			tempResources = new ArrayList<IExpirationSupport>(resources);
		}
		for (IExpirationSupport resource : tempResources) {
			if (resource.isExpired()) {
				Log.log(Level.FINER, "expiration watchdog dispose resource");
				unregisterResource(resource);
				((IDisposable) resource).dispose();
			}
		}
	}

	public long getCleanupInterval() {
		return cleanupInterval;
	}

	public Object getOwner() {
		return owner;
	}

	public void registerResource(IExpirationSupport resource) {
		assert resource instanceof IDisposable;
		synchronized (lock) {
			start();
			resources.add(resource);
		}
	}

	public void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	protected void start() {
		if (cleanupExecutor != null) {
			return;
		}
		cleanupExecutor = Executors
				.newSingleThreadScheduledExecutor(ThreadTools
						.newThreadFactoryDaemon("session registry reaper")); //$NON-NLS-1$
		cleanupExecutor.scheduleWithFixedDelay(command, getCleanupInterval(),
				getCleanupInterval(), TimeUnit.MILLISECONDS);
	}

	protected void stop() {
		if (cleanupExecutor == null) {
			return;
		}
		cleanupExecutor.shutdown();
		cleanupExecutor = null;
	}

	public void unregisterResource(IExpirationSupport resource) {
		synchronized (lock) {
			resources.remove(resource);
			if (resources.size() == 0) {
				stop();
			}
		}
	}
}
