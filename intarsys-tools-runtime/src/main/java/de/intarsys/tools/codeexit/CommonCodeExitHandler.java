package de.intarsys.tools.codeexit;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorInternalException;
import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for implementing {@link ICodeExitHandler} instances.
 * 
 */
public abstract class CommonCodeExitHandler implements ICodeExitHandler, ICodeExitMetaHandler {

	protected void checkCodeExitSource(CodeExit codeExit) throws FunctorException {
		if (StringTools.isEmpty(codeExit.getSource())) {
			throw new FunctorInternalException("CodeExit type '" //$NON-NLS-1$
					+ codeExit.getType() + "' source can't be empty"); //$NON-NLS-1$
		}
	}

	@Override
	public boolean exists(CodeExit codeExit) {
		return true;
	}

}
