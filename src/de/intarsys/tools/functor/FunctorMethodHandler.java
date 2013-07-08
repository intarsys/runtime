package de.intarsys.tools.functor;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.reflect.IMethodHandler;
import de.intarsys.tools.reflect.MethodInvocationException;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FunctorMethodHandler implements IMethodHandler, IAttributeSupport {

	public static IMethodHandler create(IFunctor functor) {
		if (functor instanceof IMethodHandler) {
			return (IMethodHandler) functor;
		} else {
			if (functor instanceof IIdentifiable) {
				return new FunctorMethodHandler(((IIdentifiable) functor)
						.getId(), functor);
			} else {
				return new FunctorMethodHandler("unknown", functor);
			}
		}
	}

	final private IFunctor invoker;

	final private String name;

	public FunctorMethodHandler(String name, IFunctor invoker) {
		super();
		this.name = name;
		this.invoker = invoker;
	}

	public Object getAttribute(Object key) {
		if (invoker instanceof IAttributeSupport) {
			return ((IAttributeSupport) invoker).getAttribute(key);
		}
		return null;
	}

	public IFunctor getInvoker() {
		return invoker;
	}

	public String getName() {
		return name;
	}

	public Object invoke(Object receiver, IArgs args)
			throws MethodInvocationException {
		if (invoker == null) {
			return null;
		}
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(
						invoker, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodInvocationException(getName(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		try {
			return invoker.perform(call);
		} catch (FunctorInvocationException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new MethodInvocationException(getName(), cause);
		}
	}

	public Object removeAttribute(Object key) {
		if (invoker instanceof IAttributeSupport) {
			return ((IAttributeSupport) invoker).removeAttribute(key);
		}
		return null;
	}

	public Object setAttribute(Object key, Object value) {
		if (invoker instanceof IAttributeSupport) {
			return ((IAttributeSupport) invoker).setAttribute(key, value);
		}
		return null;
	}

}
