package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.valueholder.ObjectValueHolder;

/**
 * Factory for a {@link ClassLoaderResourceLocator}.
 * 
 * The name is the fully qualified path to the resource, separated by "/",
 * without leading "/".
 * 
 * Example: "de/intarsys/tools/locator/resource.txt"
 * 
 */
public class ClassLoaderResourceLocatorFactory extends CommonLocatorFactory {

	private final IValueHolder<ClassLoader> classLoader;

	public ClassLoaderResourceLocatorFactory(ClassLoader classLoader) {
		super();
		this.classLoader = new ObjectValueHolder<ClassLoader>(classLoader);
	}

	public ClassLoaderResourceLocatorFactory(IValueHolder<ClassLoader> vh) {
		super();
		this.classLoader = vh;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		return new ClassLoaderResourceLocator(getClassLoader(), location);
	}

	public ClassLoader getClassLoader() {
		ClassLoader tempClassLoader = classLoader.get();
		if (tempClassLoader == null) {
			ClassLoader temp = Thread.currentThread().getContextClassLoader();
			if (temp == null) {
				temp = getClass().getClassLoader();
			}
			return temp;
		}
		return tempClassLoader;
	}
}
