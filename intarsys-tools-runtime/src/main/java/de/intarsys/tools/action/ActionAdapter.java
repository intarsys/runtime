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
package de.intarsys.tools.action;

import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.IFunctorCallFactory;
import de.intarsys.tools.reflect.IMethodHandler;
import de.intarsys.tools.reflect.IMethodHandlerAccessibility;
import de.intarsys.tools.reflect.MethodException;
import de.intarsys.tools.reflect.MethodExecutionException;
import de.intarsys.tools.reflect.ObjectCreationException;

public abstract class ActionAdapter implements IAction, IMethodHandler, IMethodHandlerAccessibility {

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
	}

	@Override
	public Object getAttribute(Object key) {
		return null;
	}

	@Override
	public String getDescription() {
		return getTip();
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	@Override
	public Object invoke(Object receiver, IArgs args) throws MethodException {
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(this, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodExecutionException(getId(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		try {
			return perform(call);
		} catch (FunctorException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			throw new MethodExecutionException(getId(), cause);
		}
	}

	@Override
	public boolean isChecked(IFunctorCall call) {
		return false;
	}

	@Override
	public boolean isCheckStyle() {
		return false;
	}

	@Override
	public boolean isEnabled(IFunctorCall call) {
		return true;
	}

	@Override
	public boolean isInvokeEnabled(Object receiver, IArgs args) throws MethodException {
		IFunctorCall call;
		if (receiver instanceof IFunctorCallFactory) {
			try {
				call = ((IFunctorCallFactory) receiver).createFunctorCall(this, receiver, args);
			} catch (ObjectCreationException e) {
				throw new MethodExecutionException(getId(), e);
			}
		} else {
			call = new FunctorCall(receiver, args);
		}
		return isEnabled(call);
	}

	@Override
	public boolean isPushStyle() {
		return true;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		return null;
	}

	@Override
	public Object removeAttribute(Object key) {
		return null;
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
	}

	@Override
	public Object setAttribute(Object key, Object o) {
		return null;
	}

	public void touch() {
	}

}
