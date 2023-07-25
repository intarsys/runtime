package de.intarsys.tools.message;

/**
 * Use the current thread context classloader.
 * 
 */
public class ThreadContextMessageClassLoaderProvider implements IMessageClassLoaderProvider {

	@Override
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

}
