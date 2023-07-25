package de.intarsys.tools.action;

import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.IFunctorCallFactory;
import de.intarsys.tools.reflect.IMethodHandler;
import de.intarsys.tools.reflect.IMethodHandlerAccessibility;
import de.intarsys.tools.reflect.MethodException;
import de.intarsys.tools.reflect.MethodExecutionException;
import de.intarsys.tools.reflect.ObjectCreationException;

public class ActionMethodHandler implements IMethodHandler, IMethodHandlerAccessibility {

	public static IMethodHandler create(IAction action) {
		if (action instanceof IMethodHandler) {
			return (IMethodHandler) action;
		} else {
			return new ActionMethodHandler(action.getId(), action);
		}
	}

	private final IAction action;

	private final String name;

	public ActionMethodHandler(String name, IAction action) {
		super();
		this.name = name;
		this.action = action;
	}

	public IAction getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	@Override
	public Object invoke(Object receiver, IArgs args) throws MethodException {
		if (action == null) {
			return null;
		}
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(action, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodExecutionException(getName(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		try {
			return action.perform(call);
		} catch (FunctorException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new MethodExecutionException(getName(), cause);
		}
	}

	@Override
	public boolean isInvokeEnabled(Object receiver, IArgs args) throws MethodException {
		if (action == null) {
			return false;
		}
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(action, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodExecutionException(getName(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		try {
			return action.isEnabled(call);
		} catch (Exception e) {
			throw new MethodExecutionException(getName(), e);
		}
	}

}
