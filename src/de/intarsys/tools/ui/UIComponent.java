/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.valueholder.IValueHolder;

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
abstract public class UIComponent<M, C, T> implements IUIComponent<M, C, T>,
		IElementConfigurable {

	class InheritedValueHolder implements IValueHolder<M>, IDisposable {

		final private INotificationListener listenParentChange = new INotificationListener() {
			@Override
			public void handleEvent(Event event) {
				onParentChange((AttributeChangedEvent) event);
			}
		};

		public InheritedValueHolder() {
			getParent().addNotificationListener(AttributeChangedEvent.ID,
					listenParentChange);
			if (getParent().getValue() instanceof INotificationSupport) {
				((INotificationSupport) getParent().getValue())
						.addNotificationListener(AttributeChangedEvent.ID,
								listenModelChange);
			}
		}

		@Override
		public void dispose() {
			getParent().removeNotificationListener(AttributeChangedEvent.ID,
					listenParentChange);
			if (getParent().getValue() instanceof INotificationSupport) {
				((INotificationSupport) getParent().getValue())
						.removeNotificationListener(AttributeChangedEvent.ID,
								listenModelChange);
			}
		}

		@Override
		public M get() {
			IUIComponent parent = getParent();
			if (parent == null) {
				return null;
			}
			return (M) parent.getValue();
		}

		@Override
		public boolean isDisposed() {
			return disposed;
		}

		protected void onParentChange(AttributeChangedEvent event) {
			if (event.getOldValue() instanceof INotificationSupport) {
				((INotificationSupport) event.getOldValue())
						.removeNotificationListener(AttributeChangedEvent.ID,
								listenModelChange);
			}
			if (event.getNewValue() instanceof INotificationSupport) {
				((INotificationSupport) event.getNewValue())
						.addNotificationListener(AttributeChangedEvent.ID,
								listenModelChange);
			}
			// change my model
			modelChanged(event);
			// funnel down the hierarchy
			triggerEvent(event);
		}

		@Override
		public M set(M newValue) {
			setValueHolder(new LocalValueHolder<M>(null));
			return setValue(newValue);
		}
	}

	class LocalValueHolder<T> implements IValueHolder<T>, IDisposable {

		private T value;

		public LocalValueHolder(T value) {
			super();
			set(value);
		}

		@Override
		public void dispose() {
			if (value instanceof INotificationSupport) {
				((INotificationSupport) value).removeNotificationListener(
						AttributeChangedEvent.ID, listenModelChange);
			}
		}

		public T get() {
			return value;
		}

		@Override
		public boolean isDisposed() {
			return disposed;
		}

		public T set(T newValue) {
			T oldValue = value;
			if (oldValue == newValue) {
				return value;
			}
			if (oldValue instanceof INotificationSupport) {
				((INotificationSupport) oldValue).removeNotificationListener(
						AttributeChangedEvent.ID, listenModelChange);
			}
			if (newValue instanceof INotificationSupport) {
				((INotificationSupport) newValue).addNotificationListener(
						AttributeChangedEvent.ID, listenModelChange);
			}
			value = newValue;
			if (value != null) {
				modelChanged(new AttributeChangedEvent(value, null, null, null));
			}
			// funnel down the hierarchy
			triggerEvent(new AttributeChangedEvent(UIComponent.this, "value",
					oldValue, newValue));
			return oldValue;
		}

	}

	final private EventDispatcher eventDispatcher = new EventDispatcher(this);
	private C container;

	final private IUIComponent parent;

	private T component;

	private IValueHolder<M> valueHolder;

	private static final Logger Log = PACKAGE.Log;

	final private INotificationListener listenModelChange = new INotificationListener() {
		@Override
		public void handleEvent(final Event event) {
			modelChanged(event);
		}
	};

	private IElement configuration;

	private boolean disposed = false;

	private boolean componentCreated = false;

	public UIComponent() {
		this(null);
	}

	public UIComponent(IUIComponent<?, C, T> parent) {
		super();
		this.parent = parent;
		if (parent == null) {
			valueHolder = new LocalValueHolder<M>(null);
		} else {
			valueHolder = new InheritedValueHolder();
		}
	}

	public void addNotificationListener(EventType type,
			INotificationListener listener) {
		eventDispatcher.addNotificationListener(type, listener);
	}

	protected void basicAssociateComponent() {
	}

	abstract protected T basicCreateComponent(C parent);

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		configuration = element;
	}

	@Override
	final public void createComponent(C parent) {
		setContainer(parent);
		T tempComponent = basicCreateComponent(parent);
		setComponent(tempComponent);
		componentCreated = true;
		basicAssociateComponent();
		updateView(null);
	}

	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		if (valueHolder instanceof IDisposable) {
			((IDisposable) valueHolder).dispose();
		}
		componentCreated = false;
	}

	final public T getComponent() {
		return component;
	}

	public IElement getConfiguration() {
		return configuration;
	}

	protected C getContainer() {
		return container;
	}

	public IUIComponent getParent() {
		return parent;
	}

	synchronized public M getValue() {
		return getValueHolder().get();
	}

	public IValueHolder<M> getValueHolder() {
		return valueHolder;
	}

	protected void guiModelChanged(final Event event) {
		try {
			if (!isComponentCreated()) {
				return;
			}
			updateView(event);
		} catch (Exception e) {
			Log.log(Level.WARNING, "unexpeced error in updateView", e);
		}
	}

	protected boolean isComponentCreated() {
		return componentCreated;
	}

	public boolean isDisposed() {
		return disposed;
	}

	protected void modelChanged(final Event event) {
		// try to prevent deadlock situations by queuing invocation
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiModelChanged(event);
			}
		});
	}

	public void removeNotificationListener(EventType type,
			INotificationListener listener) {
		eventDispatcher.removeNotificationListener(type, listener);
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
	synchronized public M setValue(M value) {
		return getValueHolder().set(value);
	}

	public void setValueHolder(IValueHolder<M> pValueHolder) {
		if (valueHolder == pValueHolder) {
			return;
		}
		if (valueHolder instanceof IDisposable) {
			((IDisposable) valueHolder).dispose();
		}
		valueHolder = pValueHolder;
	}

	protected void triggerEvent(Event event) {
		eventDispatcher.triggerEvent(event);
	}

	protected void updateView(Event e) {
	}

}
