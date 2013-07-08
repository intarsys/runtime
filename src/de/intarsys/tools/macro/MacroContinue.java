package de.intarsys.tools.macro;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * Continue execution for the next surrounding {@link MacroLoop}.
 * 
 * Execution of any following expression is omitted, the defined "after"
 * expression of the loop is executed immediately
 */
public class MacroContinue extends MacroFunctor {

	@Override
	public String getLabel() {
		return "Continue";
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		throw new Continue();
	}

}
