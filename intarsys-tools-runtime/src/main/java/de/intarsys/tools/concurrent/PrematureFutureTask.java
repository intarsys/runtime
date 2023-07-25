package de.intarsys.tools.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This {@link Future} will try to compute its value premature when asked to {@link #get()} it.
 * 
 * @param <R>
 */
class PrematureFutureTask<R> extends FutureTask<R> {

	public PrematureFutureTask(Callable<R> callable) {
		super(callable);
	}

	public PrematureFutureTask(Runnable runnable, R result) {
		super(runnable, result);
	}

	@Override
	public R get() throws InterruptedException, ExecutionException {
		/*
		 * if required, simply run synchronously if not already started
		 */
		run();
		return super.get();
	}

	@Override
	public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		/*
		 * if required, simply run synchronously if not already started. This does not comply with timeout request!
		 */
		run();
		return super.get(timeout, unit);
	}
}