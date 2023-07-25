/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import de.intarsys.tools.component.ComponentTargetException;
import de.intarsys.tools.concurrent.TaskExecutionException;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCancelledException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.MethodExecutionException;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * Tool class for dealing with Exceptions.
 */
public final class ExceptionTools {

	private static final String SUFFIX_EXCEPTION = "Exception";

	private static final Set<Class<?>> WrappingExceptions = new HashSet<>();

	static {
		WrappingExceptions.add(CompletionException.class);
		WrappingExceptions.add(ExecutionException.class);
		WrappingExceptions.add(TaskExecutionException.class);
		WrappingExceptions.add(FunctorExecutionException.class);
		WrappingExceptions.add(MethodExecutionException.class);
		WrappingExceptions.add(ObjectCreationException.class);
		WrappingExceptions.add(InvocationTargetException.class);
		WrappingExceptions.add(ComponentTargetException.class);
		WrappingExceptions.add(UncheckedIOException.class);
		WrappingExceptions.add(TunnelingException.class);
	}

	/**
	 * Provide a standard formatted exception for an invalid argument content.
	 * 
	 * @param argument
	 *            The argument name
	 * @param message
	 *            An optional message that is appended to the exception message
	 * @param t
	 *            An optional exception that caused the failure.
	 * @return
	 */
	public static IllegalArgumentException argumentInvalid(String argument, String message, Throwable t) {
		if (StringTools.isEmpty(message)) {
			return new IllegalArgumentException(String.format("Argument %s invalid", argument), t);
		} else {
			return new IllegalArgumentException(String.format("Argument %s invalid, %s", argument, message), t);
		}
	}

	/**
	 * Provide a standard formatted exception for an argument that is not provided.
	 * 
	 * @param argument
	 *            The argument name
	 * @return
	 */
	public static IllegalArgumentException argumentRequired(String argument) {
		return new IllegalArgumentException(String.format("Argument %s required", argument));
	}

	/**
	 * @deprecated use {@link #wrap(Throwable)}
	 */
	@Deprecated
	public static RuntimeException asRuntime(String msg, Throwable t) {
		return wrap(t);
	}

	/**
	 * @deprecated use {@link #wrap(Throwable)}
	 */
	@Deprecated
	public static RuntimeException asRuntime(Throwable t) {
		return wrap(t);
	}

	/**
	 * Create an exception of type {@code clazz} (via reflection), setting {@code t} as its cause.
	 * If {@code t} is already of the appropriate type, {@code t} is returned.
	 * 
	 */
	public static <T extends Throwable> T createTyped(Throwable t, Class<T> clazz) {
		if (clazz.isInstance(t)) {
			return (T) t;
		}
		try {
			Constructor<T> c = clazz.getConstructor(String.class, Throwable.class);
			return c.newInstance(ExceptionTools.getMessageShort(t), t);
		} catch (Exception e) {
			try {
				Constructor<T> c = clazz.getConstructor(Throwable.class);
				return c.newInstance(t);
			} catch (Exception e2) {
				throw new IllegalArgumentException("failed creating exception type " + clazz.getName(), t);
			}
		}
	}

	/**
	 * Create an exception of type {@code clazz} based on the cause chain of {@code t}.
	 * 
	 * If {@code clazz} is an exception that is available in the chain (including {@code t}), this exception is
	 * returned. If not, a new exception of type {@code clazz} with {@code t} as its cause is created.
	 * 
	 */
	public static <T extends Throwable> T createTypedFromChain(Throwable t, Class<T> clazz) {
		T inChain = getFromChain(t, clazz);
		if (inChain != null) {
			return inChain;
		}
		return createTyped(t, clazz);
	}

	/**
	 * Simply fail with a {@link RuntimeException}.
	 */
	public static void fail() {
		throw new RuntimeException("failed"); //$NON-NLS-1$
	}

