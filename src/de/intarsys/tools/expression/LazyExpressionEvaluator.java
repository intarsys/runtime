package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * This {@link IStringEvaluator} forwards evaluation to the current
 * {@link ExpressionEvaluator} singleton.
 * 
 */
public class LazyExpressionEvaluator implements IStringEvaluator {

	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		IStringEvaluator current = ExpressionEvaluator.get();
		if (current == this) {
			// defend against premature evaluator use
			throw new EvaluationException("singleton not available"); //$NON-NLS-1$
		}
		return current.evaluate(expression, args);
	}

}