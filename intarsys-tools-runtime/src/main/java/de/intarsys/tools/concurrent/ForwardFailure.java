package de.intarsys.tools.concurrent;

/**
 * Forward any failure (exception or cancellation) to the target.
 * 
 */
public class ForwardFailure extends TaskCallbackAdapter {

	private final IPromise target;

	public ForwardFailure(IPromise target) {
		super();
		this.target = target;
	}

	@Override
	protected void onFailed(TaskFailed exception) {
		target.fail(exception.getCause());
	}

}
