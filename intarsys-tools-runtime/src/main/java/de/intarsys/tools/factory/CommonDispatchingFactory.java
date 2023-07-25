package de.intarsys.tools.factory;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.objectmodel.ObjectModelTools;
import de.intarsys.tools.reflect.IInvocationSupport;
import de.intarsys.tools.reflect.MethodExecutionException;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * An {@link IFactory} that delegates the creation to one of its arguments (aka
 * "double dispatch").
 * 
 * A subclass must provide information on the concrete argument and the method
 * to invoke.
 */
public abstract class CommonDispatchingFactory<T> extends CommonFactory<T> {

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		Object object = null;
		try {
			object = getDoubleDispatchTarget(args);
			if (object == null) {
				throw new ObjectCreationException("factory '" + getId() + "' double dispatch target may not be null");
			} else if (object instanceof IInvocationSupport) {
				return (T) ((IInvocationSupport) object).invoke(getMethodName(), args);
			} else {
				return (T) ObjectModelTools.invoke(object, getMethodName(), args);
			}
		} catch (MethodExecutionException e) {
			throw ExceptionTools.createTypedFromChain(e.getCause(), ObjectCreationException.class);
		} catch (Exception e) {
			throw ExceptionTools.createTyped(e, ObjectCreationException.class);
		} finally {
			if (object != null) {
				releaseDoubleDispatchTarget(object);
			}
		}
	}

	/**
	 * Return the target for the double dispatch call.
	 * 
	 * If this object is subject to reference counting, do not forget to
	 * implement "releaseDoubleDispatchTarget".
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	protected abstract Object getDoubleDispatchTarget(IArgs args) throws Exception;

	protected abstract String getMethodName();

	protected void releaseDoubleDispatchTarget(Object object) {
		// if we need some post processing for the target object after instance
		// creation - do it here
	}

}
