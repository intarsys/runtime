/*
 * Copyright (c) 2012, intarsys GmbH
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
package de.intarsys.tools.ui;

import java.util.ArrayDeque;
import java.util.Deque;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.IActivityClient;
import de.intarsys.tools.activity.IActivityHandler;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.concurrent.IExecutionDecorator;
import de.intarsys.tools.concurrent.ITaskCallback;
import de.intarsys.tools.concurrent.TaskFailed;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * Abstraction of an user interface component.
 * <p>
 * The {@link UIComponent} spans a hierarchical tree. Each {@link UIComponent}
 * maps to a real toolkit interface component that gets realized upon calling
 * "createComponent".
 * <p>
 * A {@link UIComponent} is associated with a model object via an
 * {@link IValueHolder}.
 * 
 * @param <M>
 *            The model object
 * @param <C>
 *            The toolkit container class
 * @param <T>
 *            The toolkit component class
 */
public abstract class UIComponent<M, C, T> implements IUIComponent<M, C, T>, IElementConfigurable {

	private class LocalValueHolder implements IValueHolder<M> {

		private M value;

		LocalValueHolder(M value) {
			super();
			set(value);
		}

		@Override
		public M get() {
			return value;
		}

		@Override
		public M set(M newValue) {
			M oldValue = value;
			if (oldValue == newValue) {
				return value;
			}
			disarmModel(oldValue);
			armModel(newValue);
			value = newValue;
			basicSetValue(value);
			return oldValue;
		}
	}

	protected abstract class UIComponentActivityHandler implements IActivityHandler {

		protected final void activityChanged(final IActivity<?> activity, final AttributeChangedEvent event) {
			if (!isUiActive()) {
				return;
			}
			syncActivityChanged(activity, event);
			Toolkit.get().invokeInUI(decorate(new Runnable() {
				@Override
				public void run() {
					if (!isUiActive()) {
						return;
					}
					Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
					uiActivityChanged(activity, event);
				}

				@Override
				public String toString() {
					return "activityChanged " + activity;
				}
			}));
		}

		@Override
		public final <R> void activityEnter(IActivity<R> activity) {
			if (!isUiActive()) {
				return;
			}
			if (activity instanceof INotificationSupport) {
				((INotificationSupport) activity).addNotificationListener(AttributeChangedEvent.ID,
						new INotificationListener<AttributeChangedEvent>() {
							@Override
							public void handleEvent(AttributeChangedEvent event) {
								activityChanged(activity, event);
							}
						});
			}
			activity.addTaskCallback(new ITaskCallback<R>() {
				@Override
				public void failed(TaskFailed exception) {
					try {
						activityFailed(activity);
					} finally {
						activityFinally(activity);
					}
				}

				@Override
				public void finished(R result) {
					try {
						activityFinished(activity);
					} finally {
						activityFinally(activity);
					}
				}
			});
			syncActivityEnter(activity);
			Toolkit.get().invokeInUI(decorate(new Runnable() {
				@Override
				public void run() {
					if (!isUiActive()) {
						return;
					}
					Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
					uiActivityEnter(activity);
					activityStack.push(activity);
				}

				@Override
				public String toString() {
					return "activityEnter " + activity;
				}
			}));
		}

		protected final void activityFailed(final IActivity<?> activity) {
			if (!isUiActive()) {
				return;
			}
			syncActivityFailed(activity);
			Toolkit.get().invokeInUI(decorate(new Runnable() {
				@Override
				public void run() {
					if (!isUiActive()) {
						return;
					}
					Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
					uiActivityFailed(activity);
				}

				@Override
				public String toString() {
					return "activityFailed " + activity;
				}
			}));
		}

		protected final void activityFinally(final IActivity<?> activity) {
			if (!isUiActive()) {
				return;
			}
			syncActivityFinally(activity);
			Toolkit.get().invokeInUI(decorate(new Runnable() {
				@Override
				public void run() {
					activityStack.remove(activity);
					if (!isUiActive()) {
						return;
					}
					Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
					uiActivityFinally(activity);
				}

				@Override
				public String toString() {
					return "activityFinally " + activity;
				}
			}));
		}

