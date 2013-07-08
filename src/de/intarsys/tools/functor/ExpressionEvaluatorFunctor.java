package de.intarsys.tools.functor;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.ExpressionEvaluator;

public class ExpressionEvaluatorFunctor extends CommonFunctor<String> {

	private static final String ARG_EXPRESSION = "expression"; // $NON-NLS-1$

	@Override
	public String perform(IFunctorCall call) throws FunctorInvocationException {
		String expression = (String) call.getArgs().get(ARG_EXPRESSION);
		try {
			return (String) ExpressionEvaluator.get().evaluate(expression,
					call.getArgs());
		} catch (EvaluationException e) {
			throw new FunctorInvocationException("expression '" + expression
					+ "' cannot be evaluated", e);
		}
	}

}
