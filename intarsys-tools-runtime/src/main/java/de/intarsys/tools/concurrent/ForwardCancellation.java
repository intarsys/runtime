package de.intarsys.tools.concurrent;

/**
 * Forward a cancellation to the target.
 * 
 */
public class ForwardCancellation extends TaskCallbackAdapter {

	private final IPromise target;

	public ForwardCancellation(IPromise target) {
		super();
		this.target = target;
	}

	@Override
	protected void onFailedCancellation(TaskFailed exception) {
		target.fail(exception.getCause());
	}

}
