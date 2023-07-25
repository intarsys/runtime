/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.servicelocator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import de.intarsys.tools.bean.IBeanContainer;
import de.intarsys.tools.bean.ServiceResolverBeanContainer;
import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.format.Format;
import de.intarsys.tools.provider.Providers;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Lookup an implementation for a given type.
 * 
 * The implementation applies a number of default strategies for looking up the implementation,
 * - resolve from hints from the {@link ServiceImplementation} annotation
 * - resolve from the {@link IBeanContainer}
 * - resolve from the {@link Providers} framework
 * - resolve a concrete implementation instance
 * 
 */
@SingletonClass
public class ServiceLocator {

	private static ServiceLocator Active = new ServiceLocator();

	public static ServiceLocator get() {
		return Active;
	}

	public static void set(ServiceLocator value) {
		Active = value;
	}

	private final ILogger log = LogTools.getLogger(getClass());

	private final Map<Class, Object> singletons = new ConcurrentHashMap<>();

	/*
	 * use specialized lock to avoid interfering
	 */
	private final Object lock = new Object();

	private final List<IServiceResolver> resolvers = new CopyOnWriteArrayList<>();

	private final Set<Class<?>> stack = new HashSet<>();

	public ServiceLocator() {
		init();
	}

	public void addResolver(IServiceResolver resolver) {
		resolvers.add(0, resolver);
	}

	protected <T> T createSingleton(Class<T> clazz) {
		for (IServiceResolver<T> singletonResolver : resolvers) {
			T object = singletonResolver.apply(clazz);
			if (object != null) {
				return object;
			}
		}
		throw new ServiceNotFoundException("service creation for " + clazz + " failed");
	}

	public <T> T get(Class<T> clazz) {
		/*
		 * do *not* use computeIfAbsent!
		 * 
		 * it is forbidden to do a recursive computeIfAbsent on a ConcurrentHashMap and thats something we must support
		 * (JDK 8). The error is a *random* endless loop!
		 */
		T object = (T) singletons.get(clazz);
		if (object == null) {
			synchronized (lock) {
				object = (T) singletons.get(clazz);
				if (object == null) {
					if (stack.contains(clazz)) {
						String msg = Format.simple("recursive service locator lookup for {} in {}", clazz, stack);
						IllegalStateException e = new IllegalStateException(msg);
						// spring ignores this *completely* sometimes, be sure someone might know.
						log.warn(msg, e);
						throw e;
					}
					try {
						stack.add(clazz);
						object = createSingleton(clazz);
						singletons.put(clazz, object);
						log.debug("ServiceLocator cached {} with {}", clazz, object);
					} finally {
						stack.remove(clazz);
					}
				}
			}
		}
		return object;
	}

	public List<IServiceResolver> getResolvers() {
		return resolvers;
	}

	private void init() {
		resolvers.add(new ServiceResolverResolver());
		resolvers.add(new ServiceResolverBeanContainer());
		resolvers.add(new ServiceResolverProvider());
		resolvers.add(new ServiceResolverDefaultResolver());
		resolvers.add(new ServiceResolverDefaultImplementation());
		resolvers.add(new ServiceResolverConcreteImplementation());
	}

	public <T> T lookup(Class<T> clazz) {
		return (T) singletons.get(clazz);
	}

	public <T> void put(Class<T> clazz, T object) {
		singletons.put(clazz, object);
		log.debug("ServiceLocator bound {} with {}", clazz, object);
	}

	public <T> T remove(Class<T> clazz) {
		T result = (T) singletons.remove(clazz);
		log.debug("ServiceLocator unbound {}", clazz);
		return result;
	}

	public boolean removeResolver(IServiceResolver resolver) {
		return resolvers.remove(resolver);
	}

	public void resetAll() {
		resetServices();
		stack.clear();
		resolvers.clear();
		init();
	}

	public void resetServices() {
		singletons.clear();
	}
}
