package de.intarsys.tools.concurrent;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An {@link ExecutorService} that applies an {@link IExecutionDecorator} to any execution.
 * 
 */
public class ExecutionDecoratorExecutor extends AbstractExecutorService {

	private final ExecutorService delegate;

	private final IExecutionDecorator decorator;

	public ExecutionDecoratorExecutor(ExecutorService delegate, IExecutionDecorator decorator) {
		this.delegate = delegate;
		this.decorator = decorator;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return delegate.awaitTermination(timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		delegate.execute(() -> decorator.execute(command));
	}

	@Override
	public boolean isShutdown() {
		return delegate.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return delegate.isTerminated();
	}

	@Override
	public void shutdown() {
		delegate.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return delegate.shutdownNow();
	}
}
