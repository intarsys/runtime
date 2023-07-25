package de.intarsys.tools.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import de.intarsys.tools.component.ExpirationWatchdog;
import de.intarsys.tools.component.ExpirationWatchdog.IResourceHolder;
import de.intarsys.tools.component.IExpirationSupport;
import de.intarsys.tools.yalf.api.ILogger;

public class StandardSessionRegistry implements ISessionRegistry {

	private static final ILogger Log = PACKAGE.Log;

	private final ExpirationWatchdog watchdog = new ExpirationWatchdog(new IResourceHolder() {
		@Override
		public void expire(IExpirationSupport resource) {
			unregister((ISession) resource);
		}

		@Override
		public int getResourceCount() {
			return sessions.size();
		}

		@Override
		public List<IExpirationSupport> getResources() {
			return new ArrayList<>(sessions.values());
		}
	});

	private final Map<String, ISession> sessions = new ConcurrentHashMap<>();

	@Override
	public Stream<ISession> getSessions() {
		return new ArrayList<ISession>(sessions.values()).stream();
	}

	@Override
	public ISession lookup(String id) {
		ISession result = sessions.get(id);
		if (result == null || result.isExpired()) {
			throw new SessionExpired("" + id + " expired");
		}
		result.touch();
		return result;
	}

	@Override
	public void register(ISession session) {
		sessions.put(session.getId(), session);
		watchdog.wake();
		session.touch();
		Log.debug("{} registered {}", this, session);
	}

	public void setCleanupInterval(long cleanupInterval) {
		watchdog.setCleanupInterval(cleanupInterval);
	}

	@Override
	public void unregister(ISession session) {
		sessions.remove(session.getId());
		session.dispose();
		Log.debug("{} unregistered {}", this, session);
	}

}
