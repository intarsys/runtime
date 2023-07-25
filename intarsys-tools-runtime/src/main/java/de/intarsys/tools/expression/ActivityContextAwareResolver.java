package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.session.ActivityLocal;
import de.intarsys.tools.session.IActivityContext;

/**
 * Forward evaluation to a {@link ScopedResolver} attached to an
 * {@link IActivityContext}.
 * 
 * @see ThreadContextAwareResolver
 * 
 */
public class ActivityContextAwareResolver implements IStringEvaluator {

	private static final ActivityLocal<ScopedResolver> RESOLVER = new ActivityLocal<>();

	public static synchronized void attach(IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			tempResolver = new ScopedResolver();
			RESOLVER.set(tempResolver);
		}
		tempResolver.addResolver(pResolver);
	}

	public static synchronized void detach(IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			return;
		}
		tempResolver.removeResolver(pResolver);
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
