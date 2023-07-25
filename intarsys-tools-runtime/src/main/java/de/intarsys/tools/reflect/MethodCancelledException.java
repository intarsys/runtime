package de.intarsys.tools.reflect;

/**
 * Method execution was cancelled.
 * 
 */
public class MethodCancelledException extends MethodException {

	public MethodCancelledException(Class clazz, String name) {
		super(clazz, name);
	}

	public MethodCancelledException(Class clazz, String name, String message) {
		super(clazz, name, message);
	}

	public MethodCancelledException(String name) {
		super(name);
	}

	public MethodCancelledException(String name, String message) {
		super(name, message);
	}

	@Override
	protected Object getMessageSuffix() {
		return " cancelled";
	}
}
