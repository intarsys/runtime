package de.intarsys.tools.factory;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A common superclass for implementing a static delegation strategy on an
 * {@link IFactory}.
 * 
 * @param <T>
 */
public abstract class CommonDelegatingFactory<T> extends CommonFactory<T> implements IBookkeepingFactory<T> {

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		if (basicGetDelegate() instanceof INotificationSupport) {
			((INotificationSupport) basicGetDelegate()).addNotificationListener(type, listener);
			return;
		}
		super.addNotificationListener(type, listener);
	}

	protected abstract IFactory<T> basicGetDelegate();

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		return getDelegate().createInstance(args);
	}

	@Override
	public T getActive() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory<T>) getDelegate()).getActive();
		}
		return null;
	}

	@Override
	public final Object getAttribute(Object key) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate()).getAttribute(key);
		}
		return super.getAttribute(key);
	}

	public IFactory<T> getDelegate() {
		return basicGetDelegate();
	}

	@Override
	public String getDescription() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getDescription();
		}
		return super.getDescription();
	}

	@Override
	public String getIconName() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getIconName();
		}
		return super.getIconName();
	}

	@Override
	public List getInstances() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory) getDelegate()).getInstances();
		}
		return new ArrayList<>();
	}

	@Override
	public String getLabel() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getLabel();
		}
		return super.getLabel();
	}

	@Override
	public Class<T> getResultType() {
		return (Class<T>) getDelegate().getResultType();
	}

	@Override
	public String getTip() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getTip();
		}
		return super.getTip();
	}

	@Override
	public final Object removeAttribute(Object key) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate()).removeAttribute(key);
		}
		return super.removeAttribute(key);
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		if (basicGetDelegate() instanceof INotificationSupport) {
			((INotificationSupport) basicGetDelegate()).removeNotificationListener(type, listener);
			return;
		}
		super.removeNotificationListener(type, listener);
	}

	@Override
	public final Object setAttribute(Object key, Object value) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate()).setAttribute(key, value);
		}
		return super.setAttribute(key, value);
	}

	@Override
	public int size() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory) getDelegate()).size();
		}
		return 0;
	}
}
