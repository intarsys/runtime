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

import java.util.Iterator;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;

/**
 * An {@link IStringEvaluator} resolving a complete IBinding from statically
 * provided {@link IArgs}.
 * 
 */
public class StaticArgEntriesResolver extends ContainerResolver {

	private static final Object NA = new Object();

	private IArgs args;

	public StaticArgEntriesResolver(IArgs args) {
		super();
		this.args = args;
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs pArgs)
			throws EvaluationException {
		try {
			int index = Integer.parseInt(expression);
			if (index >= 0 && index < args.size()) {
				Iterator<IBinding> bindings = args.bindings();
				IBinding binding = null;
				for (int i = 0; i <= index; i++) {
					binding = bindings.next();
				}
				String result = null;
				if (binding.getName() == null) {
					result = String.valueOf(binding.getValue());
				} else {
					result = binding.getName() + "=" + binding.getValue();
				}
				return result;
			}
		} catch (NumberFormatException e) {
			//
		}
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
