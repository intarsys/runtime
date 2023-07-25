package de.intarsys.tools.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import org.junit.Test;

import de.intarsys.tools.functor.Args;

public class TestRestrictedStringEvaluator {
	@Test
	public void byDefautAllExpressionsAreBlocked() {
		assertBlocked(newEvaluator(null, null), "pi");
	}

	@Test
	public void canAllowExpressions() {
		IStringEvaluator evaluator = newEvaluator("x.*|y", null);

		assertAllowed(evaluator, "x1");
		assertAllowed(evaluator, "x2");
		assertAllowed(evaluator, "y");

		assertBlocked(evaluator, "z");
	}

	@Test
	public void canBlockAllowedExpressions() {
		IStringEvaluator evaluator = newEvaluator(".*", "x2|y");

		assertAllowed(evaluator, "x1");
		assertAllowed(evaluator, "z");

		assertBlocked(evaluator, "x2");
		assertBlocked(evaluator, "y");
	}

	private void assertAllowed(IStringEvaluator evaluator, String expression) {
		try {
			assertEquals("unexpected value", expression, evaluator.evaluate(expression, Args.create()));
		} catch (EvaluationException exception) {
			fail("unexpected exception for expression " + expression + ": " + exception);
		}
	}

	private void assertBlocked(IStringEvaluator evaluator, String expression) {
		assertThrows(EvaluationException.class, () -> evaluator.evaluate(expression, Args.create()));
	}

	private IStringEvaluator newEvaluator(String allow, String block) {
		return new RestrictedStringEvaluator((expression, args) -> expression, toPattern(allow), toPattern(block));
	}

	private Pattern toPattern(String regex) {
		return regex == null
				? null
				: Pattern.compile(regex);
	}
}
