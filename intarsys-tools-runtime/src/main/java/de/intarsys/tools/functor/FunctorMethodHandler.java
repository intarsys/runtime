package de.intarsys.tools.functor;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.reflect.IMethodHandler;
import de.intarsys.tools.reflect.MethodException;
import de.intarsys.tools.reflect.MethodExecutionException;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FunctorMethodHandler implements IMethodHandler, IAttributeSupport {

	public static IMethodHandler create(IFunctor functor) {
		if (functor instanceof IMethodHandler) {
			return (IMethodHandler) functor;
		} else {
			if (functor instanceof IIdentifiable) {
				return new FunctorMethodHandler(((IIdentifiable) functor).getId(), functor);
			} else {
				return new FunctorMethodHandler("unknown", functor);
			}
		}
	}

	private final IFunctor invoker;

	private final String name;

	public FunctorMethodHandler(String name, IFunctor invoker) {
		super();
		this.name = name;
		this.invoker = invoker;
	}

	@Override
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

	@Override
	public Object invoke(Object receiver, IArgs args) throws MethodException {
		if (invoker == null) {
			return null;
		}
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(invoker, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodExecutionException(getName(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		try {
			return invoker.perform(call);
		} catch (FunctorException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new MethodExecutionException(getName(), cause);
		}
	}

	@Override
	public Object removeAttribute(Object key) {
		if (invoker instanceof IAttributeSupport) {
			return ((IAttributeSupport) invoker).removeAttribute(key);
		}
		return null;
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		if (invoker instanceof IAttributeSupport) {
			return ((IAttributeSupport) invoker).setAttribute(key, value);
		}
		return null;
	}

}
