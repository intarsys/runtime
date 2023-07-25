package de.intarsys.tools.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestMutableResolver {
	private interface Resolver {
		Object resolve(String expression) throws EvaluationException;
	}

	private static class Probe implements IStringEvaluator {
		private Resolver resolver;
		private String lastExpression;

		public Probe(Resolver resolver) {
			this.resolver = resolver;
		}

		@Override
		public Object evaluate(String expression, IArgs args) throws EvaluationException {
			this.lastExpression = expression;
			return resolver.resolve(expression);
		}

		public String getLastExpression() {
			return lastExpression;
		}
	}

	@Test
	public void delegates() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		MutableResolver resolver = new MutableResolver(probe);

		assertEquals("value", resolver.evaluate("value", Args.create()));
		assertEquals("value", probe.getLastExpression());
	}

	@Test
	public void valuesOverrideDelegate() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		MutableResolver resolver = new MutableResolver(probe);
		resolver.setValue("value", 42);

		assertEquals(42, resolver.evaluate("value", Args.create()));
		assertNull(probe.getLastExpression());
	}
}

