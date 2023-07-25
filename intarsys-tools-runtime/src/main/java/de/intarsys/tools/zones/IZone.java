package de.intarsys.tools.zones;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.function.Throwing;

/**
 * Mimic the JavaScript zone concept.
 * 
 * Opposed to the JS version, an {@link IZone} can be active on many threads!
 * 
 */
public interface IZone extends IAttributeSupport {

	/**
	 * Make this {@link IZone} the active one
	 */
	void enter();

	/**
	 * Create a new child {@link IZone} as defined by {@code spec}:
	 * 
	 * @param spec
	 * @return The new child {@link IZone}
	 */
	IZone fork(ZoneSpec spec);

	/**
	 * 
	 * @return The name of this {@link IZone}.
	 */
	String getName();

	/**
	 * @return The parent of this. This is {@code null} if this is the root zone.
	 * 
	 */
	IZone getParent();

	/**
	 * Deactivate this {@link IZone}.
	 */
	void leave();

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Callable wrap(Callable functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Consumer wrap(Consumer functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Throwing.Specific.Consumer wrap(Throwing.Specific.Consumer functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Throwing.Specific.Function wrap(Throwing.Specific.Function functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Throwing.Specific.Supplier wrap(Throwing.Specific.Supplier functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Function wrap(Function functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Runnable wrap(Runnable functionalInterface);

	/**
	 * Create a new functional interface that will ensure that this zone is active while executing the implementation
	 * and deactivate afterwards.
	 * 
	 * @param functionalInterface
	 * @return The wrapped functional interface
	 */
	Supplier wrap(Supplier functionalInterface);
}
