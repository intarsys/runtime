package de.intarsys.tools.functor;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.reflect.FieldAccessException;
import de.intarsys.tools.reflect.IFieldHandler;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FunctorFieldHandler implements IFieldHandler,
		INotificationSupport, IAttributeSupport {

	private IFunctor getter;

	private String name;

	private IFunctor setter;

	public FunctorFieldHandler() {
	}

	public FunctorFieldHandler(String name, IFunctor getter, IFunctor setter) {
		super();
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	public void addNotificationListener(EventType type,
			INotificationListener listener) {
		if (getter instanceof INotificationSupport) {
			((INotificationSupport) getter).addNotificationListener(type,
					listener);
		}
	}

	public Object getAttribute(Object key) {
		if (getter instanceof IAttributeSupport) {
			return ((IAttributeSupport) getter).getAttribute(key);
		}
		return null;
	}

	public IFunctor getGetter() {
		return getter;
	}

	public String getName() {
		return name;
	}

	public IFunctor getSetter() {
		return setter;
	}

	public Object getValue(Object object) throws FieldAccessException {
		if (getter == null) {
			return null;
		}
		IFunctorCall call;
		if (object instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) object).createFunctorCall(getter,
						object, Args.create());
			} catch (ObjectCreationException e) {
				throw new FieldAccessException(getName(), e);
			}
		} else {
			call = new FunctorCall(object, Args.create());
		}
		try {
			return getter.perform(call);
		} catch (FunctorInvocationException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new FieldAccessException(getName(), cause);
		}
	}

	public Object removeAttribute(Object key) {
		if (getter instanceof IAttributeSupport) {
			return ((IAttributeSupport) getter).removeAttribute(key);
		}
		return null;
	}

	public void removeNotificationListener(EventType type,
			INotificationListener listener) {
		if (getter instanceof INotificationSupport) {
			((INotificationSupport) getter).removeNotificationListener(type,
					listener);
		}
	}

	public Object setAttribute(Object key, Object value) {
		if (getter instanceof IAttributeSupport) {
			return ((IAttributeSupport) getter).setAttribute(key, value);
		}
		return null;
	}

	public void setGetter(IFunctor getter) {
		this.getter = getter;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSetter(IFunctor setter) {
		this.setter = setter;
	}

	public Object setValue(Object object, Object value)
			throws FieldAccessException {
		if (setter == null) {
			return null;
		}
		IFunctorCall call;
		if (object instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) object).createFunctorCall(setter,
						object, Args.createIndexed(value));
			} catch (ObjectCreationException e) {
				throw new FieldAccessException(getName(), e);
			}
		} else {
			call = new FunctorCall(object, Args.createIndexed(value));
		}
		try {
			return setter.perform(call);
		} catch (FunctorInvocationException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new FieldAccessException(getName(), cause);
		}
	}

}
