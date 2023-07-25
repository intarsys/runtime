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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.yalf.api.ILogger;

public class Functions {

	public static <R> java.util.function.Function<Exception, R> logger(ILogger log, Supplier<R> defaultValue) {
		return e -> {
			log.warn(e.getMessage(), e);
			return defaultValue.get();
		};
	}

	public static <R> java.util.function.Function<Exception, R> thrower() {
		return e -> {
			throw ExceptionTools.wrap(e);
		};
	}

	public static <T, R> Function<T, R> tryCatch(Throwing.Specific.Function<T, R, Exception> function) {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception e) {
				throw ExceptionTools.wrap(e);
			}
		};
	}

	public static <T, R> Function<T, R> tryCatch(Throwing.Specific.Function<T, R, Exception> function,
			java.util.function.Function<Exception, R> exceptionHandler) {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception e) {
				return exceptionHandler.apply(e);
			}
		};
	}

	public static <T> Supplier<T> tryCatch(Throwing.Specific.Supplier<T, Exception> supplier) {
		return () -> {
			try {
				return supplier.get();
			} catch (Exception e) {
				throw ExceptionTools.wrap(e);
			}
		};
	}

	public static <T> Supplier<T> tryCatch(Throwing.Specific.Supplier<T, Exception> supplier,
			java.util.function.Function<Exception, T> exceptionHandler) {
		return () -> {
			try {
				return supplier.get();
			} catch (Exception e) {
				return exceptionHandler.apply(e);
			}
		};
	}

	public static <T> Consumer<T> tryCatchConsumer(Throwing.Specific.Consumer<T, Exception> consumer) {
		return (x) -> {
			try {
				consumer.accept(x);
			} catch (Exception e) {
				throw ExceptionTools.wrap(e);
			}
		};
	}

	public static <T> Consumer<T> tryCatchConsumer(Throwing.Specific.Consumer<T, Exception> consumer,
			java.util.function.Function<Exception, T> exceptionHandler) {
		return (x) -> {
			try {
				consumer.accept(x);
			} catch (Exception e) {
				exceptionHandler.apply(e);
			}
		};
	}

	private Functions() {
	}

}
