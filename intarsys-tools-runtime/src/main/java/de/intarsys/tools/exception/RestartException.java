package de.intarsys.tools.exception;

public class RestartException extends RuntimeException {

	public RestartException() {
	}

	public RestartException(String message) {
		super(message);
	}

	public RestartException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestartException(Throwable cause) {
		super(cause);
	}

}
