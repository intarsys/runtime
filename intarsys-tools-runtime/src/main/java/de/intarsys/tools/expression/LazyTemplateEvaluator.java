package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * This {@link IStringEvaluator} forwards evaluation to the current {@link TemplateEvaluator} singleton.
 */
public class LazyTemplateEvaluator implements IStringEvaluator {
	private Mode mode;

	public LazyTemplateEvaluator(Mode mode) {
		this.mode = mode;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		IStringEvaluator current = TemplateEvaluator.get(mode);
		if (current == this) {
			throw new NamespaceNotFound("singleton not available"); //$NON-NLS-1$
		}
		return current.evaluate(expression, args);
	}
}