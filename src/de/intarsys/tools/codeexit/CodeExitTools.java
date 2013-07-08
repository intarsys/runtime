package de.intarsys.tools.codeexit;

import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * A tool class for dealing with {@link CodeExit} stuff.
 * 
 */
public class CodeExitTools {

	static public Object perform(String type, String source, Object receiver,
			IArgs args) throws FunctorInvocationException {
		IFunctorCall call = new FunctorCall(receiver, args);
		CodeExit exit = new CodeExit(receiver);
		exit.setType(type);
		exit.setSource(source);
		return exit.perform(call);
	}

}
