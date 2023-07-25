package de.intarsys.tools.session;

import de.intarsys.tools.component.Singleton;

/**
 * A VM singleton to access an {@link ISessionProvider}.
 * 
 */
@Singleton
public class SessionProvider {

	private static ISessionProvider ACTIVE = new ThreadSessionProvider();

	/**
	 * The current {@link ISessionProvider}
	 * 
	 * @return
	 */
	public static ISessionProvider get() {
		return ACTIVE;
	}

	/**
	 * Assign the current {@link ISessionProvider}
	 */
	public static void set(ISessionProvider value) {
		ACTIVE = value;
	}

	private SessionProvider() {
	}

}
