package de.intarsys.tools.expression;

/**
 * This is a special {@link EvaluationException} that signals that for a composite expression (like "foo.bar") the root
 * namespace ("foo") was not available.
 * 
 * This can be used to act more defensively with expression error handling, especially default evaluation.
 */
public class NamespaceNotFound extends EvaluationException {

	public NamespaceNotFound() {
		super();
	}

	public NamespaceNotFound(String message) {
		super(message);
	}

	public NamespaceNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public NamespaceNotFound(Throwable cause) {
		super(cause);
	}

}
