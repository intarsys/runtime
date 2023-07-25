package de.intarsys.tools.functor;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.ExpressionEvaluator;
import de.intarsys.tools.expression.Mode;

public class ExpressionEvaluatorFunctor extends CommonFunctor<String> {

	private static final String ARG_EXPRESSION = "expression"; // $NON-NLS-1$

	@Override
	public String perform(IFunctorCall call) throws FunctorException {
		String expression = (String) call.getArgs().get(ARG_EXPRESSION);
		try {
			return (String) ExpressionEvaluator.get(Mode.UNTRUSTED).evaluate(expression, call.getArgs());
		} catch (EvaluationException e) {
			throw new FunctorExecutionException("expression '" + expression + "' cannot be evaluated", e);
		}
	}

}
