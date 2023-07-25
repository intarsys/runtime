package de.intarsys.tools.expression;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.string.StringTools;

public class StringEvaluatorTools {

	/**
	 * See {@link #evaluate(IStringEvaluator, Object, IArgs)}.
	 * 
	 * @param evaluator
	 * @param value
	 * @return
	 */
	public static Object evaluate(IStringEvaluator evaluator, Object value) {
		return evaluate(evaluator, value, Args.create());
	}

	/**
	 * Evaluate {@code value} if it is a {@link String}.
	 * 
	 * @param evaluator
	 * @param value
	 * @param args
	 * @return The evaluated result of the expression if {@code value } is a {@link String}, the {@code value} itself
	 *         otherwise.
	 */
	public static Object evaluate(IStringEvaluator evaluator, Object value, IArgs args) {
		if (evaluator == null) {
			return value;
		}
		if (value instanceof String) {
			try {
				return evaluator.evaluate((String) value, args);
			} catch (Exception ex) {
				// leave for later evaluation
			}
		}
		return value;
	}

	/**
	 * See {@link #evaluateString(IStringEvaluator, String, IArgs)}.
	 * 
	 * @param evaluator
	 * @param value
	 * @return
	 */
	public static String evaluateString(IStringEvaluator evaluator, String value) {
		return evaluateString(evaluator, value, Args.create());
	}

	/**
	 * Evaluate {@code value} and return the result as a {@link String}.
	 * 
	 * @param evaluator
	 * @param value
	 * @param args
	 * @return The evaluated result of the expression.
	 */
	public static String evaluateString(IStringEvaluator evaluator, String value, IArgs args) {
		if (evaluator == null) {
			return value;
		}
		if (value == null) {
			return null;
		}
		try {
			return StringTools.safeString(evaluator.evaluate(value, args));
		} catch (Exception ex) {
			// leave for later evaluation
		}
		return value;
	}

	private StringEvaluatorTools() {
	}

}
