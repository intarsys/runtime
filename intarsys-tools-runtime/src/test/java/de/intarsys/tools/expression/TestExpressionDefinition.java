package de.intarsys.tools.expression;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import de.intarsys.tools.functor.Args;

public class TestExpressionDefinition {
	enum Value {
		DEFAULT,
		UNICORN
	}

	@Test
	public void allModesByDefault() throws Exception {
		with(new ExpressionDefinition("x", Value.UNICORN), () -> {
			assertSame(Value.UNICORN, resolve(Mode.TRUSTED, "x"));
			assertSame(Value.UNICORN, resolve(Mode.UNTRUSTED, "x"));
		});
	}

	@Test
	public void trustedOnly() throws Exception {
		with(new ExpressionDefinition("x", Value.UNICORN, Mode.TRUSTED), () -> {
			assertSame(Value.UNICORN, resolve(Mode.TRUSTED, "x"));
			assertSame(Value.DEFAULT, resolve(Mode.UNTRUSTED, "x"));
		});
	}

	@Test
	public void untrustedOnly() throws Exception {
		with(new ExpressionDefinition("x", Value.UNICORN, Mode.UNTRUSTED), () -> {
			assertSame(Value.DEFAULT, resolve(Mode.TRUSTED, "x"));
			assertSame(Value.UNICORN, resolve(Mode.UNTRUSTED, "x"));
		});
	}

	private Object resolve(Mode mode, String expression) throws EvaluationException {
		return ExpressionEvaluator.get(mode).evaluate(expression, Args.create());
	}

	/**
	 * Installs pristine global trusted and untrusted evaluators, installs the given expression definition, runs
	 * the given runnable, and finally installs the original evaluators again.
	 */
	private void with(ExpressionDefinition definition, ThrowingRunnable runnable) throws Exception {
		try (TestBubble trusted = new TestBubble(Mode.TRUSTED);
				TestBubble untrusted = new TestBubble(Mode.UNTRUSTED)) {
			definition.install();
			runnable.run();
		}
	}

	private interface ThrowingRunnable {
		void run() throws Exception;
	}

	private static class TestBubble implements AutoCloseable {
		private Mode mode;
		private IStringEvaluator old;

		public TestBubble(Mode mode) {
			this.mode = mode;
			this.old = ExpressionEvaluator.get(mode);
			ExpressionEvaluator.set(mode, new MutableResolver(new ConstantResolver(Value.DEFAULT)));
		}

		@Override
		public void close() {
			ExpressionEvaluator.set(mode, old);
		}
	}
}
