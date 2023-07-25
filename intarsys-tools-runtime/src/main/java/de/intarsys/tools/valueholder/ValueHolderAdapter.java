package de.intarsys.tools.valueholder;

/**
 * A simple adapter for subclassing.
 * 
 * @param <T>
 */
public abstract class ValueHolderAdapter<T> implements IValueHolder<T> {

	@Override
	public T get() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(T newValue) {
		throw new UnsupportedOperationException();
	}

}
