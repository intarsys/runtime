package de.intarsys.tools.reflect;

/**
 * Wrapper for the exception thrown inside a method implementation.
 * 
 */
public class MethodExecutionException extends MethodException {

	public MethodExecutionException(Class clazz, String name, String message, Throwable cause) {
		super(clazz, name, message, cause);
	}

	public MethodExecutionException(Class clazz, String name, Throwable cause) {
		super(clazz, name, cause);
	}

	public MethodExecutionException(String name, String message, Throwable cause) {
		super(name, message, cause);
	}

	public MethodExecutionException(String name, Throwable cause) {
		super(name, cause);
	}

}
