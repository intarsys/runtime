package de.intarsys.tools.servicelocator;

public abstract class ServiceLocatorException extends RuntimeException {

	protected ServiceLocatorException() {
	}

	protected ServiceLocatorException(String message) {
		super(message);
	}

	protected ServiceLocatorException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ServiceLocatorException(Throwable cause) {
		super(cause);
	}

}
