package de.intarsys.tools.codeexit;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * An {@link IFunctor} that allows for complete wrapping a {@link CodeExit}
 * definition and call;
 * 
 * @param <T>
 */
public class CodeExitLauncher<T> implements IFunctor<T> {

	@Override
	public T perform(IFunctorCall call) throws FunctorException {
		Object receiver = call.getReceiver();
		IArgs args = ArgTools.getArgs(call.getArgs(), "args", Args.create());
		String type = (String) call.getArgs().get("type");
		String source = (String) call.getArgs().get("source");
		//
		IFunctorCall codeExitCall = new FunctorCall(receiver, args);
		CodeExit exit = new CodeExit(null);
		exit.setType(type);
		exit.setSource(source);
		return (T) exit.perform(codeExitCall);
	}
}
