package de.intarsys.tools.macro;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * Break from the next surrounding {@link MacroLoop}.
 * 
 * No more expression in the loop is executed.
 */
public class MacroBreak extends MacroFunctor {

	@Override
	public String getLabel() {
		return "Break";
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		throw new Break();
	}
}
