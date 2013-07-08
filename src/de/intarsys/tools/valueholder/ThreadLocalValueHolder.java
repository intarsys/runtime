package de.intarsys.tools.valueholder;

public class ThreadLocalValueHolder<T> implements IValueHolder<T> {

	final private ThreadLocal<T> threadLocal = new ThreadLocal<T>();

	public T get() {
		return threadLocal.get();
	}

	public T set(T value) {
		T oldValue = threadLocal.get();
		threadLocal.set(value);
		return oldValue;
	};

}
