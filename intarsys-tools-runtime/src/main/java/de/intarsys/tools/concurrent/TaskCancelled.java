package de.intarsys.tools.concurrent;

import java.util.concurrent.CancellationException;

import de.intarsys.tools.exception.ICancellationException;

/**
 * This is the equivalent to {@link CancellationException} in the callback case.
 * 
 */
public class TaskCancelled extends TaskFailed implements ICancellationException {

	public TaskCancelled() {
		super();
	}

	@Override
	public boolean isCancellation() {
		return true;
	}

}
