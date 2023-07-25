package de.intarsys.tools.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestAliasResolver {
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
	public void notAliased() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of());

		assertEquals("value", resolver.evaluate("value", Args.create()));
		assertEquals("value", probe.getLastExpression());
	}

	@Test
	public void valueAlias() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("alias", "value"));

		assertEquals("value", resolver.evaluate("alias", Args.create()));
		assertEquals("value", probe.getLastExpression());
	}

	@Test
	public void nestedValueAlias() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("deep.down.alias", "value"));

		assertEquals("value", resolver.evaluate("deep.down.alias", Args.create()));
		assertEquals("value", probe.getLastExpression());
	}

	@Test
	public void namespaceAlias() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("alias", "namespace"));

		assertEquals("namespace.value1", resolver.evaluate("alias.value1", Args.create()));
		assertEquals("namespace.value1", probe.getLastExpression());

		assertEquals("namespace.value2", resolver.evaluate("alias.value2", Args.create()));
		assertEquals("namespace.value2", probe.getLastExpression());
	}

	@Test
	public void namespaceAliasNestedValue() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("alias", "namespace"));

		assertEquals("namespace.value.child", resolver.evaluate("alias.value.child", Args.create()));
		assertEquals("namespace.value.child", probe.getLastExpression());

		assertEquals("namespace.value.distant.child", resolver.evaluate("alias.value.distant.child", Args.create()));
		assertEquals("namespace.value.distant.child", probe.getLastExpression());
	}

	@Test
	public void nestedNamespaceAlias() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("deep.down.alias", "namespace"));

		assertEquals("namespace.value", resolver.evaluate("deep.down.alias.value", Args.create()));
		assertEquals("namespace.value", probe.getLastExpression());
	}

	@Test
	public void nestedNamespaceAliasNestedValue() throws EvaluationException {
		Probe probe = new Probe(expression -> expression);
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("deep.down.alias", "namespace"));

		assertEquals("namespace.value.child", resolver.evaluate("deep.down.alias.value.child", Args.create()));
		assertEquals("namespace.value.child", probe.getLastExpression());

		assertEquals("namespace.value.distant.child",
				resolver.evaluate("deep.down.alias.value.distant.child", Args.create()));
		assertEquals("namespace.value.distant.child", probe.getLastExpression());
	}

	@Test
	public void evaluationException() {
		Probe probe = new Probe(expression -> { throw new NamespaceNotFound(); });
		IStringEvaluator resolver = new AliasResolver(probe, Map.of("my-alias", "my-actual-value"));

		try {
			resolver.evaluate("my-alias", Args.create());
			fail("expected a NamespaceNotFound exception");
		} catch (EvaluationException exception) {
			exception.printStackTrace();

			assertTrue("expected alias in exception message", exception.getMessage().contains("my-alias"));
			assertTrue("expected resolved expression in exception message", exception.getMessage().contains("my-actual-value"));
			assertTrue("expected NamespaceNotFound to be the cause", exception.getCause() instanceof NamespaceNotFound);
		}
	}
}
