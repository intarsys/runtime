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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reader.DirectTagReader;
import de.intarsys.tools.reader.IDirectTagHandler;
import de.intarsys.tools.reader.ILocationProvider;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * An {@link IStringEvaluator} for string templates. The evaluator handles strings of the form
 * 
 * <code>
 * [ chars | "${" chars "}" ]*
 * </code>,
 * 
 * This implementation is literally copying all chars outside the "${ }" tags and expanding all tagged
 * content using the supplied {@link IStringEvaluator}.
 * 
 * If strict is true, expansion will fail if an exception rises while expanding. If it is false, the expression that
 * failed is simply copied literally in the output. This is useful when expansion is applied multiple times with
 * different namespaces.
 */
public class TaggedStringEvaluator implements IStringEvaluator {

	private static final ILogger Log = PACKAGE.Log;

	/**
	 * Create a {@link TaggedStringEvaluator} using all {@link IStringEvaluator}
	 * resolver scopes (most significant first).
	 * 
	 * @param resolvers
	 * @return
	 */
	public static TaggedStringEvaluator decorate(IStringEvaluator... resolvers) {
		IStringEvaluator temp;
		if (resolvers.length > 1) {
			temp = ScopedResolver.create(resolvers);
		} else {
			temp = resolvers[0];
		}
		ProcessingDecorator decorator = new ProcessingDecorator(temp);
		TaggedStringEvaluator result = new TaggedStringEvaluator(decorator, false);
		// provide for recursive evaluation
		decorator.setRecursionEvaluator(result);
		return result;
	}

	/**
	 * Create a non-strict {@link TaggedStringEvaluator} using all {@link IStringEvaluator}
	 * resolver scopes (most significant first).
	 * 
	 * @param resolvers
	 * @return
	 */
	public static TaggedStringEvaluator decorateLenient(IStringEvaluator... resolvers) {
		TaggedStringEvaluator result = decorate(resolvers);
		result.setStrict(false);
		return result;
	}

	private boolean escape;

	/**
	 * The resolver used to lookup the variables in the tags.
	 */
	private final IStringEvaluator evaluator;

	private final IDirectTagHandler handler = new IDirectTagHandler() {
		@Override
		public Object endTag(String tagContent, Object context) throws IOException {
			return evaluateExpression(tagContent, (IArgs) context);
		}

		@Override
		public void setLocationProvider(ILocationProvider location) {
			// ignore
		}

		@Override
		public void startTag() {
			//
		}
	};

	private boolean strict = true;

	protected TaggedStringEvaluator(IStringEvaluator resolver) {
		this(resolver, false);
	}

	public TaggedStringEvaluator(IStringEvaluator resolver, boolean escape) {
		super();
		this.evaluator = resolver;
		this.escape = escape;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if (expression.indexOf('$') < 0) {
			return expression;
		}
		Reader base = new StringReader(expression);
		DirectTagReader reader = new DirectTagReader(base, handler, args, isEscape());
		try {
			reader.setForceToString(false);
			int length = expression.length() * 2;
			StringBuilder sb = new StringBuilder(length);
			char[] c = new char[length];
			int i = reader.read(c);
			while (i != -1) {
				sb.append(c, 0, i);
				i = reader.read(c);
			}
			if (sb.length() == 0) {
				if (reader.hasResolvedObject()) {
					return reader.getResolvedObject();
				}
			}
			return sb.toString();
		} catch (IOException e) {
			if (isStrict()) {
				throw new EvaluationException(e);
			} else {
				Log.warn("error evaluating {}, '{}'", expression, ExceptionTools.getMessage(e));
				return expression;
			}
		} finally {
			StreamTools.close(reader);
		}
	}

	protected Object evaluateExpression(String expression, IArgs args) throws IOException {
		try {
			return evaluator.evaluate(expression, args);
		} catch (EvaluationException e) {
			if (isStrict()) {
				throw new IOException("<error evaluating '" + expression + "' (" + e.getMessage() + ")>", e); //$NON-NLS-1$
			} else {
				return "${" + expression + "}";
			}
		}
	}

	public IStringEvaluator getEvaluator() {
		return evaluator;
	}

	public boolean isEscape() {
		return escape;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
