package de.intarsys.tools.zones;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.NamespaceNotFound;
import de.intarsys.tools.expression.ScopedResolver;
import de.intarsys.tools.functor.IArgs;

/**
 * Forward evaluation to a {@link ScopedResolver} attached to the
 * {@link IZone}.
 * 
 */
public class ZoneAwareResolver implements IStringEvaluator {

	private static final ZoneLocal<ScopedResolver> RESOLVER = new ZoneLocal<>();

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
