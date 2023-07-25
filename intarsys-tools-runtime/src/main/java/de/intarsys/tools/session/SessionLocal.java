package de.intarsys.tools.session;

import de.intarsys.tools.valueholder.IValueHolder;

/**
 * An {@link IValueHolder} that is backed up by an {@link ISession}.
 * 
 * @param <T>
 */
public class SessionLocal<T> implements IValueHolder<T> {

	private final Object attribute = new Object();

	@Override
	public T get() {
		ISession context = SessionProvider.get().getSession();
		return (T) context.getAttribute(attribute);
	}

	public void remove() {
		ISession context = SessionProvider.get().getSession();
		context.removeAttribute(attribute);
	}

	@Override
	public T set(T value) {
		ISession context = SessionProvider.get().getSession();
		return (T) context.setAttribute(attribute, value);
	}
}
