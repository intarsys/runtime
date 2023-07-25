/*
 * Copyright (c) 2007, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;

// use http://www.asciiflow.com/#Draw to edit
/**
 * This is an alternate implementation for {@link FutureTask}, which is in some
 * cases not flexible enough.
 * 
 * <pre>
 *         +-------------+
 *         |             |       run
 *         | created     +------------------------------+
 *         |             |       #taskStarted           |
 *         +------+------+                              |
 *                |                                     |
 *                |                                     |
 *                |cancel                      +--------v----+
 *                |#taskFailed(TaskCancelled)  |             +-------------------+
 *                |#taskFinally     +----------+ active      +----+              |
 *                |                 |          |             |    |              |
 *                |                 |cancel    +-------------+    |              |
 *                |                 |                             |              |
 *                |                 v                             |#taskFinished |#taskFailed(ex)
 *                |           +----------------+                  |#taskFinally  |#taskFinally
 *                |           |                |                  |              |
 *                |           | cancel pending |                  |              |
 *                |           |                |                  |              |
 *                |           +-----+----------+                  |              |
 *                |                 |                             v              v
 *                v                 |#undo                      +------------+  +------------+
 *         +--------------+         |#taskFailed(TaskCancelled) |            |  |            |
 *         |              |         |#taskFinally               | done       |  | failed     |
 *         | canceled     |&lt;--------+                           |            |  |            |
 *         |              |                                     +------------+  +------------+
 *         +--------------+
 * </pre>
 * 
 * @param <R>
 */
public abstract class AbstractFuture<R> implements Future<R>, ITaskCallbackSupport<R> {

	protected static final ILogger Log = PACKAGE.Log;

	private static final String UNDEFINED = "undefined";

	private TaskCallbackDispatcher<R> callbacks;

	protected final Object lockTask = new Object();

	private Throwable exception;

	private R result;

	protected boolean cancelled;

	protected boolean computed;

	private final Object id;

	protected boolean active;

	private String label;

	protected AbstractFuture() {
		super();
		this.id = createId();
		this.label = UNDEFINED;
		// be sure to call "created" as last statement in your subclass
		// constructor
	}

	protected AbstractFuture(Object id, String label) {
		super();
		this.id = id;
		this.label = label;
		// be sure to call "created" as last statement in your subclass
		// constructor
	}

	protected AbstractFuture(String label) {
		super();
		this.id = createId();
		this.label = label;
		// be sure to call "created" as last statement in your subclass
		// constructor
	}

	/**
	 * Add a {@link ITaskCallback} to get informed when {@link Future} is done.
	 * 
	 * The callbacks are executed *after* local state is changed (so
	 * {@link #isDone()} will always be true).
	 * 
	 * @param callback
	 */
	@Override
	public void addTaskCallback(ITaskCallback<R> callback) {
		boolean runImmediately = false;
		synchronized (lockTask) {
			if (callbacks == null) {
				callbacks = new TaskCallbackDispatcher<>();
			}
			callbacks.addTaskCallback(callback);
			runImmediately = computed || cancelled;
		}
		if (runImmediately) {
			try {
				if (isCancelled()) {
					callback.failed(new TaskCancelled());
				} else if (exception != null) {
					callback.failed(new TaskExecutionException(basicGetException()));
				} else {
					callback.finished(basicGetResult());
				}
			} catch (Exception e) {
				Log.debug("{} callback execution failed", getLabel(), e); //$NON-NLS-1$
			}
		}
	}

	protected Throwable basicGetException() {
		synchronized (lockTask) {
			return exception;
		}
	}

	protected R basicGetResult() {
		synchronized (lockTask) {
			return result;
		}
	}

	@Override
	public boolean cancel(boolean interrupt) {
		synchronized (lockTask) {
			if (cancelled) {
				return false;
			}
			if (computed) {
				Log.debug("{} can't cancel, already computed", getLabel()); //$NON-NLS-1$
				return false;
			}
			Log.debug("{} cancel {} task", getLabel(), active ? "active" : "inactive"); //$NON-NLS-1$
			if (interrupt) {
				checkInterrupt();
			}
			cancelled = true;
			lockTask.notifyAll();
		}
		handleCancel();
		return true;
	}

	protected void checkInterrupt() {
	}

	@SuppressWarnings("java:S4973")
	protected final void created() {
		if (label == UNDEFINED) {
			label = createLabel();
		}
		Log.trace("{} created", getLabel()); //$NON-NLS-1$
	}

	protected Object createId() {
		return ObjectTools.createId(this);
	}

	protected String createLabel() {
		return ObjectTools.createLabel(this, getId());
	}

