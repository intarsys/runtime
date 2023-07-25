package de.intarsys.tools.servicelocator;

/**
 * An exception within an {@link IServiceResolver} when trying to create a service object.
 * 
 */
public class ServiceCreationException extends ServiceLocatorException {

	public ServiceCreationException() {
	}

	public ServiceCreationException(String message) {
		super(message);
	}

	public ServiceCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceCreationException(Throwable cause) {
		super(cause);
	}

}
