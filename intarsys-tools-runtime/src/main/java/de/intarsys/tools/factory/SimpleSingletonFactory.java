package de.intarsys.tools.factory;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class SimpleSingletonFactory<T> extends AbstractSingletonFactory<T> {

	private final T object;

	private final Class<T> clazz;

	public SimpleSingletonFactory(Class<T> clazz, T object) {
		this.clazz = clazz;
		this.object = object;
	}

	@Override
	protected T basicCreateSingleton(IArgs args) throws ObjectCreationException {
		return object;
	}

	public T getObject() {
		return object;
	}

	@Override
	public Class<T> getResultType() {
		return clazz;
	}

	@Override
	protected IAttributeSupport getSingletonContext(IArgs args) {
		return this;
	}

}
