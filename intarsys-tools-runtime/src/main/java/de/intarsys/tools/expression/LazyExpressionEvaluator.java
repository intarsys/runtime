package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * This {@link IStringEvaluator} forwards evaluation to the current {@link ExpressionEvaluator} singleton.
 */
public class LazyExpressionEvaluator implements IStringEvaluator {
	private Mode mode;

	public LazyExpressionEvaluator(Mode mode) {
		this.mode = mode;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		IStringEvaluator current = ExpressionEvaluator.get(mode);
		if (current == this) {
			// defend against premature evaluator use
			throw new NamespaceNotFound("singleton not available"); //$NON-NLS-1$
		}
		return current.evaluate(expression, args);
	}
}