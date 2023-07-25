package de.intarsys.tools.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.intarsys.tools.yalf.api.ILogger;

public class OnError {

	public static <T, R> java.util.function.Function<T, R> log(Throwing.Specific.Function<T, R, Exception> function,
			ILogger log, Supplier<R> defaultValue) {
		return Functions.tryCatch(function, Functions.logger(log, defaultValue));
	}

	public static <T> Supplier<T> log(Throwing.Specific.Supplier<T, Exception> supplier, ILogger log,
			Supplier<T> defaultValue) {
		return Functions.tryCatch(supplier, Functions.logger(log, defaultValue));
	}

	public static <T, R> Function<T, R> rethrow(Throwing.Specific.Function<T, R, Exception> function) {
		return Functions.tryCatch(function, Functions.thrower());
	}

	public static <T> Supplier<T> rethrow(Throwing.Specific.Supplier<T, Exception> supplier) {
		return Functions.tryCatch(supplier, Functions.thrower());
	}

	public static <T> Consumer<T> rethrowConsumer(Throwing.Specific.Consumer<T, Exception> consumer) {
		return Functions.tryCatchConsumer(consumer, Functions.thrower());
	}

	private OnError() {
	}

}
