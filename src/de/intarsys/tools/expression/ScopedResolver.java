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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} that supports a list of other resolvers that are
 * each asked in turn for evaluating the result.
 */
public class ScopedResolver implements IStringEvaluator {

	/**
	 * Create a {@link ScopedResolver}, looking up its resolver scopes in the
	 * order defined in the argument list (most significant first).
	 * 
	 * @param evaluator
	 * @return
	 */
	public static ScopedResolver create(IStringEvaluator... evaluator) {
		ScopedResolver resolver = new ScopedResolver();
		for (int i = 0; i < evaluator.length; i++) {
			resolver.addResolver(evaluator[i]);
		}
		return resolver;
	}

	private List<IStringEvaluator> resolvers = new ArrayList<IStringEvaluator>();

	/**
	 * Add a new resolver at the end of the search sequence.
	 * 
	 * @param resolver
	 * 
	 */
	public void addResolver(IStringEvaluator resolver) {
		resolvers.add(resolver);
	}

	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		for (Iterator it = resolvers.iterator(); it.hasNext();) {
			IStringEvaluator resolver = (IStringEvaluator) it.next();
			try {
				return resolver.evaluate(expression, args);
			} catch (Exception e) {
				// try next
			}
		}
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IStringEvaluator popResolver() {
		if (resolvers.size() == 0) {
			return null;
		}
		return resolvers.remove(0);
	}

	/**
	 * Add a new resolver at the beginning of the search sequence.
	 * 
	 * @param resolver
	 * 
	 */
	public void pushResolver(IStringEvaluator resolver) {
		resolvers.add(0, resolver);
	}

	public void removeResolver(IStringEvaluator resolver) {
		resolvers.remove(resolver);
	}

}
