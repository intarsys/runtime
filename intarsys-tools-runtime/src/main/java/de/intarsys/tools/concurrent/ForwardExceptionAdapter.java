package de.intarsys.tools.concurrent;

/**
 * Forward an exception to the target.
 * 
 */
public class ForwardExceptionAdapter extends TaskCallbackAdapter {

	private final IPromise target;

	public ForwardExceptionAdapter(IPromise target) {
		super();
		this.target = target;
	}

	@Override
	protected void onFailedException(TaskFailed exception) {
		target.fail(exception.getCause());
	}

}
