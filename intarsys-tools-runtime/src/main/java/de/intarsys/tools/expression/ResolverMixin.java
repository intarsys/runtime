package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * Provide a "semi ready" implementation with a simple, redefinable protocol for building a {@link MapResolver}.
 */
public class ResolverMixin implements IStringEvaluator {

	private final Object object;

	private IStringEvaluator expressionEvaluator;

	public ResolverMixin(Object object) {
		this.object = object;
	}

	protected IStringEvaluator createExpressionEvaluator() {
		MapResolver resolver = MapResolver.createStrict();
		resolver.setExceptionResolver(new IStringEvaluator() {
			@Override
			public Object evaluate(String expression, IArgs args) throws EvaluationException {
				return evaluateFallback(expression, args);
			}
		});
		createExpressionEvaluator(resolver);
		return resolver;
	}

	protected void createExpressionEvaluator(MapResolver resolver) {
		// hook method
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		return getExpressionEvaluator().evaluate(expression, args);
	}

	protected Object evaluateFallback(String expression, IArgs args) throws EvaluationException {
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IStringEvaluator getExpressionEvaluator() {
		synchronized (this) {
			if (expressionEvaluator == null) {
				expressionEvaluator = createExpressionEvaluator();
			}
			return expressionEvaluator;
		}
	}

	public Object getObject() {
		return object;
	}

	public void reset() {
		expressionEvaluator = null;
	}
}