		protected final void activityFinished(final IActivity<?> activity) {
			if (!isUiActive()) {
				return;
			}
			syncActivityFinished(activity);
			Toolkit.get().invokeInUI(decorate(new Runnable() {
				@Override
				public void run() {
					if (!isUiActive()) {
						return;
					}
					Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
					uiActivityFinished(activity);
				}

				@Override
				public String toString() {
					return "activityFinished " + activity;
				}
			}));
		}

		public void armModel(Object value) {
			if (value instanceof IActivityClient) {
				((IActivityClient) value).setActivityHandler(this);
			}
		}

		protected void syncActivityChanged(IActivity<?> activity, AttributeChangedEvent event) {
		}

		protected void syncActivityEnter(final IActivity<?> activity) {
			//
		}

		protected void syncActivityFailed(IActivity<?> activity) {
			//
		}

		protected void syncActivityFinally(final IActivity<?> activity) {
			//
		}

		protected void syncActivityFinished(IActivity<?> activity) {
			//
		}

		protected void uiActivityChanged(final IActivity<?> activity, final AttributeChangedEvent event) {
			//
		}

		protected void uiActivityEnter(final IActivity<?> activity) {
			//
		}

		protected void uiActivityFailed(IActivity<?> activity) {
			//
		}

		protected void uiActivityFinally(final IActivity<?> activity) {
			//
		}

		protected void uiActivityFinished(IActivity<?> activity) {
			//
		}
	}

	private static final String LOG_TEMPLATE = "UI {}";

	private static final ILogger Log = PACKAGE.Log;

	private UIComponentActivityHandler activityHandler;

	private C container;

	private final IUIComponent parent;

	private T component;

	private final IValueHolder<M> valueHolder;

	private final INotificationListener<AttributeChangedEvent> listenModelChange = new INotificationListener<AttributeChangedEvent>() {
		@Override
		public void handleEvent(final AttributeChangedEvent event) {
			modelChanged(event);
		}
	};

	private IElement configuration;

	private boolean componentCreated;

	private boolean disposed;
	private boolean suspended;

	private final Object runtimeStateLock = new Object();
	private final Deque<IActivity<?>> activityStack = new ArrayDeque<>();

	protected UIComponent(IUIComponent<?, C, T> parent) {
		super();
		this.parent = parent;
		this.valueHolder = new LocalValueHolder(null);
	}

	protected void armModel(Object value) {
		if (value instanceof INotificationSupport) {
			((INotificationSupport) value).addNotificationListener(AttributeChangedEvent.ID, listenModelChange);
		}
		if (activityHandler != null) {
			activityHandler.armModel(value);
		}
	}

	protected void basicAssociateComponent() {
		//
	}

	protected abstract T basicCreateComponent(C parent);

	protected void basicDispose() {
		//
	}

	protected void basicResume() {
		//
	}

	/**
	 * @param value
	 *            The new value for the component
	 */
	protected void basicSetValue(M value) {
		if (getComponent() != null) {
			callUpdateView(null);
		}
	}

	protected void basicSuspend() {
		//
	}

	private void callUpdateView(final Event event) {
		if (!isUiActive()) {
			return;
		}
		Toolkit.get().invokeInUI(decorate(new Runnable() {
			@Override
			public void run() {
				if (!isUiActive()) {
					return;
				}
				Log.trace(LOG_TEMPLATE, this); // $NON-NLS-1$
				try {
					updateView(event);
				} catch (Exception e) {
					Log.warn("UI {} unexpected error", this, e); //$NON-NLS-1$
				}
			}

			@Override
			public String toString() {
				return "updateView";
			}
		}));
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		configuration = element;
	}

	@Override
	public final void createComponent(C parent) {
		setContainer(parent);
		T tempComponent = basicCreateComponent(parent);
		setComponent(tempComponent);
		componentCreated = true;
		basicAssociateComponent();
		callUpdateView(null);
	}

	protected Runnable decorate(Runnable cmd) {
		if (getValue() instanceof IExecutionDecorator) {
			return () -> ((IExecutionDecorator) getValue()).execute(cmd);
		} else {
			return cmd;
		}
	}

	protected void disarmModel(Object oldValue) {
		if (oldValue instanceof INotificationSupport) {
			((INotificationSupport) oldValue).removeNotificationListener(AttributeChangedEvent.ID, listenModelChange);
		}
	}

