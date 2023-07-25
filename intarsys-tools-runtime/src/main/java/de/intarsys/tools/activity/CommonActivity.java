/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IClassifiable;
import de.intarsys.tools.concurrent.IPromise;
import de.intarsys.tools.concurrent.ITaskCallback;
import de.intarsys.tools.concurrent.Promise;
import de.intarsys.tools.concurrent.SynchronousExecutorService;
import de.intarsys.tools.concurrent.TaskFailed;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.message.PrefixedMessageBundle;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.InvocableArgument;
import de.intarsys.tools.reflect.InvocableMethod;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.state.AtomicState;
import de.intarsys.tools.state.ComplexStateHolder;
import de.intarsys.tools.state.IState;
import de.intarsys.tools.ui.Toolkit;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A common superclass for implementing {@link IActivity}.
 * 
 * @param <R>
 *            The result type of the {@link IActivity}.
 * @param <P>
 *            An optional parent for the activity.
 */
public abstract class CommonActivity<R, P extends IActivity<?>> implements IActivity<R>, IActivityHandler, IPromise<R>,
		IAttributeSupport, IMessageBundleSupport, INotificationSupport, IClassifiable {

	private static final ILogger Log = PACKAGE.Log;

	private final IAttributeSupport attributes = new AttributeMap();

	private final Promise<R> promise;

	private R deferredResult;

	private Throwable deferredException;

	private final String autoid;

	protected final Object lock = new Object();

	private final ComplexStateHolder stateHolder;

	private final EventDispatcher dispatcher;

	private final P parent;

	private final List<CommonActivity<?, ?>> children = new ArrayList<>();

	private IActivityHandler activityHandler;

	private IMessageBundle messageBundle;

	private final ITaskCallback<R> callbackPromise = new ITaskCallback<R>() {

		@Override
		public void failed(TaskFailed exception) {
			try {
				if (exception.isCancellation()) {
					enterState(getStateCancelled());
					onCancelled();
				} else {
					enterState(getStateFailed());
					onFailed(exception.getCause());
				}
			} finally {
				onFinally();
			}
		}

		@Override
		public void finished(R result) {
			try {
				enterState(getStateOK());
				onFinished(result);
			} finally {
				onFinally();
			}
		}
	};

	private final IMessageBundle Msg = PACKAGE.Messages; // NOSONAR

	private boolean modal;

	private boolean block = true;

	private boolean onTop;

	private ExecutorService executorService = createExecutor();

	protected CommonActivity() {
		this(null);
	}

	protected CommonActivity(P parent) {
		super();
		this.autoid = ObjectTools.createLabel(this);
		this.dispatcher = new EventDispatcher(this);
		this.promise = new Promise<>("Promise-" + this.autoid);
		addTaskCallback(callbackPromise);
		this.stateHolder = new ComplexStateHolder(this, dispatcher);
		this.stateHolder.setMessageBundle(
				new PrefixedMessageBundle(getMessageBundle(), ClassTools.getUnqualifiedName(getClass())));
		this.parent = parent;
	}

	@Override
	public <RR> void activityEnter(IActivity<RR> activity) {
		IActivityHandler tempHandler = getActivityHandler();
		if (tempHandler != null) {
			tempHandler.activityEnter(activity);
		}
	}

	protected void activityPublish() {
		activityEnter(this);
	}

	protected void addChildActivity(CommonActivity<?, ?> activity) {
		synchronized (lock) {
			children.add(activity);
		}
	}

	@Override
	public <T extends Event> void addNotificationListener(EventType<? extends T> type,
			INotificationListener<T> listener) {
		dispatcher.addNotificationListener(type, listener);
	}

	@Override
	public void addTaskCallback(ITaskCallback<R> callback) {
		promise.addTaskCallback(callback);
	}

	/**
	 * Redefine this method if there is anything to be done *after* the activity
	 * enters its "active" state and is mapped by its handler.
	 * 
	 * @throws Exception
	 */
	protected void basicEnterAfter() throws Exception {
		// override if necessary
	}

	/**
	 * Redefine this method if there is anything to be done *before* the activity
	 * enters its "active" state.
	 * 
	 * You can acquire resources, initialize state...
	 * 
	 * @throws Exception
	 */
	protected void basicEnterBefore() throws Exception {
		// override if necessary
	}

	protected void basicPublishAfter() {
	}

	protected void basicPublishBefore() {
	}

	public final boolean cancel() {
		return cancel(false);
	}

	@Override
	@InvocableMethod
	public final boolean cancel(@InvocableArgument(name = "interrupt") boolean interrupt) {
		if (isDone()) {
			return false;
		}
		logCancel();
		return promise.cancel(interrupt);
	}

	protected ExecutorService createExecutor() {
		return new SynchronousExecutorService();
	}

	public final boolean deferredCancel() {
		try {
			Log.debug("{} deferred cancel", getLogLabel());
			deferredException = new CancellationException();
			onDeferredCancelled();
			return true;
		} finally {
			onDeferredFinally();
		}
	}

	public final void deferredFail(Throwable t) {
		try {
			Log.debug("{} deferred fail", getLogLabel());
			deferredException = t;
			onDeferredFailed(t);
		} finally {
			onDeferredFinally();
		}
	}

	public final void deferredFinish(R value) {
		try {
			Log.debug("{} deferred finish", getLogLabel());
			deferredResult = value;
			onDeferredFinished(value);
		} finally {
			onDeferredFinally();
		}
	}

	public void deferredRelease() {
		synchronized (lock) {
			if (deferredException != null || deferredResult != null) {
				try {
					finish(getDeferred());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail(e);
				} catch (ExecutionException e) {
					fail(e.getCause());
				} catch (CancellationException e) {
					cancel(false);
				}
			}
			// ??
		}
	}

	@Override
	@InvocableMethod
	public final IActivity<R> enter() {
		try {
			basicEnterBefore();
			enterState(getStateActive());
			if (getParent() != null) {
				if (getParent() instanceof CommonActivity) {
					((CommonActivity<?, ?>) getParent()).addChildActivity(this);
				}
				if (getParent().isDone()) {
					cancel(false);
					return this;
				}
			}
			logEnterAfter();
			basicPublishBefore();
			if (isDone()) {
				return this;
			}
			activityPublish();
			basicPublishAfter();
			basicEnterAfter();
			submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					/*
					 * execute itself should catch all expected exceptions and fail if necessary
					 * catch the unexpected stuff here and log it
					 */
					try {
						execute();
					} catch (Throwable t) {
						Log.log(Level.SEVERE, t.getMessage(), t);
					}
					return null;
				}
			});
		} catch (Exception e) {
			fail(e);
		}
		return this;
	}

	@Override
	public void enterState(IState pState) {
		Log.trace("{} enter state {}", getLogLabel(), pState.getId());
		stateHolder.enterState(pState);
	}

	/**
	 * Redefine this method if the activity will "run" its implementation to end
	 * automatically after being entered.
	 * 
	 * If this method is implemented, it is expected that it will bring the activity
	 * to a final state, e.g. via call to "ok()", "cancel()" or "fail()".
	 * 
	 * @throws Exception
	 */
	protected void execute() throws Exception {
		// override if needed
	}

	/**
	 * @param t
	 */
	@Override
	public final void fail(Throwable t) {
		if (isDone()) {
			return;
		}
		Throwable cause = ExceptionTools.unwrap(t);
		if (ExceptionTools.isCancellation(cause)) {
			cancel();
			return;
		}
		logFail(cause);
		promise.fail(cause);
	}

	/**
	 * Synonym for fail, may be used in scripting clients
	 * 
	 * @param t
	 */
	public final void failed(Throwable t) {
		fail(t);
	}

	@Override
	@InvocableMethod
	public final void finish(@InvocableArgument(name = "value") R value) {
		if (!validate(value)) {
			logValidationFailure();
			return;
		}
		logFinish();
		promise.finish(value);
	}

	@Override
	public final R get() throws InterruptedException, ExecutionException {
		return promise.get();
	}

	@Override
	public final R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return promise.get(timeout, unit);
	}

	public IActivityHandler getActivityHandler() {
		if (activityHandler == null) {
			if (getParent() instanceof IActivityHandler) {
				return (IActivityHandler) getParent();
			}
			if (Toolkit.get() instanceof IActivityHandler) {
				return (IActivityHandler) Toolkit.get();
			}
		}
		return activityHandler;
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	protected String getAutoid() {
		return autoid;
	}

	public List<CommonActivity<?, ?>> getChildren() {
		synchronized (lock) {
			return new ArrayList<>(children);
		}
	}

	@InvocableMethod
	@Override
	public String getClassifier() {
		return getClass().getName();
	}

	protected IMessageBundle getDefaultMessageBundle() {
		if (getParent() instanceof IMessageBundleSupport) {
			return ((IMessageBundleSupport) getParent()).getMessageBundle();
		}
		return Msg;
	}

	protected R getDefaultResult() {
		return null;
	}

	public final R getDeferred() throws InterruptedException, ExecutionException {
		synchronized (lock) {
			if (deferredException != null) {
				if (deferredException instanceof CancellationException) {
					throw (CancellationException) deferredException;
				}
				throw new ExecutionException(deferredException);
			}
			return deferredResult;
		}
	}

	/**
	 * @param timeout
	 *            The timeout value
	 * @param unit
	 *            The timeout unit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public final R getDeferred(long timeout, TimeUnit unit) // NOSONAR
			throws InterruptedException, ExecutionException, TimeoutException {
		synchronized (lock) {
			if (deferredException != null) {
				throw new ExecutionException(deferredException);
			}
			return deferredResult;
		}
	}

	protected EventDispatcher getDispatcher() {
		return dispatcher;
	}

	public final Throwable getException() {
		try {
			promise.get();
			return null;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new CancellationException();
		} catch (ExecutionException e) {
			return e.getCause();
		}
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public String getLogLabel() {
		return autoid;
	}

	@Override
	public final IMessageBundle getMessageBundle() {
		if (messageBundle == null) {
			return getDefaultMessageBundle();
		}
		return messageBundle;
	}

	public P getParent() {
		return parent;
	}

	protected Promise<R> getPromise() {
		return promise;
	}

	public final String getStackTrace() {
		Throwable t = getException();
		if (t == null) {
			return null;
		}
		return ExceptionTools.getStackTraceString(t);
	}

	@Override
	public IState getState() {
		return stateHolder.getState();
	}

	protected AtomicState getStateActive() {
		return AtomicState.ACTIVE;
	}

	protected AtomicState getStateCancelled() {
		return AtomicState.CANCELLED;
	}

	protected AtomicState getStateFailed() {
		return AtomicState.FAILED;
	}

	protected AtomicState getStateOK() {
		return AtomicState.OK;
	}

	public boolean isBlock() {
		return block;
	}

	@Override
	public final boolean isCancelled() {
		return promise.isCancelled();
	}

	public final boolean isDeferredCancelled() {
		return deferredException instanceof CancellationException;
	}

	public final boolean isDeferredDone() {
		return deferredResult != null || deferredException != null;
	}

	@Override
	public final boolean isDone() {
		return promise.isDone();
	}

	public final boolean isFailed() {
		return promise.isFailed();
	}

	public boolean isModal() {
		return modal;
	}

	public boolean isOnTop() {
		return onTop;
	}

	protected void logCancel() {
		Log.debug("{} cancel", getLogLabel());
	}

	protected void logEnterAfter() {
		Log.debug("{} entered", getLogLabel());
	}

	protected void logFail(Throwable t) {
		Log.debug("{} fail {}", getLogLabel(), ExceptionTools.getMessage(t));
	}

	protected void logFinish() {
		Log.trace("{} finish", getLogLabel());
	}

	protected void logValidationFailure() {
		Log.trace("{} validation failure", getLogLabel());
	}

	@InvocableMethod
	public void ok() {
		finish(getDefaultResult());
	}

	protected void onCancelled() {
		List<IActivity<?>> tempChildren;
		synchronized (lock) {
			tempChildren = new ArrayList<>(children);
		}
		for (IActivity<?> child : tempChildren) {
			child.cancel(false);
		}
	}

	protected void onDeferredCancelled() {
	}

	protected void onDeferredFailed(Throwable t) {
	}

	protected void onDeferredFinally() {
	}

	protected void onDeferredFinished(R result) {
	}

	protected void onFailed(Throwable t) {
		List<CommonActivity<?, ?>> tempChildren;
		synchronized (lock) {
			tempChildren = new ArrayList<>(children);
		}
		for (CommonActivity<?, ?> child : tempChildren) {
			child.fail(t);
		}
	}

	protected void onFinally() {
		onFinallyRelease();
		if (getParent() != null) {
			if (getParent() instanceof CommonActivity) {
				((CommonActivity<?, ?>) getParent()).removeChildActivity(this);
			}
		}
	}

	protected void onFinallyRelease() {
		if (dispatcher != null) {
			dispatcher.clear();
		}
	}

	protected void onFinished(R result) {
	}

	public R peek() throws ExecutionException {
		if (promise.isDone()) {
			try {
				return promise.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new ExecutionException(e);
			}
		}
		return null;
	}

	@Override
	public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	protected void removeChildActivity(IActivity<?> activity) {
		synchronized (lock) {
			children.remove(activity);
		}
	}

	@Override
	public <T extends Event> void removeNotificationListener(EventType<? extends T> type,
			INotificationListener<T> listener) {
		dispatcher.removeNotificationListener(type, listener);
	}

	@Override
	public void removeTaskCallback(ITaskCallback<R> callback) {
		promise.removeTaskCallback(callback);
	}

	public void setActivityHandler(IActivityHandler activityHandler) {
		this.activityHandler = activityHandler;
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return attributes.setAttribute(key, value);
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setMessageBundle(IMessageBundle messageBundle) {
		this.messageBundle = messageBundle;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}

	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}

	protected void submit(Callable<Void> callable) {
		getExecutorService().submit(callable);
	}

	/**
	 * Synonym for finish, maybe used in scripting clients
	 * 
	 * @param value
	 */
	public final void success(R value) {
		finish(value);
	}

	@Override
	public String toString() {
		return getLogLabel();
	}

	protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		dispatcher.triggerChanged(attribute, oldValue, newValue);
	}

	protected void triggerEvent(Event event) {
		dispatcher.triggerEvent(event);
	}

	/**
	 * @param value
	 *            The result value to validate.
	 * @return
	 */
	protected boolean validate(R value) {
		return true;
	}
}
