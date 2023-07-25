package de.intarsys.tools.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * This exception is used for communicating an exception in an asynchronous
 * execution.
 * 
 * The "standard" {@link ExecutionException} and {@link CancellationException}
 * are not well suited for the use in a method signature, so they are
 * transformed to some exception in this hierarchy.
 * 
 */
public abstract class TaskFailed extends Exception {

	protected TaskFailed() {
		super();
	}

	protected TaskFailed(String message) {
		super(message);
	}

	protected TaskFailed(String message, Throwable cause) {
		super(message, cause);
	}

	protected TaskFailed(Throwable cause) {
		super(cause);
	}

	public abstract boolean isCancellation();
}
