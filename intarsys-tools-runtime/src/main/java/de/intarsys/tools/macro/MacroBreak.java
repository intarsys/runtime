package de.intarsys.tools.macro;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * Break from the next surrounding {@link MacroLoop}.
 * 
 * No more expression in the loop is executed.
 */
public class MacroBreak extends MacroFunctor<Void> {

	@Override
	protected String getDefaultLabel() {
		return "break";
	}

	@Override
	public Void perform(IFunctorCall call) throws FunctorException {
		throw new Break();
	}
}
