package de.intarsys.tools.provider;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.junit.Test;

public class TestProviders {

	@Test
	public void skipInvalid() {
		ClassLoader classLoader = new ClassLoader() {

			@Override
			public Enumeration<URL> getResources(String name) throws IOException {
				String actualName = name;
				if ("META-INF/provider/provider.list".equals(name)) {
					actualName = getClass().getPackage().getName().replace('.', '/') + "/skipInvalid-provider.list";
				}
				return super.getResources(actualName);
			}
		};
		Iterator<Object> providers = Providers.get(classLoader).lookupProviders(Object.class);
		assertThat(providers.next().getClass(), sameInstance(Object.class));
	}

	@Test
	public void skipInvalidLast() {
		ClassLoader classLoader = new ClassLoader(Thread.currentThread().getContextClassLoader()) {

			@Override
			public Enumeration<URL> getResources(String name) throws IOException {
				String actualName = name;
				if ("META-INF/provider/provider.list".equals(name)) {
					actualName = getClass().getPackage().getName().replace('.', '/') + "/skipInvalidLast-provider.list";
				}
				return super.getResources(actualName);
			}
		};
		Iterator<Object> providers = Providers.get(classLoader).lookupProviders(Object.class);
		providers.next();
		assertFalse(providers.hasNext());
	}
}
