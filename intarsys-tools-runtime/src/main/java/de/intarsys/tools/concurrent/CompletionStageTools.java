package de.intarsys.tools.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public final class CompletionStageTools {

	public static <T> CompletionStage<T> exceptionallyCompose(CompletionStage<T> stage,
			Function<Throwable, CompletionStage<T>> recover) {
		return stage
				.handle((result, ex) -> ex == null
						? CompletableFuture.completedFuture(result)
						: recover.apply(ex)) //
				.thenCompose(x -> x);
	}

	/**
	 * Returns a new CompletableFuture that is already completed
	 * exceptionally with the given exception.
	 *
	 * @param ex
	 *            the exception
	 * @param <U>
	 *            the type of the value
	 * @return the exceptionally completed CompletableFuture
	 * @since 9
	 */
	public static <U> CompletableFuture<U> failedFuture(Throwable ex) {
		if (ex == null)
			throw new NullPointerException();
		CompletableFuture<U> future = new CompletableFuture<>();
		future.completeExceptionally(ex);
		return future;
	}

	private CompletionStageTools() {
	}
}
