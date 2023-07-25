/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.expression;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.Singleton;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

/**
 * A VM singleton for a template evaluation engine. This should be able to
 * evaluate templates with embedded expressions, for example in the form "Hello,
 * ${user}. Are you ${state}?".
 *
 */
@Singleton
public final class TemplateEvaluator {
	public static class Installer {
		private Mode mode;
		private IStringEvaluator resolver;

		public Installer(Mode mode, IStringEvaluator resolver) {
			this.mode = mode;
			this.resolver = resolver;
		}

		@PostConstruct
		public void install() {
			TemplateEvaluator.set(mode, TaggedStringEvaluator.decorate(resolver));
		}
	}

	private static final Map<Mode, IStringEvaluator> EVALUATORS = new EnumMap<>(Mode.class);

	public static String evaluateString(Mode mode, String value) {
		return evaluateString(mode, value, Args.create());
	}

	public static String evaluateString(Mode mode, String value, IArgs args) {
		return StringEvaluatorTools.evaluateString(get(mode), value, args);
	}

	/**
	 * @deprecated Use {@link #evaluateString(Mode, String)} instead.
	 */
	@Deprecated(since = "4.23.0", forRemoval = true)
	public static String evaluateString(String value) {
		return evaluateString(value, Args.create());
	}

	/**
	 * @deprecated Use {@link #evaluateString(Mode, String, IArgs)} instead.
	 */
	@Deprecated(since = "4.23.0", forRemoval = true)
	public static String evaluateString(String value, IArgs args) {
		return StringEvaluatorTools.evaluateString(get(), value, args);
	}

	/**
	 * Returns an evaluator for untrusted data.
	 *
	 * @deprecated Use {@link #get(Mode)} instead.
	 */
	@Deprecated(since = "4.23.0", forRemoval = true)
	public static IStringEvaluator get() {
		return get(Mode.UNTRUSTED);
	}

	/**
	 * Returns the global template evaluator for the given mode. The evaluators for trusted and untrusted modes may have
	 * different restrictions. For example, the untrusted evaluator may only support simple values whereas the
	 * trusted evaluator may grant access to the application's environment.
	 *
	 * @param mode
	 *            mode of the requested evaluator
	 * @return a global template evaluator
	 */
	public static IStringEvaluator get(Mode mode) {
		if (mode == null) {
			throw new IllegalArgumentException("mode must not be null");
		}

		/**
		 * LazyTemplateEvaluator is a simple trick to support lazy access to this singleton. If someone reads the
		 * singleton before the context is set up properly, he will be forwarded to the correct context later on
		 * execution.
		 */
		return EVALUATORS.computeIfAbsent(mode, LazyTemplateEvaluator::new);
	}

	/**
	 * Sets the evaluator for untrusted data.
	 *
	 * @deprecated Use {@link #set(Mode, IStringEvaluator)} instead.
	 */
	@Deprecated(since = "4.23.0", forRemoval = true)
	public static void set(IStringEvaluator evaluator) {
		set(Mode.UNTRUSTED, evaluator);
	}

	public static void set(Mode mode, IStringEvaluator evaluator) {
		if (mode == null) {
			throw new IllegalArgumentException("mode must not be null");
		}

		if (evaluator == null) {
			throw new IllegalArgumentException("evaluator must not be null");
		}

		EVALUATORS.put(mode, evaluator);
	}

	/**
	 * Translate the expression syntax within template. This is useful for establishing migration paths from old style
	 * templates.
	 */
	public static String translate(String template, final Map<String, String> expressionMap) {
		IStringEvaluator resolver = new IStringEvaluator() {
			@Override
			public Object evaluate(String expression, IArgs args) throws EvaluationException {
				String[] instructions = expression.split(":", 2);
				String valueExpression = instructions[0].trim();
				String replacement = expressionMap.get(valueExpression);
				if (replacement == null) {
					replacement = expression;
				} else {
					if (instructions.length > 1) {
						replacement = replacement + ":" + instructions[1];
					}
				}
				return "${" + replacement + "}";
			}
		};
		TaggedStringEvaluator evaluator = new TaggedStringEvaluator(resolver);
		return StringEvaluatorTools.evaluateString(evaluator, template);
	}

	private TemplateEvaluator() {
		super();
	}
}
