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

import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} implementation giving access to
 * {@link IFileEnvironment} related information.
 */
public class EnvironmentResolver implements IStringEvaluator {

	private IFileEnvironment environment;

	public EnvironmentResolver() {
		this(null);
	}

	public EnvironmentResolver(IFileEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		IFileEnvironment tempEnvironment = environment;
		if (environment == null) {
			tempEnvironment = FileEnvironment.get();
		}
		if ("basedir".equals(expression)) { //$NON-NLS-1$
			return tempEnvironment.getBaseDir().getAbsolutePath();
		}
		if ("profiledir".equals(expression)) { //$NON-NLS-1$
			return tempEnvironment.getProfileDir().getAbsolutePath();
		}
		if ("datadir".equals(expression)) { //$NON-NLS-1$
			return tempEnvironment.getDataDir().getAbsolutePath();
		}
		if ("workingdir".equals(expression)) { //$NON-NLS-1$
			return tempEnvironment.getWorkingDir().getAbsolutePath();
		}
		if ("tempdir".equals(expression)) { //$NON-NLS-1$
			return tempEnvironment.getTempDir().getAbsolutePath();
		}
		throw new EvaluationException("can't evaluate '" + expression + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
