package de.intarsys.tools.message;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IMessageBundleFactory} that delegates to a list of {@link IMessageBundleFactory} instances.
 * 
 */
public class CompositeMessageBundleFactory extends CommonMessageBundleFactory {

	private final List<IMessageBundleFactory> factories = new ArrayList<>();

	public void addFactory(IMessageBundleFactory factory) {
		factories.add(factory);
	}

	@Override
	protected CommonMessageBundle createMessageBundle(String name, ClassLoader classloader) {
		List<IMessageBundle> bundles = factories
				.stream()
				.map(factory -> factory.getMessageBundle(name, classloader))
				.toList();
		return new CompositeMessageBundle(this, name, bundles);
	}

}
