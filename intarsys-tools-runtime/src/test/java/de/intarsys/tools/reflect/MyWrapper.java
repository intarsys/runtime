package de.intarsys.tools.reflect;

public class MyWrapper implements IWrapper {

	private final Object wrapped;

	public MyWrapper(Object wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public Object getWrapped() {
		return wrapped;
	}

}
