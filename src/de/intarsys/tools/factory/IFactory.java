package de.intarsys.tools.factory;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public interface IFactory<T> {

	public T createInstance(IArgs args) throws ObjectCreationException;

	public String getId();

	public Class<T> getResultType();

}
