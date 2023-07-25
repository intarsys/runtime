package de.intarsys.tools.factory;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class DoedelFactory implements IFactory {

	@Override
	public Object createInstance(IArgs args) throws ObjectCreationException {
		return new Doedel();
	}

	@Override
	public String getId() {
		return getClass().getName();
	}

	@Override
	public Class getResultType() {
		return Doedel.class;
	}

}
