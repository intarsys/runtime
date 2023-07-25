package de.intarsys.tools.reflect;

public interface IWrapper<T> {

	static Object unwrap(Object object) {
		Object candidate = object;
		while (candidate instanceof IWrapper) {
			candidate = ((IWrapper) candidate).getWrapped();
		}
		return candidate;
	}

	T getWrapped();
}
