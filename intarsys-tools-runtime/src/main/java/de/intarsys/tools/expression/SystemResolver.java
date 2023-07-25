/*
/*
 * Copyright (c) 2012, intarsys GmbH
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

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.system.SystemTools;

/**
 * An {@link IStringEvaluator} implementation giving access common system state.
 *
 * <ul>
 * <li>getenv</li>
 * <li>properties</li>
 * <li>architecture</li>
 * </ul>
 */
public class SystemResolver implements IStringEvaluator {
	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if (expression == null) {
			throw new EvaluationException("expression is null"); //$NON-NLS-1$
		}

		if ("architecture".equals(expression)) { //$NON-NLS-1$
			String arch = SystemTools.getOSArch();
			return arch != null && arch.contains("64") //$NON-NLS-1$
					? "64-bit" //$NON-NLS-1$
					: "32-bit"; //$NON-NLS-1$
		}

		int indexOfSeparator = expression.indexOf('.');
		if (indexOfSeparator >= 0) {
			String namespace = expression.substring(0, indexOfSeparator);
			String localExpression = expression.substring(indexOfSeparator + 1);

			if ("properties".equals(namespace)) { //$NON-NLS-1$
				return System.getProperty(localExpression);
			}

			if ("getenv".equals(namespace)) { //$NON-NLS-1$
				return System.getenv(localExpression);
			}
		}

		throw new EvaluationException(String.format("can't evaluate '%s'", expression)); //$NON-NLS-1$
	}
}
