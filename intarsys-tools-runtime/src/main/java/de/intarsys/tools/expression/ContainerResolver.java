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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import de.intarsys.tools.adapter.AdapterOutlet;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.variable.IVariableNamespace;

/**
 * A common superclass for implementing an {@link IStringEvaluator} that can navigate along a "." separated path in an
 * expression.
 * 
 * The strict flag decides if we throw an error when an expression is encountered that can not be resolved or simply
 * return a default result value. The strict flag is true by default.
 */
public abstract class ContainerResolver implements IStringEvaluator {

	public static final char PATH_SEPARATOR = '.';

	private IStringEvaluator exceptionResolver;

	private final char separator;

	private boolean strict;

	private Object notFoundResult;

	protected ContainerResolver() {
		this(PATH_SEPARATOR);
	}

	protected ContainerResolver(char separator) {
		this(separator, true, null);
	}

	protected ContainerResolver(char separator, boolean strict, IStringEvaluator exceptionResolver) {
		super();
		this.separator = separator;
		this.strict = strict;
		this.exceptionResolver = exceptionResolver;
	}

	protected abstract Object basicEvaluate(String expression, IArgs args) throws EvaluationException;

	protected Object basicEvaluateUnwrapped(String expression, IArgs args) throws EvaluationException {
		Object result = basicEvaluate(expression, args);
		if (result instanceof IValueHolder) {
			result = ((IValueHolder) result).get();
		}
		if (result instanceof Supplier) {
			result = ((Supplier) result).get();
		}
		return result;
	}

	protected IStringEvaluator createStringEvaluator(Object object) {
		if (object == null) {
			return null;
		}
		IStringEvaluator evaluator;
		if (object instanceof IStringEvaluator) {
			// do not customize!!
			return (IStringEvaluator) object;
		} else if (object instanceof IStringEvaluatorSupport) {
			// do not customize!!
			return ((IStringEvaluatorSupport) object).getStringEvaluator();
		} else if (object instanceof Map) {
			evaluator = new MapResolver((Map) object);
		} else if (object instanceof List) {
			evaluator = new ListResolver((List) object);
		} else if (object instanceof IArgs) {
			evaluator = new StaticArgsResolver((IArgs) object);
		} else if (object instanceof IVariableNamespace) {
			evaluator = new VariableNamespaceResolver((IVariableNamespace) object);
		} else if (object instanceof Object[]) {
			evaluator = new ArrayResolver((Object[]) object);
		} else {
			evaluator = AdapterOutlet.get().getAdapter(object, IStringEvaluator.class);
		}
		if (evaluator == null) {
			evaluator = new ReflectiveResolver(object);
		}
		if (evaluator instanceof ContainerResolver) {
			((ContainerResolver) evaluator).setStrict(isStrict());
		}
		return evaluator;
	}

	@Override
	public final Object evaluate(String expression, IArgs args) throws EvaluationException {
		int pos = expression.indexOf(getSeparator());
		if (pos == -1) {
			try {
				return basicEvaluateUnwrapped(expression, args);
			} catch (EvaluationException e) {
				if (exceptionResolver != null) {
					return exceptionResolver.evaluate(expression, args);
				} else {
					throw e;
				}
			}
		} else {
			String exprLead = expression.substring(0, pos);
			String exprTrail = expression.substring(pos + 1);
			Object result;
			try {
				result = basicEvaluateUnwrapped(exprLead, args);
			} catch (EvaluationException e) {
				if (exceptionResolver != null) {
					return exceptionResolver.evaluate(expression, args);
				} else {
					/*
					 * for a container we throw the special NamespaceNotFound, indicating that a first
					 * level path segment was not resolved
					 */
					throw new NamespaceNotFound(e.getMessage(), e.getCause());
				}
			}
			IStringEvaluator subResolver = createStringEvaluator(result);
			if (subResolver != null) {
				try {
					/*
					 * while nested "NamespaceNotFound" is no longer namespace...
					 */
					return subResolver.evaluate(exprTrail, args);
				} catch (NamespaceNotFound e) {
					throw new EvaluationException(e.getMessage(), e.getCause());
				}
			}
		}
		return notFound(expression);
	}

	protected EvaluationException failed(String expression) throws EvaluationException {
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IStringEvaluator getExceptionResolver() {
		return exceptionResolver;
	}

	public Object getNotFoundResult() {
		return notFoundResult;
	}

	public final char getSeparator() {
		return separator;
	}

	public boolean isStrict() {
		return strict;
	}

	protected Object notFound(String expression) throws EvaluationException {
		if (isStrict()) {
			throw failed(expression);
		} else {
			return getNotFoundResult();
		}
	}

	public void setExceptionResolver(IStringEvaluator exceptionResolver) {
		this.exceptionResolver = exceptionResolver;
	}

	public void setNotFoundResult(Object notFoundResult) {
		this.notFoundResult = notFoundResult;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
