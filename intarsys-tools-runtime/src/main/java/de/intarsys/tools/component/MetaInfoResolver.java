/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.component;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} implementation giving access to a components meta
 * information.
 * 
 */
public class MetaInfoResolver implements IStringEvaluator {

	private final IMetaInfoSupport metaInfo;

	public MetaInfoResolver(IMetaInfoSupport metaInfo) {
		super();
		this.metaInfo = metaInfo;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {

		if ("name".equals(expression)) { //$NON-NLS-1$
			return metaInfo.getMetaInfo(IMetaInfoSupport.META_NAME);
		}
		if ("version".equals(expression)) { //$NON-NLS-1$
			return metaInfo.getMetaInfo(IMetaInfoSupport.META_VERSION);
		}
		if ("major".equals(expression)) { //$NON-NLS-1$
			String version = metaInfo.getMetaInfo(IMetaInfoSupport.META_VERSION);
			return VersionTools.getMajor(version);
		}
		if ("micro".equals(expression)) { //$NON-NLS-1$
			String version = metaInfo.getMetaInfo(IMetaInfoSupport.META_VERSION);
			return VersionTools.getMicro(version);
		}
		if ("minor".equals(expression)) { //$NON-NLS-1$
			String version = metaInfo.getMetaInfo(IMetaInfoSupport.META_VERSION);
			return VersionTools.getMinor(version);
		}

		String value = metaInfo.getMetaInfo(expression);
		if (value == null) {
			throw new EvaluationException("can't evaluate '" + expression + "'");
		}
		return value;
	}
}
