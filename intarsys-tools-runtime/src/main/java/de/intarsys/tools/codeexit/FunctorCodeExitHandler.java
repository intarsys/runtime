/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.codeexit;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.factory.CommonFactory;
import de.intarsys.tools.factory.FactoryTools;
import de.intarsys.tools.factory.IFactory;
import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.FunctorInternalException;
import de.intarsys.tools.functor.FunctorTools;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.IElement;

/**
 * An {@link ICodeExitHandler} that delegates to a java {@link IFunctor}
 * implementation class.
 * 
 * The source is not expanded.
 */
public class FunctorCodeExitHandler extends CommonCodeExitHandler {
	/**
	 * 
	 */
	public FunctorCodeExitHandler() {
		super();
	}

	protected IFunctor createFunctor(CodeExit codeExit) throws FunctorException {
		checkCodeExitSource(codeExit);
		IFunctor functor = null;
		try {
			IFactory factory = FactoryTools.lookupFactoryFuzzy(codeExit.getSource(), codeExit.getClassLoader());
			InstanceSpec spec = InstanceSpec.createFromFactory(Object.class, factory, Args.create());
			IElement handlerElement = codeExit.getHandlerElement();
			if (handlerElement != null) {
				spec.getArgs().put(CommonFactory.ARG_CONFIGURATION, handlerElement);
			}
			spec.getArgs().put(CommonFactory.ARG_CLASSLOADER, codeExit.getClassLoader());
			spec.getArgs().put(CommonFactory.ARG_CONTEXT, codeExit.getOwner());
			// the spec may realize to a plain Runnable or Callable!
			functor = FunctorTools.toFunctorResult(spec.createInstance());
		} catch (Exception e) {
			throw ExceptionTools.createTyped(e, FunctorExecutionException.class);
		}
		if (functor == null) {
			throw new FunctorInternalException("can not create IFunctor from " + codeExit.getSource());
		}
		return functor;
	}

	@Override
	public Object perform(CodeExit codeExit, IFunctorCall call) throws FunctorException {
		IFunctor functor = (IFunctor) codeExit.getCompiledSource();
		if (functor == null) {
			functor = createFunctor(codeExit);
			codeExit.setCompiledSource(functor);
		}
		try {
			return functor.perform(call);
		} catch (FunctorException e) {
			throw e;
		} catch (Throwable e) {
			throw new FunctorExecutionException(e);
		}
	}
}
