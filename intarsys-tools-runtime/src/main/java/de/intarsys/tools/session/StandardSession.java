package de.intarsys.tools.session;

import java.util.Objects;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.component.ExpirationPredicate;
import de.intarsys.tools.component.ExpireTimeout;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.oid.IOIDGenerator;
import de.intarsys.tools.oid.UUIDGenerator;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A simple default implementation for {@link ISession}.
 *
 */
public class StandardSession implements ISession {

	private static final int DEFAULT_EXPIRE_AFTER = 1000 * 60 * 5;

	private static final ILogger Log = PACKAGE.Log;

	private static IOIDGenerator<String> OidGenerator = new UUIDGenerator();

	protected static String createSessionId() {
		return OidGenerator.createOID();
	}

	public static IOIDGenerator<String> getOidGenerator() {
		return OidGenerator;
	}

	public static void setOidGenerator(IOIDGenerator<String> oidGenerator) {
		OidGenerator = oidGenerator;
	}

	private final AttributeMap attributes = new AttributeMap();

	private final EventDispatcher dispatcher = new EventDispatcher(this);

	private final String id;

	private final long startTime;

	private ExpirationPredicate expiration;

	private final Object lock = new Object();

	private boolean disposed;

	private final String label;

	public StandardSession() {
		this(createSessionId());
	}

	public StandardSession(String id) {
		super();
		this.id = id;
		this.label = "session " + id;
		this.startTime = System.currentTimeMillis();
		setExpiration(getDefaultExpiration());
	}

	public void addNotificationListener(EventType type, INotificationListener listener) {
		if (isDisposed()) {
			return;
		}
		dispatcher.addNotificationListener(type, listener);
	}

	protected void basicDispose() {
		// hook method
	}

	@Override
	public final void dispose() {
		synchronized (lock) {
			if (disposed) {
				return;
			}
			disposed = true;
		}
		Log.debug("{} dispose", getLabel()); //$NON-NLS-1$
		Object[] keys = attributes.getKeys();
		for (Object key : keys) {
			Object value = attributes.get(key);
			if (value instanceof IDisposable) {
				Log.debug("{} disposing {}", getLabel(), StringTools.safeString(value));
				((IDisposable) value).dispose();
			}
		}
		attributes.clear();
		Log.info("{} disposed", getLabel()); //$NON-NLS-1$
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	protected ExpirationPredicate getDefaultExpiration() {
		return new ExpireTimeout(DEFAULT_EXPIRE_AFTER);
	}

	public ExpirationPredicate getExpiration() {
		return expiration;
	}

	@Override
	public String getId() {
		return id;
	}

	protected Object getLabel() {
		return label;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getTotalUpTime() {
		return System.currentTimeMillis() - getStartTime();
	}

	public boolean isDisposed() {
		synchronized (lock) {
			return disposed;
		}
	}

	@Override
	public boolean isExpired() {
		if (isDisposed()) {
			return true;
		}
		if (getExpiration().isExpired()) {
			// thought about checking attribute values for "nested expiration"
			// and prolong the lifetime of the session accordingly. We rejected
			// this design to get a clear containment hierarchy and avoid corner
			// cases where "innocent" client code keeps a session from expiring
			return true;
		}
		return false;
	}

	@Override
	public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	public void removeNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.removeNotificationListener(type, listener);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		if (isDisposed()) {
			return null;
		}
		return attributes.setAttribute(key, value);
	}

	public void setExpiration(ExpirationPredicate expiration) {
		Objects.requireNonNull(expiration, "expiration cannot be null");
		this.expiration = expiration;
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public void touch() {
		getExpiration().touch();
		// touch is not forwarded to nested IExpirationSupport intentionally
		// nested resources may have a different thinking about being used (e.g.
		// signature devices...)
	}

	protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		Event event = new AttributeChangedEvent(this, attribute, oldValue, newValue);
		triggerEvent(event);
	}

	protected void triggerEvent(Event event) {
		try {
			dispatcher.triggerEvent(event);
		} catch (RuntimeException e) {
			Log.log(Level.SEVERE, "unexpected exception in CommonSession.triggerEvent", //$NON-NLS-1$
					e);
		}
	}

}
