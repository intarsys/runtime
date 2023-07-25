package de.intarsys.tools.action;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.IFunctorCallFactory;
import de.intarsys.tools.reflect.FieldAccessException;
import de.intarsys.tools.reflect.IFieldHandler;
import de.intarsys.tools.reflect.ObjectCreationException;

public class ActionCheckedFieldHandler implements IFieldHandler {

	public static IFieldHandler create(IAction action) {
		return new ActionCheckedFieldHandler(action);
	}

	private final IAction action;

	public ActionCheckedFieldHandler(IAction action) {
		super();
		this.action = action;
	}

	public IAction getAction() {
		return action;
	}

	public Object getValue(Object receiver) throws FieldAccessException {
		if (action == null) {
			return null;
		}
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(action, receiver, Args.create());
			} catch (ObjectCreationException e) {
				throw new FieldAccessException("checked", e);
			}
		} else {
			call = new FunctorCall(receiver, Args.create());
		}
		return action.isChecked(call);
	}

	public Object setValue(Object receiver, Object value) throws FieldAccessException {
		return null;
	}

}