	@Override
	public final void dispose() {
		Log.trace("dispose called"); //$NON-NLS-1$
		synchronized (runtimeStateLock) {
			if (disposed) {
				return;
			}
			disposed = true;
		}
		disarmModel(getValue());
		componentCreated = false;
		basicDispose();
		Log.log(Level.TRACE, "UI queuing dispose"); //$NON-NLS-1$
		try {
			Toolkit.get().invokeNow(decorate(() -> {
				uiDispose();
				Log.trace("UI dispose called"); //$NON-NLS-1$
			}));
		} catch (Exception e) {
			Log.log(Level.WARN, "dispose UI failed", e); //$NON-NLS-1$
		}
	}

	protected void execute(Runnable cmd) {
		if (getValue() instanceof IExecutionDecorator) {
			((IExecutionDecorator) getValue()).execute(cmd);
		} else {
			cmd.run();
		}
	}

	@Override
	public final T getComponent() {
		return component;
	}

	public IElement getConfiguration() {
		return configuration;
	}

	protected C getContainer() {
		return container;
	}

	protected IActivity<?> getCurrentActivity() {
		// call from UI thread only
		return activityStack.peek();
	}

	public IUIComponent getParent() {
		return parent;
	}

	@Override
	public synchronized M getValue() {
		return getValueHolder().get();
	}

	protected IValueHolder<M> getValueHolder() {
		return valueHolder;
	}

	protected boolean isActivityActive(Class<?> clazz) {
		for (Object activity : activityStack) {
			if (clazz.isInstance(activity)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isComponentCreated() {
		return componentCreated;
	}

	public boolean isDisposed() {
		synchronized (runtimeStateLock) {
			return disposed;
		}
	}

	protected boolean isInheritValue() {
		return false;
	}

	@Override
	public boolean isSuspended() {
		synchronized (runtimeStateLock) {
			return suspended;
		}
	}

	public boolean isUiActive() {
		synchronized (runtimeStateLock) {
			return !(suspended || disposed);
		}
	}

	protected void modelChanged(final AttributeChangedEvent event) {
		if (!isComponentCreated()) {
			return;
		}
		Log.log(Level.TRACE, "UI modelChanged calling updateView"); //$NON-NLS-1$
		callUpdateView(event);
	}

	@Override
	public final void resume() {
		Log.trace("resume called"); //$NON-NLS-1$
		synchronized (runtimeStateLock) {
			if (!suspended) {
				return;
			}
			suspended = false;
		}
		basicResume();
		Log.log(Level.TRACE, "UI queuing resume"); //$NON-NLS-1$
		try {
			Toolkit.get().invokeNow(decorate(() -> {
				uiResume();
				Log.trace("UI resume called"); //$NON-NLS-1$
			}));
		} catch (Exception e) {
			Log.log(Level.WARN, "resume UI failed", e); //$NON-NLS-1$
		}
	}

	protected void setActivityHandler(UIComponentActivityHandler activityHandler) {
		this.activityHandler = activityHandler;
	}

	protected void setComponent(T component) {
		this.component = component;
	}

	protected void setComponentCreated(boolean componentAvailable) {
		this.componentCreated = componentAvailable;
	}

	protected void setContainer(C parent) {
		this.container = parent;
	}

	@Override
	public synchronized M setValue(M value) {
		return getValueHolder().set(value);
	}

	@Override
	public final void suspend() {
		Log.trace("suspend called"); //$NON-NLS-1$
		synchronized (runtimeStateLock) {
			if (suspended) {
				return;
			}
			suspended = true;
		}
		basicSuspend();
		Log.log(Level.TRACE, "UI queuing suspend"); //$NON-NLS-1$
		try {
			Toolkit.get().invokeNow(decorate(() -> {
				uiSuspend();
				Log.trace("UI suspend called"); //$NON-NLS-1$
			}));
		} catch (Exception e) {
			Log.log(Level.WARN, "suspend UI failed", e); //$NON-NLS-1$
		}
	}

	protected void uiDispose() {
		//
	}

	protected void uiResume() {
		//
	}

	protected void uiSuspend() {
		//
	}

	protected void updateView(Event e) {
		//
	}
}