	public static <T> T futureSimpleGet(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw ExceptionTools.wrap(e);
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof Error) {
				throw (Error) ex.getCause();
			}
			if (ex.getCause() instanceof RuntimeException) {
				throw (RuntimeException) ex.getCause();
			}
			throw new InternalError("Program execution should not reach this point.");
		}
	}

	public static <T extends Number> T futureSimpleGetNumber(Future<T> future) {
		T result = futureSimpleGet(future);
		if (result == null) {
			result = (T) Integer.valueOf(-1);
		}
		return result;
	}

	public static <T extends Throwable> T getCause(Throwable e, Class<T> exceptionType, Function<Throwable, T> wrap) {
		Throwable cause = e.getCause();
		if (cause == null) {
			return wrap.apply(e);
		}
		if (exceptionType.isAssignableFrom(cause.getClass())) {
			return (T) cause;
		}
		return wrap.apply(cause);
	}

	/**
	 * Get a usable code for the exception.
	 * 
	 */
	public static String getCode(Throwable t) {
		if (t instanceof ICodeException) {
			return ((ICodeException) t).getCode();
		}
		String tmpCode = ClassTools.getUnqualifiedName(t.getClass());
		if (tmpCode.endsWith(SUFFIX_EXCEPTION)) {
			tmpCode = tmpCode.substring(0, tmpCode.length() - SUFFIX_EXCEPTION.length());
		}
		return tmpCode;
	}

	/**
	 * Return an exception of type {@code clazz} from the chain of {@code t} (including {@code t}) if
	 * available, null otherwise.
	 * 
	 */
	public static <T extends Throwable> T getFromChain(Throwable t, Class<T> clazz) {
		if (clazz.isInstance(t)) {
			return (T) t;
		}
		if (t.getCause() != null) {
			return getFromChain(t.getCause(), clazz);
		}
		return null;
	}

	/**
	 * Get a usable "one liner" for the exception.
	 * 
	 * The result consists of the code and the message of the exception.
	 */
	public static String getMessage(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCode(t));
		String tempMsg = t.getMessage();
		if (tempMsg != null) {
			sb.append(" - ");
			sb.append(tempMsg);
		}
		return sb.toString();
	}

	/**
	 * Get a usable "one liner" for the exception.
	 * 
	 * The result is the message of the exception or some replacement if no message is availabale.
	 */
	public static String getMessageShort(Throwable t) {
		String tempMsg = t.getMessage();
		if (!StringTools.isEmpty(tempMsg)) {
			return tempMsg;
		}
		return getMessage(t);
	}

	/**
	 * The root cause of <code>t</code>.
	 * 
	 * @param t
	 *            A {@link Throwable}.
	 * @return The most inner cause of <code>t</code>.
	 */
	public static Throwable getRoot(Throwable t) {
		Throwable root = t;
		while (root.getCause() != null) {
			root = root.getCause();
		}
		return root;
	}

	/**
	 * The stacktrace output of t as String.
	 * 
	 * @param t
	 * @return
	 */
	public static String getStackTraceString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static boolean isCancellation(Throwable t) {
		if (t == null) {
			return false;
		}
		if (isInChain(t, FunctorCancelledException.class)) {
			return true;
		}
		if (isInChain(t, CancellationException.class)) {
			return true;
		}
		if (isInChain(t, ICancellationException.class)) {
			return true;
		}
		return isInChain(t, InterruptedException.class);
	}

	/**
	 * true if an exception of type clazz is in the chain of t (including t).
	 * 
	 * @param t
	 * @param clazz
	 * @return
	 */
	public static boolean isInChain(Throwable t, Class<?> clazz) {
		if (t == null) {
			return false;
		}
		if (clazz.isInstance(t)) {
			return true;
		}
		if (t.getCause() != null) {
			return isInChain(t.getCause(), clazz);
		}
		return false;
	}

	/**
	 * true if an exception with classname is in the chain of t (including t).
	 * 
	 * @param t
	 * @param classname
	 * @return
	 */
	public static boolean isInChain(Throwable t, String classname) {
		if (t == null) {
			return false;
		}
		if (t.getClass().getName().equals(classname)) {
			return true;
		}
		if (t.getCause() != null) {
			return isInChain(t.getCause(), classname);
		}
		return false;
	}

	/**
	 * true if an exception of type {@link KnownReason} is in the chain of t
	 * (including t).
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isKnownReason(Throwable t) {
		return isInChain(t, KnownReason.class);
	}

	/**
	 * true if an exception of type {@link TimeoutException} is in the chain of t
	 * (including t).
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isTimeout(Throwable t) {
		return isInChain(t, TimeoutException.class);
	}

	/**
	 * Create an {@link IArgs} structure from a {@link Throwable}.
	 * 
	 * This can be used to prepare serialization.
	 * 
	 * @param t
	 * @return
	 */
	public static IArgs toArgs(Throwable t, boolean recursive, boolean includeStacktrace) {
		IArgs args = Args.create();
		args.put("_class", t.getClass().getName());
		args.put("message", t.getMessage());
		if (includeStacktrace) {
			args.put("stacktrace", getStackTraceString(t));
		}
		if (recursive && t.getCause() != null) {
			args.put("cause", toArgs(t.getCause(), recursive, includeStacktrace));
		}
		return args;
	}

	/**
	 * Follow the chain of causes to the first non-wrapping exception and return it.
	 * 
	 */
	public static Throwable unwrap(Throwable t) {
		if (t == null) {
			return null;
		}
		if (WrappingExceptions.contains(t.getClass())) {
			Throwable cause = t.getCause();
			return cause == null ? t : unwrap(cause);
		}
		return t;
	}

	/**
	 * @deprecated Use {@link #unwrapTypedFromChain(Throwable, Class)} instead
	 */
	@Deprecated
	public static <T extends Throwable> T unwrap(Throwable t, Class<T> clazz) {
		return unwrapTypedFromChain(t, clazz);
	}

	/**
	 * Unwrap {@code t} and the create new exception of type {@code clazz} from it (or its chain).
	 * 
	 * @param t
	 * @return
	 */
	public static <T extends Throwable> T unwrapTypedFromChain(Throwable t, Class<T> clazz) {
		return ExceptionTools.createTypedFromChain(ExceptionTools.unwrap(t), clazz);
	}

	/**
	 * Create a new exception with same type and stack as {@code t} with the new {@code message}.
	 * 
	 */
	public static Throwable withMessage(Throwable t, String message) {
		try {
			Class clazz = t.getClass();
			Constructor c = clazz.getConstructor(String.class, Throwable.class);
			return (Throwable) c.newInstance(message, t);
		} catch (Exception e) {
			return t;
		}
	}

	/**
	 * Create a new exception with same type and stack as {@code t} and add the prefix {@code prefix} to the message.
	 * 
	 */
	public static Throwable withMessagePrefix(Throwable t, String prefix) {
		String message = prefix + ExceptionTools.getMessageShort(t);
		return withMessage(t, message);
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary.
	 * 
	 */
	public static RuntimeException wrap(Throwable t) {
		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else if (t instanceof IOException) {
			return new UncheckedIOException((IOException) t);
		} else {
			return new TunnelingException(t);
		}
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after creating an exception of type {@code clazz}
	 * out of it.
	 * 
	 * @deprecated Use {@link #wrapTyped(Throwable,Class)} instead
	 * 
	 */
	@Deprecated
	public static RuntimeException wrap(Throwable t, Class clazz) {
		return wrapTyped(t, clazz);
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after creating or pulling an exception of type
	 * {@code clazz} out of it (its chain).
	 * 
	 * @deprecated Use {@link #wrapTypedFromChain(Throwable,Class)} instead
	 * 
	 */
	@Deprecated
	public static RuntimeException wrapFromChain(Throwable t, Class clazz) {
		return wrapTypedFromChain(t, clazz);
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after creating an exception of type {@code clazz}
	 * out of it.
	 * 
	 */
	public static RuntimeException wrapTyped(Throwable t, Class clazz) {
		return wrap(createTyped(t, clazz));
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after creating or pulling an exception of type
	 * {@code clazz} out of it (its chain).
	 * 
	 */
	public static RuntimeException wrapTypedFromChain(Throwable t, Class clazz) {
		return wrap(createTypedFromChain(t, clazz));
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after re-creating {@code t} with a new message.
	 * 
	 */
	public static RuntimeException wrapWithMessage(Throwable t, String message) {
		return wrap(withMessage(t, message));
	}

	/**
	 * Wrap {@code t} into a {@link RuntimeException} if necessary, after creating or pulling an exception of type
	 * {@code clazz} out of it (its chain).
	 * 
	 */
	public static RuntimeException wrapWithMessagePrefix(Throwable t, String prefix) {
		return wrap(withMessagePrefix(t, prefix));
	}

	private ExceptionTools() {
	}
}
