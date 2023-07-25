package de.intarsys.tools.servicelocator;

public class ServiceNotFoundException extends ServiceLocatorException {

	public ServiceNotFoundException() {
	}

	public ServiceNotFoundException(String message) {
		super(message);
	}

	public ServiceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceNotFoundException(Throwable cause) {
		super(cause);
	}

}
