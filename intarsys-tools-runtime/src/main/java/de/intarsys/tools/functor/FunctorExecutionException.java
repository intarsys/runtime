package de.intarsys.tools.functor;

/**
 * Wrapper for the exception thrown inside an {@link IFunctor} implementation.
 * 
 */
public class FunctorExecutionException extends FunctorException {

	public FunctorExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctorExecutionException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
