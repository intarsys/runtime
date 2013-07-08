/*
 * Copyright (c) 2008, intarsys consulting GmbH
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

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.reflect.FieldAccessException;

/**
 * The wrapper around {@link IFunctor} to make it an {@link IField}.
 */
public class FunctorField extends Member implements IField {

	private IFunctor getFunctor;

	private String name;

	private IFunctor setFunctor;

	public FunctorField(String name, IFunctor getFunctor, IFunctor setFunctor) {
		this.name = name;
		this.getFunctor = getFunctor;
		this.setFunctor = setFunctor;
	}

	public IClass getFieldType() {
		return null;
	}

	public String getName() {
		return name;
	}

	public Object getValue(Object receiver) throws FieldAccessException {
		try {
			IFunctorCall call = new FunctorCall(receiver, Args.create());
			return getFunctor.perform(call);
		} catch (FunctorInvocationException e) {
			Throwable cause = (e.getCause() == null) ? e : e.getCause();
			throw new FieldAccessException(getName(), cause);
		}
	}

	public Object setValue(Object receiver, Object value)
			throws FieldAccessException {
		try {
			IArgs args = Args.createIndexed(value);
			IFunctorCall call = new FunctorCall(receiver, args);
			return setFunctor.perform(call);
		} catch (FunctorInvocationException e) {
			Throwable cause = (e.getCause() == null) ? e : e.getCause();
			throw new FieldAccessException(getName(), cause);
		}
	}

}
