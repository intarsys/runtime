package de.intarsys.tools.proxy;

public class RealizedProxy<T> implements IProxy<T> {

	private final T object;

	public RealizedProxy(T object) {
		super();
		this.object = object;
	}

	@Override
	public T getRealized() {
		return object;
	}

}
