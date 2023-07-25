package de.intarsys.tools.codeexit;

import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * A tool class for dealing with {@link CodeExit} stuff.
 * 
 */
public final class CodeExitTools {

	public static Object perform(String type, String source, Object receiver, IArgs args) throws FunctorException {
		IFunctorCall call = new FunctorCall(receiver, args);
		CodeExit exit = new CodeExit(receiver);
		exit.setType(type);
		exit.setSource(source);
		return exit.perform(call);
	}

	private CodeExitTools() {
	}

}
