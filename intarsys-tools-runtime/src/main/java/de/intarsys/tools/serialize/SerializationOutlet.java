package de.intarsys.tools.serialize;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * VM singleton access to {@link ISerializationOutlet}.
 * 
 */
@SingletonProvider
public class SerializationOutlet {

	public static ISerializationOutlet get() {
		return ServiceLocator.get().get(ISerializationOutlet.class);
	}

	private SerializationOutlet() {
	}

}
