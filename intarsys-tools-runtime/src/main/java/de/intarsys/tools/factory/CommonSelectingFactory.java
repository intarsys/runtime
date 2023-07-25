package de.intarsys.tools.factory;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * Create an instance after selection of a matching {@link IFactory} from a
 * target set.
 * 
 * A subclass must provide information on the concrete argument and the method
 * to invoke.
 */
public abstract class CommonSelectingFactory<T> extends CommonFactory<T> {

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		try {
			IFactory factory = selectTargetFactory(args);
			if (factory == null) {
				throw new ObjectCreationException("factory '" + getId() + "' can not find a target factory");
			}
			return (T) factory.createInstance(args);
		} catch (Exception e) {
			throw ExceptionTools.createTyped(e, ObjectCreationException.class);
		}
	}

	protected abstract IFactory selectTargetFactory(IArgs args) throws Exception;

}
