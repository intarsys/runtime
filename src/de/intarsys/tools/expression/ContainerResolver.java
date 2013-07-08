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

import java.util.List;
import java.util.Map;

import de.intarsys.tools.adapter.AdapterOutlet;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.variable.IVariableNamespace;

/**
 * A common superclass for implementing an {@link IStringEvaluator} that can
 * navigate along a "." separated path in an expression.
 * 
 */
abstract public class ContainerResolver implements IStringEvaluator {

	private IStringEvaluator exceptionResolver;

	final private char separator;

	public ContainerResolver() {
		this('.');
	}

	public ContainerResolver(char separator) {
		super();
		this.separator = separator;
	}

	abstract protected Object basicEvaluate(String expression, IArgs args)
			throws EvaluationException;

	protected IStringEvaluator createStringEvaluator(Object object) {
		if (object instanceof IStringEvaluator) {
			return (IStringEvaluator) object;
		}
		if (object instanceof IStringEvaluatorSupport) {
			return ((IStringEvaluatorSupport) object).getStringEvaluator();
		}
		if (object instanceof Map) {
			return new MapResolver((Map) object, false);
		}
		if (object instanceof List) {
			return new ListResolver((List) object);
		}
		if (object instanceof IArgs) {
			return new StaticArgsResolver((IArgs) object);
		}
		if (object instanceof IVariableNamespace) {
			return new VariableNamespaceResolver((IVariableNamespace) object);
		}
		if (object instanceof Object[]) {
			return new ArrayResolver((Object[]) object);
		}
		if (object == null) {
			return null;
		}
		IStringEvaluator evaluator = AdapterOutlet.get().getAdapter(object,
				IStringEvaluator.class);
		if (evaluator == null) {
			return new ReflectiveResolver(object);
		}
		return evaluator;
	}

	final public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		String tempExpr = expression;
		int pos = tempExpr.indexOf(getSeparator());
		if (pos == -1) {
			return evaluatePrefix(tempExpr, args);
		} else {
			String name = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result = evaluatePrefix(name, args);
			IStringEvaluator subResolver = createStringEvaluator(result);
			if (subResolver != null) {
				return subResolver.evaluate(pathTrail, args);
			}
		}
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected Object evaluatePrefix(String prefix, IArgs args)
			throws EvaluationException {
		Object result;
		try {
			result = basicEvaluate(prefix, args);
		} catch (EvaluationException e) {
			if (exceptionResolver != null) {
				result = exceptionResolver.evaluate(prefix, args);
			} else {
				throw e;
			}
		}
		if (result instanceof IValueHolder) {
			result = ((IValueHolder) result).get();
		}
		return result;
	}

	public IStringEvaluator getExceptionResolver() {
		return exceptionResolver;
	}

	final public char getSeparator() {
		return separator;
	}

	public void setExceptionResolver(IStringEvaluator exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}

}
