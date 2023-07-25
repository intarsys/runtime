package de.intarsys.tools.functor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StandardFunctorOutlet implements IFunctorOutlet {

	private Map<String, IFunctorFactory> factories = new HashMap<>();

	@Override
	public List<IFunctorFactory> getFunctorFactories() {
		return new ArrayList<>(factories.values());
	}

	@Override
	public IFunctorFactory lookupFunctorFactory(String id) {
		if (id == null) {
			return null;
		}
		return factories.get(id);
	}

	@Override
	public void registerFunctorFactory(String id, IFunctorFactory factory) {
		factories.put(id, factory);
	}

	@Override
	public void unregisterFunctorFactory(IFunctorFactory factory) {
		for (Iterator it = factories.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue() == factory) {
				it.remove();
				return;
			}
		}
	}

}
