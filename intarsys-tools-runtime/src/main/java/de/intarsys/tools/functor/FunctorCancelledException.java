package de.intarsys.tools.functor;

/**
 * An {@link IFunctor} did not complete its processing because of cancellation.
 * 
 */
public class FunctorCancelledException extends FunctorException {

	public FunctorCancelledException() {
		super();
	}

	public FunctorCancelledException(String message) {
		super(message);
	}

}
