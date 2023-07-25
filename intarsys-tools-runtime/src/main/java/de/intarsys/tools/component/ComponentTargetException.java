package de.intarsys.tools.component;

public class ComponentTargetException extends ComponentException {

	public ComponentTargetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComponentTargetException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
