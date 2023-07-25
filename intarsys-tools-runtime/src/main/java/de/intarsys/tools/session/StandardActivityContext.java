package de.intarsys.tools.session;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.exception.InvalidRequestException;

/**
 * A simple standard implementation for {@link IActivityContext}.
 * 
 */
public class StandardActivityContext implements IActivityContext {

	private final AttributeMap attributes = new AttributeMap();

	private int ref;

	private final Object lock = new Object();

	@Override
	public Object acquire() {
		synchronized (lock) {
			ref++;
		}
		return null;
	}

	protected void basicRelease() {
		Object[] keys = attributes.getKeys();
		for (Object key : keys) {
			Object value = attributes.get(key);
			if (value instanceof IDisposable) {
				((IDisposable) value).dispose();
			}
		}
		attributes.clear();
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	@Override
	public int getReferenceCount() {
		synchronized (lock) {
			return ref;
		}
	}

	@Override
	public final boolean release(Object handle) {
		int tempRef;
		synchronized (lock) {
			tempRef = --ref;
		}
		if (tempRef == 0) {
			basicRelease();
			return true;
		}
		return false;
	}

	@Override
	public Object removeAttribute(Object key) {
		synchronized (lock) {
			if (ref <= 0) {
				throw new InvalidRequestException("IActivityContext not active");
			}
			return attributes.removeAttribute(key);
		}
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		synchronized (lock) {
			// stop writing if not active, prevent resource leaks as this will
			// get never released. As it is not acquired, nobody will be
			// interested in the information anyway
			if (ref <= 0) {
				throw new InvalidRequestException("IActivityContext not active");
			}
			return attributes.setAttribute(key, value);
		}
	}
}
