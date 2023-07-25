package de.intarsys.tools.exception;

/**
 * An UnreachableCodeError may be thrown whenever an execution path that the programmer has assumed to be unreachable
 * actually turns out to be reachable. In a perfect world, therefore, this exception will never be thrown. Otherwise it
 * is better to throw an UnreachableCodeError than to do nothing at all and risk leaving the bug undetected!
 */
public class UnreachableCodeError extends Error {
	private static final long serialVersionUID = 1L;

	public UnreachableCodeError() {
		super();
	}

	public UnreachableCodeError(String message) {
		super(message);
	}

	public UnreachableCodeError(Throwable cause) {
		super(cause);
	}

	public UnreachableCodeError(String message, Throwable cause) {
		super(message, cause);
	}
}
