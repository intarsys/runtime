package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * Ask an {@link IStringEvaluator} attached to the current thread to resolve.
 *
 */
public class ThreadContextAwareResolver implements IStringEvaluator {

	private static final ThreadLocal<ScopedResolver> RESOLVER = new ThreadLocal<>();

	public static synchronized IStringEvaluator attach(IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			tempResolver = new ScopedResolver();
			RESOLVER.set(tempResolver);
		}
		tempResolver.pushResolver(pResolver);
		return pResolver;
	}

	/**
	 * Convenience method for the common use case of adding another namespace to the current thread context.
	 * 
	 * @return The newly created strict {@link MapResolver}
	 */
	public static synchronized MapResolver attachMapResolver() {
		return (MapResolver) attach(MapResolver.createStrict());
	}

	public static synchronized void detach(IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			return;
		}
		tempResolver.removeResolver(pResolver);
		if (tempResolver.isEmpty()) {
			RESOLVER.remove();
		}
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			throw new NamespaceNotFound("can't evaluate '" + expression + "'");
		}
		return tempResolver.evaluate(expression, args);
	}

}
