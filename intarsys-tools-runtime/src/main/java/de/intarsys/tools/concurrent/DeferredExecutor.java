package de.intarsys.tools.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * This executor defers the execution of its task until {@link #release()}, but a future submitted will be
 * executed synchronously if {@link Future#get()} is requested at any point in time before {@link #release()}!
 */
public class DeferredExecutor extends AbstractExecutorService implements IDeferredExecutor {

	private ExecutorService executor;

	private final List<Runnable> runnables = new ArrayList<>();

	private boolean released = false;

	private Object lock = new Object();

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executor.awaitTermination(timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		synchronized (lock) {
			if (released) {
				executor.execute(command);
			} else {
				runnables.add(command);
			}
		}
	}

	protected int getExecutorPoolSize() {
		return 5;
	}

	@PostConstruct
	public void init() {
		ThreadPoolExecutor tmpExecutor = new ThreadPoolExecutor(
				getExecutorPoolSize(),
				getExecutorPoolSize(),
				10L,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(),
				ThreadTools.newThreadFactoryDaemon("EagerInitializer"));
		tmpExecutor.allowCoreThreadTimeOut(true);
		this.executor = tmpExecutor;
	}

	@Override
	public boolean isShutdown() {
		return executor.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return executor.isTerminated();
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new PrematureFutureTask<>(callable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new PrematureFutureTask<>(runnable, value);
	}

	@Override
	public void release() {
		synchronized (lock) {
			if (released) {
				return;
			}
			released = true;
			/*
			 * drain with lock held to ensure order
			 */
			for (Runnable runnable : runnables) {
				execute(runnable);
			}
			runnables.clear();
		}
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}

}
