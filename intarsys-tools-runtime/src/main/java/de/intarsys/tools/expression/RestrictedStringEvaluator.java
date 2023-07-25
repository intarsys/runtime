package de.intarsys.tools.expression;

import java.util.regex.Pattern;

import de.intarsys.tools.functor.IArgs;

/**
 * A wrapper for an {@code IStringEvaluator} that allows or blocks expressions by regular expressions.
 */
public class RestrictedStringEvaluator implements IStringEvaluator {
	private IStringEvaluator delegate;
	private Pattern allowPattern;
	private Pattern blockPattern;

	public RestrictedStringEvaluator(IStringEvaluator delegate, Pattern allowPattern, Pattern blockPattern) {
		this.delegate = delegate;
		this.allowPattern = allowPattern;
		this.blockPattern = blockPattern;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if (allowed(expression)) {
			return delegate.evaluate(expression, args);
		}

		throw new EvaluationException("Cannot evaluate expression: " + expression);
	}

	private boolean allowed(String expression) {
		return allowPattern != null
				&& allowPattern.matcher(expression).matches()
				&& (blockPattern == null || !blockPattern.matcher(expression).matches());
	}
}
