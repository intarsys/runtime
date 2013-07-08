/*
 * Copyright (c) 2007, intarsys consulting GmbH
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

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

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

	protected IFunctor createFunctor(CodeExit codeExit)
			throws FunctorInvocationException {
		checkCodeExitSource(codeExit);
		IFunctor functor = null;
		try {
			Class clazz = Class.forName(codeExit.getSource(), true,
					codeExit.getClassLoader());
			functor = (IFunctor) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new FunctorInvocationException(e);
		} catch (InstantiationException e) {
			throw new FunctorInvocationException(e);
		} catch (IllegalAccessException e) {
			throw new FunctorInvocationException(e);
		}
		if (functor instanceof IElementConfigurable) {
			IElement handlerElement = codeExit.getHandlerElement();
			if (handlerElement != null) {
				try {
					((IElementConfigurable) functor).configure(handlerElement);
				} catch (ConfigurationException e) {
					throw new FunctorInvocationException(e);
				}
			}
		}
		return functor;
	}

	public Object perform(CodeExit codeExit, IFunctorCall call)
			throws FunctorInvocationException {
		IFunctor functor = (IFunctor) codeExit.getCompiledSource();
		if (functor == null) {
			functor = createFunctor(codeExit);
			codeExit.setCompiledSource(functor);
		}
		try {
			return functor.perform(call);
		} catch (FunctorInvocationException e) {
			throw e;
		} catch (Error e) {
			throw new FunctorInvocationException(e);
		}
	}
}
