package de.intarsys.tools.nls;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

@SingletonProvider
public class NlsContext {

	public static INlsContext get() {
		return ServiceLocator.get().get(INlsContext.class);
	}

	private NlsContext() {
	}

}
