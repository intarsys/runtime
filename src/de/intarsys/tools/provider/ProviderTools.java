package de.intarsys.tools.provider;

import java.util.Iterator;

/**
 * Tool class to ease access to provider implementations.
 */
public class ProviderTools {

	public static <T> Iterator<T> providers(Class<T> type) {
		return providers(type, type);
	}

	public static <T> Iterator<T> providers(Class<T> type, Class<?> callerClass) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = callerClass.getClassLoader();
		}
		return Providers.get(classLoader).lookupProviders(type);
	}

}
