package de.intarsys.tools.exception;

/**
 * Signal an exception in the system initialization phase, e.g when executing a @PostConstruct method.
 */
public class InitializationException extends RuntimeException {

	public InitializationException() {
	}

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}

}
