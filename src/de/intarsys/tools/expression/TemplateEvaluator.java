/*
 * Copyright (c) 2007, intarsys consulting GmbH
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

import java.util.Map;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

/**
 * A VM singleton for a template evaluation engine. This should be able to
 * evaluate templates with embedded expressions, for example in the form
 * "Hello, ${user}. Are you ${state}?".
 * 
 */
final public class TemplateEvaluator {

	/**
	 * A simple trick to support lazy access to this singleton. If someone reads
	 * the singleton before the context is set up properly, he will be forwarded
	 * to the correct context upon execution.
	 * 
	 */
	private static IStringEvaluator ACTIVE = new LazyTemplateEvaluator();

	static public IStringEvaluator get() {
		return ACTIVE;
	}

	static public void set(IStringEvaluator active) {
		ACTIVE = active;
	}

	/**
	 * Translate the expression syntax within template.
	 * 
	 * This is useful for establishing migration paths from old style templates.
	 * 
	 * @param template
	 * @param expressionMap
	 * @return
	 */
	public static String translate(String template,
			final Map<String, String> expressionMap) {
		IStringEvaluator resolver = new IStringEvaluator() {
			@Override
			public Object evaluate(String expression, IArgs args)
					throws EvaluationException {
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
		try {
			return (String) evaluator.evaluate(template, Args.create());
		} catch (EvaluationException e) {
			return template;
		}
	}

	private TemplateEvaluator() {
	}
}