	@Override
	public R get() throws InterruptedException, ExecutionException {
		try {
			return get(0, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new InterruptedException();
		}
	}

	/**
	 * Wait for the result. The "timeout" parameter allows to
	 * <ul>
	 * <li>wait indefinite &ndash; when timeout == 0;</li>
	 * <li>wait specified duration &ndash; when timeout &gt; 0;</li>
	 * <li>don't wait (peek) &ndash; when timeout &lt; 0;</li>
	 * </ul>
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 * 
	 * @return the computed result
	 * @throws CancellationException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 */
	@Override
	public R get(final long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long remainNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
		long lastNanos = System.nanoTime();
		synchronized (lockTask) {
			while (true) {
				if (cancelled) {
					throw new CancellationException();
				}
				if (computed) {
					if (exception != null) {
						throw new ExecutionException(exception);
					}
					// may be null!
					return result;
				}
				if (timeout >= 0) {
					if (timeout != 0) {
						long nowNanos = System.nanoTime();
						remainNanos -= nowNanos - lastNanos;
						lastNanos = nowNanos;
					}
					long tempMillis = TimeUnit.MILLISECONDS.convert(remainNanos, TimeUnit.NANOSECONDS);
					if (tempMillis <= 0 && timeout != 0) {
						throw new TimeoutException();
					}
					lockTask.wait(tempMillis);
				} else {
					// support a unblocked "get current value"
					return null;
				}
			}
		}
	}

	public Throwable getException() {
		synchronized (lockTask) {
			return exception;
		}
	}

	protected Object getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	protected String getStateString() {
		if (active) {
			return "active";
		}
		if (cancelled) {
			return "cancelled";
		}
		if (computed) {
			return "computed";
		}
		return "";
	}

	protected void handleCancel() {
		if (!active) {
			try {
				taskFailed();
			} finally {
				taskFinally();
			}
		}
	}

	protected final void handleException() {
		try {
			if (cancelled) {
				Log.debug("{} exception after cancel, undo", getLabel()); //$NON-NLS-1$
				undo();
			} else {
				Log.debug("{} exception ({})", getLabel(), ExceptionTools.getMessage(exception)); //$NON-NLS-1$
			}
			try {
				taskFailed();
			} finally {
				taskFinally();
			}
		} catch (Exception e) {
			Log.severe("{} exception in exception handling", getLabel(), e); //$NON-NLS-1$
		}
	}

	protected final void handleResult() {
		try {
			if (cancelled) {
				Log.debug("{} computed after cancel, undo", getLabel()); //$NON-NLS-1$
				undo();
				try {
					taskFailed();
				} finally {
					taskFinally();
				}
			} else {
				Log.debug("{} computed", getLabel()); //$NON-NLS-1$
				try {
					taskFinished();
				} finally {
					taskFinally();
				}
			}
		} catch (Exception e) {
			Log.severe("{} exception in result handling", getLabel(), e); //$NON-NLS-1$
		}
	}

	public boolean isActive() {
		synchronized (lockTask) {
			return active;
		}
	}

	@Override
	public boolean isCancelled() {
		synchronized (lockTask) {
			return cancelled;
		}
	}

	protected boolean isComputed() {
		synchronized (lockTask) {
			return computed;
		}
	}

	@Override
	public boolean isDone() {
		synchronized (lockTask) {
			return computed || cancelled;
		}
	}

	public boolean isFailed() {
		synchronized (lockTask) {
			return exception != null;
		}
	}

	/**
	 * Remove {@link ITaskCallback} from the list of callbacks to get informed
	 * when {@link Future} is done.
	 * 
	 * @param callback
	 */
	@Override
	public void removeTaskCallback(ITaskCallback<R> callback) {
		synchronized (lockTask) {
			if (callbacks == null) {
				return;
			}
			callbacks.removeTaskCallback(callback);
		}
	}

	public void reset() {
		synchronized (lockTask) {
			active = false;
			computed = false;
			exception = null;
			result = null;
		}
	}

	protected void setException(Throwable e) {
		synchronized (lockTask) {
			if (computed) {
				throw new InvalidRequestException("" + this + " already computed");
			}
			computed = true;
			active = false;
			exception = e;
			lockTask.notifyAll();
		}
		handleException();
	}

	protected void setResult(R object) {
		synchronized (lockTask) {
			if (computed) {
				throw new InvalidRequestException("" + this + " already computed");
			}
			computed = true;
			active = false;
			result = object;
			lockTask.notifyAll();
		}
		handleResult();
	}

	/**
	 * This is called whenever task computation failed or the task was
	 * cancelled.
	 */
	protected void taskFailed() {
		if (callbacks != null) {
			if (isCancelled()) {
				callbacks.failed(new TaskCancelled());
			} else {
				callbacks.failed(new TaskExecutionException(basicGetException()));
			}
		}
	}

	/**
	 * This is executed when computation took place, regardless if this task
	 * finished, cancelled or failed, at the end of the task lifecycle.
	 */
	protected void taskFinally() {
		// redefine
	}

	/**
	 * This is called whenever the task finished successfully.
	 */
	protected void taskFinished() {
		if (callbacks != null) {
			callbacks.finished(basicGetResult());
		}
	}

	/**
	 * This is called when the task was scheduled for computation (computation
	 * just started).
	 * 
	 * @throws Exception
	 */
	protected void taskStarted() throws Exception {
		if (callbacks instanceof ITaskListener) {
			((ITaskListener) callbacks).started();
		}
	}

	@Override
	public String toString() {
		return getLabel();
	}

	/**
	 * undo is called in case of a cancellation while computation is already
	 * going on, but not yet finished. The "cancel" state is entered and after
	 * computation (either finished or failed) this method is called to give a
	 * chance to rollback any relevant changes.
	 */
	protected void undo() {
		// redefine
	}

}
