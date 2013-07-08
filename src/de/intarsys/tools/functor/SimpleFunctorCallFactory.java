package de.intarsys.tools.functor;

import de.intarsys.tools.reflect.ObjectCreationException;

public class SimpleFunctorCallFactory implements IFunctorCallFactory {

	public IFunctorCall createFunctorCall(IFunctor functor, Object receiver,
			IArgs args) throws ObjectCreationException {
		return new FunctorCall(receiver, args);
	}

}
