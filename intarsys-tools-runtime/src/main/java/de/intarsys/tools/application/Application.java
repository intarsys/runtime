/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.application;

import de.intarsys.tools.component.Singleton;

/**
 * Singleton access to the platform "application".
 * 
 * This is a very high level abstraction and can be anything that represents the
 * process that has been started. Other code can access optional behavior of
 * this "application" via the {@link ApplicationTools}.
 * 
 */
@Singleton
public final class Application {

	public static final String BEAN_ROLE_LIFECYCLE = "application.lifecycle";

	private static Object ACTIVE;

	public static final Object get() {
		return ACTIVE;
	}

	public static final void set(Object application) {
		ACTIVE = application;
	}

	private Application() {
	}
}
