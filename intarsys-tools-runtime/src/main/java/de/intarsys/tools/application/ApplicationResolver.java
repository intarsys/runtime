/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.application;

import de.intarsys.tools.component.VersionTools;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} implementation giving access to some meta
 * information of the {@link Application}.
 * 
 */
public class ApplicationResolver implements IStringEvaluator {

	private final Object application;

	public ApplicationResolver() {
		this.application = null;
	}

	public ApplicationResolver(Object application) {
		super();
		this.application = application;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if ("name".equals(expression)) { //$NON-NLS-1$
			return ApplicationTools.getApplicationLabel(getApplication());
		}
		if ("version".equals(expression)) { //$NON-NLS-1$
			return ApplicationTools.getApplicationVersion(getApplication());
		}
		if ("major".equals(expression)) { //$NON-NLS-1$
			String version = ApplicationTools.getApplicationVersion(getApplication());
			return VersionTools.getMajor(version);
		}
		if ("micro".equals(expression)) { //$NON-NLS-1$
			String version = ApplicationTools.getApplicationVersion(getApplication());
			return VersionTools.getMicro(version);
		}
		if ("minor".equals(expression)) { //$NON-NLS-1$
			String version = ApplicationTools.getApplicationVersion(getApplication());
			return VersionTools.getMinor(version);
		}
		throw new EvaluationException("can't evaluate '" + expression + "'");
	}

	protected Object getApplication() {
		if (application == null) {
			return Application.get();
		}
		return application;
	}
}
