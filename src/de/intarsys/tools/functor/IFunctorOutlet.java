package de.intarsys.tools.functor;

import java.util.List;

public interface IFunctorOutlet {

	public List<IFunctorFactory> getFunctorFactories();

	public IFunctorFactory lookupFunctorFactory(String id);

	public void registerFunctorFactory(String id, IFunctorFactory factory);

	public void unregisterFunctorFactory(IFunctorFactory factory);
}
