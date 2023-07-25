package de.intarsys.tools.bean;

public abstract class BeanException extends RuntimeException {

	protected BeanException() {
	}

	protected BeanException(String message) {
		super(message);
	}

	protected BeanException(String message, Throwable cause) {
		super(message, cause);
	}

	protected BeanException(Throwable cause) {
		super(cause);
	}

}
