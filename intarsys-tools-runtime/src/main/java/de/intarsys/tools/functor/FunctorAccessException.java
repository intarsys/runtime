package de.intarsys.tools.functor;

/**
 * Access protection related failure to perform method.
 * 
 */
public class FunctorAccessException extends FunctorException {

	public FunctorAccessException() {
		super();
	}

	public FunctorAccessException(String message) {
		super(message);
	}

	public FunctorAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctorAccessException(Throwable cause) {
		super(cause);
	}

}
