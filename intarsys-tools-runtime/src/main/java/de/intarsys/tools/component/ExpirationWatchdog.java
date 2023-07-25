package de.intarsys.tools.component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.intarsys.tools.concurrent.ThreadTools;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * A tool component that will keep an eye on owners resources and detect
 * expiration conditions.
 * 
 */
public class ExpirationWatchdog {

	public interface IResourceHolder {

		void expire(IExpirationSupport resource);

		int getResourceCount();

		List<IExpirationSupport> getResources();

	}

	private static final ILogger Log = PACKAGE.Log;

	private final Object lock = new Object();

	private final IResourceHolder owner;

	private ScheduledExecutorService cleanupExecutor;

	private long cleanupInterval = 60000;

	public ExpirationWatchdog(IResourceHolder owner) {
		this.owner = owner;
	}

	protected void cleanup() {
		try {
			Log.debug("expiration watchdog cleanup");
			List<IExpirationSupport> tempResources = getOwner().getResources();
			for (IExpirationSupport resource : tempResources) {
				if (resource.isExpired()) {
					Log.debug("expiration watchdog expire {}", resource);
					try {
						getOwner().expire(resource);
					} catch (Throwable e) {
						// do not let the cleanup die...
						Log.warn("expiration watchdog cleanup failed", e);
					}
				}
			}
			wake();
		} catch (Throwable e) {
			// do not let the cleanup die...
			Log.warn("expiration watchdog cleanup failed", e);
		}
	}

	public long getCleanupInterval() {
		return cleanupInterval;
	}

	public IResourceHolder getOwner() {
		return owner;
	}

	public void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
		if (cleanupExecutor != null) {
			stop();
			start();
		}
	}

	protected void start() {
		if (cleanupExecutor != null) {
			return;
		}
		cleanupExecutor = Executors
				.newSingleThreadScheduledExecutor(ThreadTools.newThreadFactoryDaemon("session registry reaper")); //$NON-NLS-1$
		cleanupExecutor.scheduleWithFixedDelay(this::cleanup, getCleanupInterval(), getCleanupInterval(),
				TimeUnit.MILLISECONDS);
	}

	protected void stop() {
		if (cleanupExecutor == null) {
			return;
		}
		cleanupExecutor.shutdown();
		cleanupExecutor = null;
	}

	public void wake() {
		synchronized (lock) {
			if (getOwner().getResourceCount() == 0) {
				stop();
			} else {
				start();
			}
		}
	}

}
