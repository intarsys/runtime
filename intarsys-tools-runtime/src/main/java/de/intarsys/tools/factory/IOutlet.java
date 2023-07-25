package de.intarsys.tools.factory;

import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * The {@link IOutlet} is a collection of {@link IFactory} instances.
 * 
 */
@ServiceImplementation(StandardOutlet.class)
public interface IOutlet extends INotificationSupport {

	/**
	 * Clear all registered {@link IFactory} instances.
	 */
	public void clear();

	/**
	 * All known {@link IFactory} instances.
	 * 
	 * @return
	 */
	public IFactory<?>[] getFactories();

	/**
	 * Request all {@link IFactory} instances producing the target type.
	 * 
	 * @param type
	 * @return
	 */
	public <T> IFactory<T>[] lookupFactories(Class<T> type);

	/**
	 * An {@link IFactory} with a specific id.
	 * 
	 * @param id
	 * @return
	 */
	public IFactory<?> lookupFactory(String id);

	/**
	 * Register a factory.
	 * 
	 * @param id
	 * @param factory
	 */
	public void registerFactory(String id, IFactory<?> factory);

	/**
	 * Unregister a factory.
	 * 
	 * @param id
	 */
	public void unregisterFactory(String id);

}
