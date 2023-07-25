package de.intarsys.tools.exception;

public class InternalError extends Error {

	public InternalError() {
		super();
	}

	public InternalError(String message) {
		super(message);
	}

	public InternalError(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalError(Throwable e) {
		super(e);
	}
}
