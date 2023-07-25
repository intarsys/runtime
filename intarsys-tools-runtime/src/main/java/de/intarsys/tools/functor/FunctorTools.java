package de.intarsys.tools.functor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.ComponentTargetException;
import de.intarsys.tools.component.IActivateDeactivate;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.concurrent.Promise;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.reflect.ObjectCreationException;

public class FunctorTools {

	static class FutureFunctor implements IFunctor {

		private final IFunctor functor;

		public FutureFunctor(IFunctor functor) {
			this.functor = functor;
		}

		public IFunctor getFunctor() {
			return functor;
		}

		@Override
		public Object perform(IFunctorCall call) throws FunctorException {
			Object result = functor.perform(call);
			return Promise.newFinished(result);
		}

	}

	/**
	 * "Process" a result object and ensure its is a deferred one.
	 * 
	 * @param object
	 * @return
	 * @throws FunctorException
	 */
	public static Future asFuture(Object object) throws FunctorException {
		if (object instanceof Future) {
			// object is not the result, but the representation of the
			// computation itself
			if (object instanceof IStartStop) {
				try {
					((IStartStop) object).start();
				} catch (ComponentTargetException e) {
					throw new FunctorExecutionException(e.getMessage(), e.getCause());
				} catch (ComponentException e) {
					throw new FunctorExecutionException(e);
				}
				if (((IStartStop) object).isStarted() && object instanceof IActivateDeactivate) {
					((IActivateDeactivate) object).activate();
				}
			}
			return (Future) object;
		} else {
			return Promise.newFinished(object);
		}
	}

	/**
	 * "Process" a result object. In case it is a deferred result, we take some
	 * actions to get the real result.
	 * 
	 * @param object
	 * @return
	 * @throws FunctorException
	 */
	public static Object asResult(Object object) throws FunctorException {
		if (object instanceof Future) {
			// object is not the result, but the representation of the
			// computation itself
			if (object instanceof IStartStop) {
				try {
					((IStartStop) object).start();
				} catch (ComponentTargetException e) {
					throw new FunctorExecutionException(e.getMessage(), e.getCause());
				} catch (ComponentException e) {
					throw new FunctorExecutionException(e);
				}
				if (((IStartStop) object).isStarted() && object instanceof IActivateDeactivate) {
					((IActivateDeactivate) object).activate();
				}
			}
			Object result = object;
			if (object instanceof IResultProvider) {
				// for the moment we can not use Future#get - which will block
				// always. #getResult doesn't
				result = ((IResultProvider) object).getResult();
			}
			return result;
		} else {
			return object;
		}
	}

