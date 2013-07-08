/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Tool class for dealing with Exceptions.
 */
public class ExceptionTools {
	/**
	 * @deprecated Java 6 has suitable constructor and we don't support Java 5 anymore. 
	 */
	@Deprecated
	static public IOException createIOException(String message, Throwable cause) {
		IOException ioe = new IOException(message);
		ioe.initCause(cause);
		return ioe;
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
			return null;
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof Error) {
				throw (Error) ex.getCause();
			}
			if (ex.getCause() instanceof RuntimeException) {
				throw (RuntimeException) ex.getCause();
			}
			throw new InternalError(
					"Program execution should not reach this point.");
		}
	}

	public static <T extends Number> T futureSimpleGetNumber(Future<T> future) {
		T result = futureSimpleGet(future);
		if (result == null) {
			result = (T) new Integer(-1);
		}
		return result;
	}

	static public <T extends Throwable> T getInChain(Throwable t, Class<T> clazz) {
		if (clazz.isInstance(t)) {
			return (T) t;
		}
		if (t.getCause() != null) {
			return getInChain(t.getCause(), clazz);
		}
		return null;
	}

	/**
	 * The root cause of <code>t</code>.
	 * 
	 * @param t
	 *            A {@link Throwable}.
	 * @return The most inner cause of <code>t</code>.
	 */
	static public Throwable getRoot(Throwable t) {
		Throwable root = t;
		while (root.getCause() != null) {
			root = root.getCause();
		}
		return root;
	}

	static public String getStackTraceString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	static public boolean isInChain(Throwable t, Class<?> clazz) {
		if (clazz.isInstance(t)) {
			return true;
		}
		if (t.getCause() != null) {
			return isInChain(t.getCause(), clazz);
		}
		return false;
	}

	public static boolean isKnownReason(Throwable t) {
		if (t instanceof KnownReason) {
			return true;
		}
		Throwable cause = t.getCause();
		if (cause != null) {
			return isKnownReason(cause);
		}
		return false;
	}

	public static boolean isTimeout(Throwable t) {
		if (t instanceof TimeoutException) {
			return true;
		}
		Throwable cause = t.getCause();
		if (cause != null) {
			return isTimeout(cause);
		}
		return false;
	}

	static public <T extends Throwable> T createWithCause(Class<T> clazz,
			Throwable t) {
		Throwable cause = t.getCause() == null ? t : t.getCause();
		if (clazz.isInstance(cause)) {
			return (T) cause;
		}
		try {
			Constructor<T> c = clazz.getConstructor(Throwable.class);
			T newThrowable = c.newInstance(clazz);
			return newThrowable;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"failed creating exception type " + clazz.getName(), t);
		}
	}
}
