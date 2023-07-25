package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.session.ISession;
import de.intarsys.tools.session.SessionLocal;

/**
 * Forward evaluation to a {@link ScopedResolver} attached to an
 * {@link ISession}.
 * 
 * @see ThreadContextAwareResolver
 * 
 */
public class SessionAwareResolver implements IStringEvaluator {

	private static final SessionLocal<ScopedResolver> RESOLVER = new SessionLocal<>();

	public static synchronized void add(IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			tempResolver = new ScopedResolver();
			RESOLVER.set(tempResolver);
		}
		tempResolver.addResolver(pResolver);
	}

	public static synchronized IStringEvaluator add(String key, IStringEvaluator pResolver) {
		ScopedResolver tempResolver = RESOLVER.get();
		if (tempResolver == null) {
			tempResolver = new ScopedResolver();
			RESOLVER.set(tempResolver);
		}
		MapResolver newResolver = MapResolver.create(key, pResolver);
		tempResolver.addResolver(newResolver);
		return newResolver;
	}

	public static synchronized void remove(IStringEvaluator pResolver) {
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
