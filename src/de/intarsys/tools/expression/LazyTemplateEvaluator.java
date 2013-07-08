package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * This {@link IStringEvaluator} forwards evaluation to the current
 * {@link TemplateEvaluator} singleton.
 * 
 */
public class LazyTemplateEvaluator implements IStringEvaluator {
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		IStringEvaluator current = TemplateEvaluator.get();
		if (current == this) {
			throw new EvaluationException("singleton not available"); //$NON-NLS-1$
		}
		return current.evaluate(expression, args);
	}
}