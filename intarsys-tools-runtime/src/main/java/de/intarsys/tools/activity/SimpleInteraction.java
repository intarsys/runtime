package de.intarsys.tools.activity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.intarsys.tools.concurrent.CompletionStageTools;
import de.intarsys.tools.concurrent.ICompletable;

/**
 * A simple default implementation for {@link IInteraction}.
 * 
 * @param <M>
 * @param <R>
 */
public class SimpleInteraction<M, R> implements IInteraction<M, R> {

	public static <M, R> SimpleInteraction<M, Void> completed(M model, R result) {
		CompletableFuture<R> completable = CompletableFuture.completedFuture(result);
		return new SimpleInteraction(model, completable, completable);
	}

	public static <M> SimpleInteraction<M, Void> failed(M model, Throwable ex) {
		CompletableFuture<Void> completable = CompletionStageTools.failedFuture(ex);
		return new SimpleInteraction(model, completable, completable);
	}

	private final M model;

	private final CompletableFuture<R> future;

	private final ICompletable<R> completable = new ICompletable() {

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return future.cancel(mayInterruptIfRunning);
		}

		@Override
		public void fail(Throwable t) {
			future.completeExceptionally(t);
		}

		@Override
		public void finish() {
			future.complete(null);
		}

		@Override
		public R get() throws InterruptedException, ExecutionException {
			return future.get();
		}

		@Override
		public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
				TimeoutException {
			return future.get(timeout, unit);
		}

		@Override
		public boolean isCancelled() {
			return future.isCancelled();
		}

		@Override
		public boolean isDone() {
			return future.isDone();
		}
	};

	private final CompletionStage<R> stage;

	public SimpleInteraction(M model, CompletableFuture<R> completable, CompletionStage<R> stage) {
		super();
		this.model = model;
		this.future = completable;
		this.stage = stage;
	}

	@Override
	public ICompletable<R> getCompletable() {
		return completable;
	}

	@Override
	public M getModel() {
		return model;
	}

	@Override
	public CompletionStage getStage() {
		return stage;
	}

}
