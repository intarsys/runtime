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
package de.intarsys.tools.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.reflect.ClassTools;

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
 *                |#taskCancelled              |             +-------------------+
 *                |                 +----------+ active      +----+              |
 *                |                 |          |             |    |              |
 *                |                 |cancel    +-------------+    |              |
 *                |                 |                             |              |
 *                |                 v                             |#taskFinished |#taskFailed
 *                |           +----------------+                  |#taskFinally  |#taskFinally
 *                |           |                |                  |              |
 *                |           | cancel pending |                  |              |
 *                |           |                |                  |              |
 *                |           +-----+----------+                  |              |
 *                |                 |                             v              |
 *                v                 |#undo                  +------------+  +----v-------+
 *         +--------------+         |#taskCancelled         |            |  |            |
 *         |              |         |                       | done       |  | failed     |
 *         | cancelled    |<--------+                       |            |  |            |
 *         |              |                                 +------------+  +------------+
 *         +--------------+
 * </pre>
 * 
 * @param <R>
 */
abstract public class AbstractFutureTask<R> implements Runnable, Future<R> {

	protected final static Logger Log = PACKAGE.Log;

	private ITaskListener taskListener;

	static private int COUNTER = 0;

	final private Object lockTask = new Object();

	private Throwable exception;

	private R result;

	private boolean cancelled = false;

	private boolean computed = false;

	final private int id = COUNTER++;

	private boolean active = false;

	private boolean asynch = false;

	final private String label = ClassTools.getUnqualifiedName(getClass())
			+ "-" + id;

	protected AbstractFutureTask() {
		super();
	}

	protected AbstractFutureTask(ITaskListener callback) {
		super();
		this.taskListener = callback;
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
		boolean tempActive;
		synchronized (lockTask) {
			if (cancelled) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " can't cancel, already canceled"); //$NON-NLS-1$
				}
				return false;
			}
			if (computed) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " can't cancel, already computed"); //$NON-NLS-1$
				}
				return false;
			}
			if (Log.isLoggable(Level.FINEST)) {
				Log.finest("" + this + " cancel " + (active ? "active" : "inactive") + " task"); //$NON-NLS-1$
			}
			tempActive = active;
			cancelled = true;
			lockTask.notifyAll();
		}
		if (!tempActive) {
			taskCancelled();
		}
		return true;
	}

	protected abstract R compute() throws Exception;

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
	 * <li>wait indefinite</li> when timeout == 0;
	 * <li>wait specified duration</li> when timeout > 0;
	 * <li>don't wait (peek)</li> when timeout < 0;
	 * </ul>
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public R get(final long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
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
					long tempMillis = TimeUnit.MILLISECONDS.convert(
							remainNanos, TimeUnit.NANOSECONDS);
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

	public String getLabel() {
		return label;
	}

	public ITaskListener getTaskListener() {
		synchronized (lockTask) {
			return taskListener;
		}
	}

	final protected void handleException() {
		try {
			if (cancelled) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " exception undo"); //$NON-NLS-1$
				}
				undo();
				taskCancelled();
			} else {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " exception"); //$NON-NLS-1$
				}
				try {
					taskFailed();
				} finally {
					handleFinally();
				}
			}
		} catch (Exception e) {
			Log.log(Level.SEVERE,
					"" + this + " exception in exception handling", e); //$NON-NLS-1$
		}
	}

	final protected void handleFinally() {
		try {
			if (Log.isLoggable(Level.FINEST)) {
				Log.finest("" + this + " finally"); //$NON-NLS-1$
			}
			taskFinally();
		} catch (Exception e) {
			Log.log(Level.SEVERE,
					"" + this + " exception in finally handling", e); //$NON-NLS-1$
		}
	}

	final protected void handleResult() {
		try {
			if (cancelled) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " computed undo"); //$NON-NLS-1$
				}
				undo();
				taskCancelled();
			} else {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " computed"); //$NON-NLS-1$
				}
				try {
					taskFinished();
				} finally {
					handleFinally();
				}
			}
		} catch (Exception e) {
			Log.log(Level.SEVERE,
					"" + this + " exception in result handling", e); //$NON-NLS-1$
		}
	}

	public boolean isActive() {
		synchronized (lockTask) {
			return active;
		}
	}

	public boolean isAsynch() {
		return asynch;
	}

	@Override
	public boolean isCancelled() {
		synchronized (lockTask) {
			return cancelled;
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

	public void reset() {
		synchronized (lockTask) {
			active = false;
			computed = false;
			exception = null;
			result = null;
		}
	}

	@Override
	final public void run() {
		synchronized (lockTask) {
			if (active || cancelled || computed) {
				if (Log.isLoggable(Level.FINEST)) {
					Log.finest("" + this + " will not run" + (cancelled ? " (canceled)" : "")); //$NON-NLS-1$
				}
				return;
			}
			active = true;
		}
		try {
			taskStarted();
			R tempResult = compute();
			if (!isAsynch()) {
				setResult(tempResult);
			}
		} catch (Throwable e) {
			setException(e);
		}
	}

	final public void runAsync() {
		setAsynch(true);
		run();
	}

	public void setAsynch(boolean asynch) {
		this.asynch = asynch;
	}

	protected void setException(Throwable e) {
		synchronized (lockTask) {
			computed = true;
			active = false;
			exception = e;
			lockTask.notifyAll();
		}
		handleException();
	}

	protected void setResult(R object) {
		synchronized (lockTask) {
			computed = true;
			active = false;
			result = object;
			lockTask.notifyAll();
		}
		handleResult();
	}

	public void setTaskListener(ITaskListener taskListener) {
		synchronized (lockTask) {
			this.taskListener = taskListener;
		}
	}

	/**
	 * This is executed when this task is cancelled.
	 * 
	 * Execution starts either directly after the call to "cancel" if this task
	 * is not yet started. If this task is already active but not yet computed,
	 * the computation goes on. "taskCancelled" is called after undoing the
	 * computation via "undo". If the task was already computed, it will never
	 * be cancelled.
	 * 
	 */
	protected void taskCancelled() {
		if (taskListener != null) {
			taskListener.taskCancelled(this);
		}
	}

	/**
	 * This is called whenever task computation failed and the task was not
	 * cancelled.
	 */
	protected void taskFailed() {
		if (taskListener != null) {
			taskListener.taskFailed(this, new ExecutionException(
					basicGetException()));
		}
	}

	/**
	 * This is executed when computation took place, regardless if this task
	 * finished or failed, at the end of the task lifecycle.
	 * 
	 * This is not called upon cancellation!!
	 */
	protected void taskFinally() {
		// redefine
	}

	/**
	 * This is called whenever the task finished successfully and is not
	 * cancelled.
	 */
	protected void taskFinished() {
		if (taskListener != null) {
			taskListener.taskFinished(this, basicGetResult());
		}
	}

	/**
	 * This is called when the task was scheduled for computation (computation
	 * just started).
	 * 
	 * @throws Exception
	 */
	protected void taskStarted() throws Exception {
		if (taskListener != null) {
			taskListener.taskStarted(this);
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
	 * chance to rollback any relevant changes. "taskCancelled" is then called
	 * afterwards.
	 */
	protected void undo() {
		// redefine
	}

}
