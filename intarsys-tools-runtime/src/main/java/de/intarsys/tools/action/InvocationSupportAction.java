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
package de.intarsys.tools.action;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.IInvocationSupport;
import de.intarsys.tools.reflect.IInvocationSupportAccessibility;
import de.intarsys.tools.reflect.MethodException;

/**
 * This action wraps functionality that supports performing the action
 * implementation via the "reflective" interface {@link IInvocationSupport}.
 * This way the owner object may publish {@link IAction} instances without
 * redefining the behavior in subclasses or using {@link IFunctor} objects.
 */
public class InvocationSupportAction extends Action {

	private IInvocationSupport invocationSupport;

	private boolean definedEnabled;

	private boolean definedChecked;

	private String invocationName;

	public InvocationSupportAction() {
		super();
	}

	public InvocationSupportAction(Object owner) {
		super(owner);
	}

	public InvocationSupportAction(Object owner, boolean checked) {
		super(owner, checked);
	}

	public InvocationSupportAction(String id, Object owner) {
		super(id, owner);
	}

	public InvocationSupportAction(String id, Object owner, boolean checked) {
		super(id, owner, checked);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		String value = element.attributeValue("invoke", null); //$NON-NLS-1$
		if (value != null) {
			setInvocationName(value);
		}
	}

	public String getInvocationName() {
		if (invocationName == null) {
			return getId();
		}
		return invocationName;
	}

	public IInvocationSupport getInvocationSupport() {
		return invocationSupport;
	}

	protected boolean isDefinedChecked() {
		return definedChecked;
	}

	protected boolean isDefinedEnabled() {
		return definedEnabled;
	}

	@Override
	public boolean isEnabled(IFunctorCall call) {
		if (isDefinedEnabled()) {
			return super.isEnabled(call);
		} else if (getInvocationSupport() instanceof IInvocationSupportAccessibility) {
			try {
				return ((IInvocationSupportAccessibility) getInvocationSupport()).isInvokeEnabled(getInvocationName(),
						call.getArgs());
			} catch (MethodException e) {
				return false;
			}
		} else {
			return super.isEnabled(call);
		}
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		if (getInvocationSupport() != null) {
			try {
				return getInvocationSupport().invoke(getInvocationName(), call.getArgs());
			} catch (MethodException e) {
				Throwable cause = e.getCause() == null ? e : e.getCause();
				if (cause instanceof FunctorException) {
					throw (FunctorException) cause;
				} else {
					throw new FunctorExecutionException(cause);
				}
			}
		} else {
			return super.perform(call);
		}
	}

	@Override
	public void setChecked(boolean checked) {
		setDefinedChecked(true);
		super.setChecked(checked);
	}

	protected void setDefinedChecked(boolean definedChecked) {
		this.definedChecked = definedChecked;
	}

	protected void setDefinedEnabled(boolean definedEnabled) {
		this.definedEnabled = definedEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		setDefinedEnabled(true);
		super.setEnabled(enabled);
	}

	public void setInvocationName(String invocationName) {
		this.invocationName = invocationName;
	}

	@Override
	public void setOwner(Object owner) {
		super.setOwner(owner);
		if (owner instanceof IInvocationSupport) {
			invocationSupport = (IInvocationSupport) owner;
		}
	}

	@Override
	public String toString() {
		try {
			return "InvocationSupportAction '" + getId() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RuntimeException e) {
			return "<unprintable InvocationSupportAction>"; //$NON-NLS-1$
		}
	}
}
