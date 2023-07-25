package de.intarsys.tools.factory;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class SchneuffFactory implements IFactory {

	@Override
	public Object createInstance(IArgs args) throws ObjectCreationException {
		return new Schneuff();
	}

	@Override
	public String getId() {
		return getClass().getName();
	}

	@Override
	public Class getResultType() {
		return Schneuff.class;
	}

}
