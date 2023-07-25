package de.intarsys.tools.valueholder;

public class ThreadLocalValueHolder<T> implements IValueHolder<T> {

	private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

	@Override
	public T get() {
		return threadLocal.get();
	}

	@Override
	public T set(T value) {
		T oldValue = threadLocal.get();
		if (value == null) {
			threadLocal.remove();
		} else {
			threadLocal.set(value);
		}
		return oldValue;
	}

}
