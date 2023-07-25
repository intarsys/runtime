package de.intarsys.tools.infoset;

import de.intarsys.tools.component.Singleton;

/**
 * Singleton access to a {@link IElement} configuration.
 *
 */
@Singleton
public final class Configuration {

	private static IElement ACTIVE = new DocumentBuilder().getRootElement();

	public static IElement get() {
		return ACTIVE;
	}

	public static void set(IElement element) {
		ACTIVE = element;
	}

	private Configuration() {
	}
}
