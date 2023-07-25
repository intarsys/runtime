package de.intarsys.tools.zones;

import de.intarsys.tools.valueholder.IValueHolder;

/**
 * An {@link IValueHolder} that is backed up by an {@link IZone}.
 * 
 * @param <T>
 */
public class ZoneLocal<T> implements IValueHolder<T> {

	private final Object attribute = new Object();

	@Override
	public T get() {
		IZone context = Zone.getCurrent();
		return (T) context.getAttribute(attribute);
	}

	public void remove() {
		IZone context = Zone.getCurrent();
		context.removeAttribute(attribute);
	}

	@Override
	public T set(T value) {
		IZone context = Zone.getCurrent();
		return (T) context.setAttribute(attribute, value);
	}
}