	protected static IFunctor createFunctorFromCallable(final Callable callable) {
		return new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				try {
					return callable.call();
				} catch (Exception e) {
					throw ExceptionTools.createTyped(e, FunctorExecutionException.class);
				}
			}
		};
	}

	protected static IFunctor createFunctorFromObject(final Object value, boolean futureMode) {
		return new IFunctor() {
			private IFunctor cached;

			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				Object instance = null;
				if (cached == null) {
					InstanceSpec<?> spec = InstanceSpec.createFromFactory(Object.class, value, call.getArgs());
					try {
						instance = spec.createInstance();
					} catch (ObjectCreationException e) {
						throw new FunctorExecutionException(e);
					}
					if (instance instanceof IFunctor) {
						cached = (IFunctor) instance;
					}
				}
				if (instance == null) {
					return null;
				}
				if (cached == null) {
					// so we have an object here that represents the result,
					// not the function to create it.
					return futureMode ? asFuture(instance) : asResult(instance);
				} else {
					return futureMode ? Promise.newFinished(cached.perform(call)) : cached.perform(call);
				}
			}

			@Override
			public String toString() {
				return "Deferred Functor on " + value;
			}
		};
	}

	protected static IFunctor createFunctorFromRunnable(final Runnable runnable) {
		return new IFunctor() {

			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				try {
					runnable.run();
					return null;
				} catch (Exception e) {
					throw ExceptionTools.createTyped(e, FunctorExecutionException.class);
				}
			}
		};
	}

	/**
	 * Launches execution for a polymorphic {@link IFunctor} and return the
	 * {@link Future} computation representation.
	 * 
	 * @param functorDef
	 *            A polymorphic {@link IFunctor} .
	 * @param args
	 *            The arguments for the {@link IFunctor} call.
	 * @return The {@link Future} representing the computation of the result.
	 * 
	 * @throws ObjectCreationException
	 */
	public static Future launch(Object functorDef, IArgs args) throws ObjectCreationException {
		try {
			IFunctor<Future> functor = toFunctorFuture(functorDef);
			if (functor != null) {
				return functor.perform(new FunctorCall(null, args));
			}
			throw new ObjectCreationException("invalid functor " + functorDef);
		} catch (FunctorExecutionException e) {
			throw new ObjectCreationException(e.getCause());
		} catch (FunctorException e) {
			throw new ObjectCreationException(e);
		}
	}

	/**
	 * Execute a polymorphic {@link IFunctor} and return the result of the
	 * computation.
	 * 
	 * @param spec
	 *            The functor instance specification
	 * @return The result of executing the {@link IFunctor}.
	 * 
	 * @throws FunctorException
	 */
	public static Object perform(InstanceSpec<?> spec) throws FunctorException {
		try {
			Object instance = spec.createInstance();
			return asResult(instance);
		} catch (ObjectCreationException e) {
			throw new FunctorExecutionException(e);
		}
	}

	/**
	 * Execute a polymorphic {@link IFunctor} and return the result of the
	 * computation.
	 * 
	 * @param functorDef
	 *            A polymorphic {@link IFunctor} instance.
	 * @param args
	 *            The arguments for the {@link IFunctor} call.
	 * @return The result of executing the {@link IFunctor}.
	 * 
	 * @throws FunctorException
	 */
	public static Object perform(Object functorDef, IArgs args) throws FunctorException {
		try {
			IFunctor functor = toFunctorResult(functorDef);
			if (functor != null) {
				return functor.perform(new FunctorCall(null, args));
			}
			throw new FunctorInternalException("invalid functor " + functorDef);
		} catch (ObjectCreationException e) {
			throw new FunctorExecutionException(e);
		}
	}

	protected static IFunctor toFunctor(final Object value, boolean futureMode) throws ObjectCreationException {
		if (value == null) {
			return null;
		}
		if (value instanceof IFunctor) {
			return wrap((IFunctor) value, futureMode);
		} else if (value instanceof Callable) {
			final Callable callable = (Callable) value;
			return wrap(createFunctorFromCallable(callable), futureMode);
		} else if (value instanceof Runnable) {
			final Runnable runnable = (Runnable) value;
			return wrap(createFunctorFromRunnable(runnable), futureMode);
		} else {
			return createFunctorFromObject(value, futureMode);
		}
	}

	/**
	 * Create an {@link IFunctor} that will return a {@link Future} for the
	 * final result when eventually executed.
	 * 
	 * @param value
	 * @return An {@link IFunctor}
	 * 
	 * @throws ObjectCreationException
	 */
	public static IFunctor toFunctorFuture(final Object value) throws ObjectCreationException {
		return toFunctor(value, true);
	}

	/**
	 * Create an {@link IFunctor} that will return the final computation result
	 * when eventually executed.
	 * 
	 * @param value
	 * @return An {@link IFunctor}
	 * 
	 * @throws ObjectCreationException
	 */
	public static IFunctor toFunctorResult(final Object value) throws ObjectCreationException {
		return toFunctor(value, false);
	}

	protected static IFunctor wrap(final IFunctor value, boolean futureMode) {
		return futureMode ? new FutureFunctor(value) : value;
	}

	private FunctorTools() {
	}
}
