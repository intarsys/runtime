package de.intarsys.tools.concurrent;

import java.util.concurrent.ExecutionException;

/**
 * This is the equivalent to {@link ExecutionException} in the callback case.
 * 
 */
public class TaskExecutionException extends TaskFailed {

	public TaskExecutionException(Throwable cause) {
		super(cause);
	}

	@Override
	public boolean isCancellation() {
		return false;
	}
}
