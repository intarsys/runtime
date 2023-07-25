package de.intarsys.tools.message;

public class NullMessageClassLoaderProvider implements IMessageClassLoaderProvider {

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

}
