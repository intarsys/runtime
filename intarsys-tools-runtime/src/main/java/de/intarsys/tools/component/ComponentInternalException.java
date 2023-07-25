package de.intarsys.tools.component;

public class ComponentInternalException extends ComponentException {

	public ComponentInternalException() {
		super();
	}

	public ComponentInternalException(String message) {
		super(message);
	}

	public ComponentInternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComponentInternalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ComponentInternalException(Throwable cause) {
		super(cause);
	}

}
