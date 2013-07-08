package de.intarsys.tools.factory;

/**
 * The {@link IOutlet} is a collection of {@link IFactory}.
 * 
 */
public interface IOutlet {

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

	public void registerFactory(IFactory<?> factory);

	public void unregisterFactory(IFactory<?> factory);

}
