package de.intarsys.tools.functor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StandardFunctorOutlet implements IFunctorOutlet {

	private Map<String, IFunctorFactory> factories = new HashMap<String, IFunctorFactory>();

	public List<IFunctorFactory> getFunctorFactories() {
		return new ArrayList<IFunctorFactory>(factories.values());
	}

	public IFunctorFactory lookupFunctorFactory(String id) {
		if (id == null) {
			return null;
		}
		return factories.get(id);
	}

	public void registerFunctorFactory(String id, IFunctorFactory factory) {
		factories.put(id, factory);
	}

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
