package de.intarsys.tools.expression;

import java.util.UUID;

import de.intarsys.tools.functor.IArgs;

public class IdentifiersResolver implements IStringEvaluator {
	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if ("uuid".equals(expression)) {
			return UUID.randomUUID();
		}

		throw new EvaluationException(String.format("can't evaluate '%s'", expression));
	}
}
