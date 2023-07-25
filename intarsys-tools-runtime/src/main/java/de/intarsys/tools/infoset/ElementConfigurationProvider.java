package de.intarsys.tools.infoset;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * VM singleton to access {@link IElementConfigurationProvider}
 */
@SingletonProvider()
public class ElementConfigurationProvider {

	public static IElementConfigurationProvider get() {
		return ServiceLocator.get().get(IElementConfigurationProvider.class);
	}

	private ElementConfigurationProvider() {
	}

}
