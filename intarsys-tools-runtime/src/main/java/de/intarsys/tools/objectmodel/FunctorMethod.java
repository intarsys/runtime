/*
 * Copyright (c) 2008, intarsys GmbH
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
package de.intarsys.tools.objectmodel;

import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorCancelledException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.reflect.MethodException;
import de.intarsys.tools.reflect.MethodExecutionException;

/**
 * The wrapper around an {@link IFunctor} to make it an {@link IMethod}.
 */
public class FunctorMethod extends Member implements IMethod {

	private IFunctor functor;

	private String name;

	public FunctorMethod(String name, IFunctor functor) {
		this.name = name;
		this.functor = functor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IClass[] getParameterTypes() {
		return new IClass[0];
	}

	@Override
	public IClass getReturnType() {
		return null;
	}

	@Override
	public Object invoke(Object receiver, IArgs args) throws MethodException {
		try {
			IFunctorCall call = new FunctorCall(receiver, args);
			return functor.perform(call);
		} catch (FunctorCancelledException e) {
			return null;
		} catch (FunctorException e) {
			Throwable cause = (e.getCause() == null) ? e : e.getCause();
			throw new MethodExecutionException(getName(), cause);
		}
	}

}
