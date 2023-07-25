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

import java.lang.reflect.Method;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectTools;

/**
 * An {@link IStringEvaluator} that provides reflective access to an objects
 * properties.
 * 
 */
public class ReflectiveResolver extends ContainerResolver {

	private final Object object;

	private Method resolveAny;

	public ReflectiveResolver(Object object) {
		super(PATH_SEPARATOR, false, null);
		this.object = object;
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args) throws EvaluationException {
		Class<?> clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (int index = methods.length - 1; index >= 0; index--) {
			Method method = methods[index];
			ResolveProperty annotProperty = method.getAnnotation(ResolveProperty.class);
			if (annotProperty != null) {
				String property = annotProperty.property();
				if (expression.equals(property)) {
					try {
						return method.invoke(object);
					} catch (Exception e) {
						throw new EvaluationException("can't evaluate '" + expression + "' (" + method + ")", e);
					}
				}
			} else if (resolveAny == null) {
				ResolveAny annotAny = method.getAnnotation(ResolveAny.class);
				if (annotAny != null) {
					resolveAny = method;
				}
			}
		}
		if (resolveAny != null) {
			try {
				return resolveAny.invoke(object, expression, args);
			} catch (Exception e) {
				// retry reflection
			}
		}
		try {
			return ObjectTools.get(object, expression);
		} catch (Exception e) {
			throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
