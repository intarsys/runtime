package de.intarsys.tools.macro;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * Continue execution for the next surrounding {@link MacroLoop}.
 * 
 * Execution of any following expression is omitted, the defined "after"
 * expression of the loop is executed immediately
 */
public class MacroContinue extends MacroFunctor<Void> {

	@Override
	protected String getDefaultLabel() {
		return "continue";
	}

	@Override
	public Void perform(IFunctorCall call) throws FunctorException {
		throw new Continue();
	}

}
