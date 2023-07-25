package de.intarsys.tools.streaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.function.Throwing;

public class StreamingTools {

	public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
		return asStream(sourceIterator, false);
	}

	public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
		Iterable<T> iterable = () -> sourceIterator;
		return StreamSupport.stream(iterable.spliterator(), parallel);
	}

	public static <T> Stream<T> concatIfEmpty(Stream<T> stream, Supplier<Stream<T>> additional) {
		List<T> elements = stream.toList();
		if (!elements.isEmpty()) {
			return elements.stream();
		}
		return additional.get();
	}

	public static <T> Stream<T> concatLazy(Stream<T> stream, Future<Stream<T>> additional) {
		return concatLazy(stream, new Supplier<Stream<T>>() {
			@Override
			public Stream<T> get() {
				try {
					return additional.get();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw ExceptionTools.wrap(e);
				} catch (ExecutionException e) {
					throw ExceptionTools.wrap(e);
				}
			}
		});
	}

	public static <T> Stream<T> concatLazy(Stream<T> stream, Supplier<Stream<T>> additional) {
		// todo lazy call
		return Stream.concat(stream, additional.get());
	}

	public static <T> Stream<T> copyStream(Collection<T> list) {
		if (list == null) {
			return Stream.empty();
		}
		return new ArrayList<>(list).stream();
	}

	public static <T> Stream<T> safeStream(Collection<T> list) {
		if (list == null) {
			return Stream.empty();
		}
		return list.stream();
	}

	private StreamingTools() {
	}

}
