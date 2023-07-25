package de.intarsys.tools.factory;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;

/**
 * A factory creating instances via Java {@link Class} reflection.
 * 
 * @param <T>
 */
public class ClassBasedFactory<T> extends CommonInstantiatingFactory<T> {

	private final Class clazz;

	public ClassBasedFactory(Class clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	protected T basicCreateInstance(IArgs args) throws ObjectCreationException {
		return (T) ObjectTools.createObject(clazz, clazz);
	}

	@Override
	public String getId() {
		return clazz.getName();
	}

	@Override
	public Class<T> getResultType() {
		return clazz;
	}

}
