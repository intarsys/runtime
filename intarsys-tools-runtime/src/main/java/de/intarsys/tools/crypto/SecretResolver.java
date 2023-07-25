package de.intarsys.tools.crypto;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.IArgs;

/**
 * Resolve expression to a {@link Secret}.
 * 
 * This way you can embed secrets into text like configuration values.
 *
 * Example: ${secret.plain#Zm9v}
 */
public class SecretResolver implements IStringEvaluator {

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		return Secret.parse(expression);
	}

}
