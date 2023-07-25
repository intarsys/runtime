package de.intarsys.tools.session;

import de.intarsys.tools.valueholder.IValueHolder;

/**
 * An {@link IValueHolder} that is backed up by an {@link IActivityContext}.
 * 
 * @param <T>
 */
public class ActivityLocal<T> implements IValueHolder<T> {

	private final Object attribute = new Object();

	@Override
	public T get() {
		IActivityContext context = ActivityContext.get();
		return (T) context.getAttribute(attribute);
	}

	public void remove() {
		IActivityContext context = ActivityContext.get();
		context.removeAttribute(attribute);
	}

	@Override
	public T set(T value) {
		IActivityContext context = ActivityContext.get();
		return (T) context.setAttribute(attribute, value);
	}
}
