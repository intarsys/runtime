package de.intarsys.tools.concurrent;

public abstract class AbstractFutureTask<R> extends AbstractFuture<R> implements Runnable {

	private boolean asynch;

	protected Thread runner;

	protected AbstractFutureTask() {
		super();
	}

	@Override
	protected void checkInterrupt() {
		if (runner != null) {
			runner.interrupt();
		}
	}

	protected abstract R compute() throws Exception;

	public boolean isAsynch() {
		return asynch;
	}

	@Override
	public final void run() {
		synchronized (lockTask) {
			if (active || cancelled || computed) {
				Log.trace("{} will not run, already {}", getLabel(), getStateString()); //$NON-NLS-1$
				return;
			}
			active = true;
			runner = Thread.currentThread();
		}
		try {
			taskStarted();
			Log.trace("{} started", getLabel()); //$NON-NLS-1$
			R tempResult = compute();
			if (!isAsynch() && !isCancelled()) {
				setResult(tempResult);
			}
		} catch (Throwable e) {
			setException(e);
		} finally {
			synchronized (lockTask) {
				runner = null;
			}
		}
	}

	public final void runAsync() {
		setAsynch(true);
		run();
	}

	public void setAsynch(boolean asynch) {
		this.asynch = asynch;
	}

}
