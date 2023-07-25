package de.intarsys.tools.reflect;

public class MyPolymorphic implements IPolymorphic, IWrapper {

	private final Object wrapped;

	public MyPolymorphic(Object wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public Object getWrapped() {
		return wrapped;
	}

}
