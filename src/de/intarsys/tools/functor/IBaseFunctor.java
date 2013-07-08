package de.intarsys.tools.functor;

/**
 * An iconified behavior. The object implementing this interface is the
 * encapsulation of a business logic that can be executed using an {@link IArgs}
 * argument.
 * <p>
 * There is no statement about the concurrency or state restrictions of the
 * IFunctor in this interface. Such restrictions must be stated with the
 * implementation itself.
 */
public interface IBaseFunctor<T> {
	/**
	 * Apply the encapsulated business logic to the arguments.
	 * 
	 * @param call
	 *            The call context
	 * @return The behavior execution result.
	 * 
	 * @throws FunctorInvocationException
	 *             An exception that raised while executing the business logic
	 *             will be wrapped in a FunctorInvocationException.
	 */
	public T perform(Object... args) throws FunctorInvocationException;
}
