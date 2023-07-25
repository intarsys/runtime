package de.intarsys.tools.reflect;

/**
 * Access protection related failure to perform method.
 * 
 */
public class MethodAccessException extends MethodException {

	public MethodAccessException(Class clazz, String name) {
		super(clazz, name);
	}

	public MethodAccessException(Class clazz, String name, String message) {
		super(clazz, name, message);
	}

	public MethodAccessException(Class clazz, String name, String message, Throwable cause) {
		super(clazz, name, message, cause);
	}

	public MethodAccessException(Class clazz, String name, Throwable cause) {
		super(clazz, name, cause);
	}

	public MethodAccessException(String name) {
		super(name);
	}

	public MethodAccessException(String name, String message) {
		super(name, message);
	}

	public MethodAccessException(String name, String message, Throwable cause) {
		super(name, message, cause);
	}

	public MethodAccessException(String name, Throwable cause) {
		super(name, cause);
	}

}
