package de.intarsys.tools.factory;

import java.util.List;

/**
 * An {@link IFactory} that has additional information on the instances created.
 * 
 */
public interface IBookkeepingFactory<T> extends IFactory<T> {

	/**
	 * The currently active instance for this factory.
	 * <p>
	 * This depends on the factory implementation and may be for example the
	 * currently active printing process.
	 * 
	 * @return The currently active instance for this factory.
	 */
	public T getActive();

	/**
	 * The collection of currently available instances for this factory.
	 * <p>
	 * An implementation should not allow access to its internal data structure
	 * here.
	 * 
	 * @return The collection of currently available instances for this factory.
	 */
	public List<T> getInstances();

	/**
	 * A convenience method to quickly access the number of instances in the
	 * factory. This avoids copying the processor collection.
	 * 
	 * @return The number of currently available objects for this factory.
	 */
	public int size();

}
