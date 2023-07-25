package de.intarsys.tools.bean;

public class BeanNotFoundException extends BeanException {

	public BeanNotFoundException() {
	}

	public BeanNotFoundException(String message) {
		super(message);
	}

	public BeanNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanNotFoundException(Throwable cause) {
		super(cause);
	}

}
