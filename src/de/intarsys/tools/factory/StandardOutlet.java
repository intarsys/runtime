package de.intarsys.tools.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StandardOutlet implements IOutlet {

	private Map<String, IFactory> factories = new HashMap<String, IFactory>();

	public IFactory[] getFactories() {
		return factories.values().toArray(new IFactory[factories.size()]);
	}

	public IFactory[] lookupFactories(Class type) {
		Set<IFactory> result = new HashSet<IFactory>();
		for (IFactory factory : factories.values()) {
			Class resultType = factory.getResultType();
			if (resultType != null && type.isAssignableFrom(resultType)) {
				result.add(factory);
			}
		}
		return result.toArray(new IFactory[result.size()]);
	}

	public IFactory lookupFactory(String id) {
		return factories.get(id);
	}

	public void registerFactory(IFactory factory) {
		factories.put(factory.getId(), factory);
	}

	public void unregisterFactory(IFactory factory) {
		factories.remove(factory.getId());
	}

}
