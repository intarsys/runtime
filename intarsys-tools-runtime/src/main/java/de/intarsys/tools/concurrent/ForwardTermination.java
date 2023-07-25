package de.intarsys.tools.concurrent;

import de.intarsys.tools.exception.ExceptionTools;

/**
 * Forward any termination (exception, cancellation or result) to the target.
 * 
 */
public class ForwardTermination<R> extends TaskCallbackAdapter<R> {

	private final IPromise<R> target;

	public ForwardTermination(IPromise<R> target) {
		super();
		this.target = target;
	}

	@Override
	protected void onFailedCancellation(TaskFailed exception) {
		target.cancel(false);
	}

	@Override
	protected void onFailedException(TaskFailed exception) {
		target.fail(ExceptionTools.unwrap(exception));
	}

	@Override
	protected void onFinished(R result) {
		target.finish(result);
	}

}
