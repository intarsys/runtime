/*
 * Copyright (c) 2016, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.function;

public interface Throwing {

	@FunctionalInterface
	public interface BiConsumer<T, U> extends Specific.BiConsumer<T, U, Throwable> {
	}

	@FunctionalInterface
	public interface BiFunction<T, U, R> extends Specific.BiFunction<T, U, R, Throwable> {
	}

	@FunctionalInterface
	public interface BiPredicate<T, U> extends Specific.BiPredicate<T, U, Throwable> {
	}

	@FunctionalInterface
	public interface Consumer<T> extends Specific.Consumer<T, Throwable> {
	}

	@FunctionalInterface
	public interface Function<T, R> extends Specific.Function<T, R, Throwable> {
	}

	@FunctionalInterface
	public interface Predicate<T> extends Specific.Predicate<T, Throwable> {
	}

	@FunctionalInterface
	public interface Runnable extends Specific.Runnable<Throwable> {
	}

	/**
	 * Variations on the standard functional interfaces which throw a specific
	 * subclass of Throwable.
	 */
	public interface Specific {
		@FunctionalInterface
		public interface BiConsumer<T, U, E extends Throwable> {
			void accept(T t, U u) throws E;
		}

		@FunctionalInterface
		public interface BiFunction<T, U, R, E extends Throwable> {
			R apply(T t, U u) throws E;
		}

		@FunctionalInterface
		public interface BiPredicate<T, U, E extends Throwable> {
			boolean accept(T t, U u) throws E;
		}

		@FunctionalInterface
		public interface Consumer<T, E extends Throwable> {
			void accept(T t) throws E;
		}

		@FunctionalInterface
		public interface Function<T, R, E extends Throwable> {
			R apply(T t) throws E;
		}

		@FunctionalInterface
		public interface Predicate<T, E extends Throwable> {
			boolean test(T t) throws E;
		}

		@FunctionalInterface
		public interface Runnable<E extends Throwable> {
			void run() throws E;
		}

		@FunctionalInterface
		public interface Supplier<T, E extends Throwable> {
			T get() throws E;
		}
	}

	@FunctionalInterface
	public interface Supplier<T> extends Specific.Supplier<T, Throwable> {
	}
}