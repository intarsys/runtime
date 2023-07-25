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

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;

/**
 * This action wraps functionality in an object that supports the action via the
 * "reflective" interface {@link IActionHandler}. This way the owner object may
 * publish {@link IAction} instances without redefining the behavior in
 * subclasses or using {@link IFunctor} objects.
 */
public class ActionHandlerAction extends Action {

	/** This is a reference to owner if it implements {@link IActionHandler} */
	private IActionHandler actionHandler;

	private boolean definedEnabled;

	private boolean definedChecked;

	public ActionHandlerAction() {
		super();
	}

	public ActionHandlerAction(Object owner) {
		super(owner);
	}

	public ActionHandlerAction(Object owner, boolean checked) {
		super(owner, checked);
	}

	public ActionHandlerAction(String id, Object owner) {
		super(id, owner);
	}

	public ActionHandlerAction(String id, Object owner, boolean checked) {
		super(id, owner, checked);
	}

	protected IActionHandler getActionHandler() {
		return actionHandler;
	}

	@Override
	public boolean isChecked(IFunctorCall call) {
		if (isDefinedChecked()) {
			return super.isChecked(call);
		} else {
			if (getActionHandler() != null) {
				return getActionHandler().isChecked(this, call);
			} else {
				return super.isChecked(call);
			}
		}
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
		} else if (getActionHandler() != null) {
			return getActionHandler().isEnabled(this, call);
		} else {
			return super.isEnabled(call);
		}
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		if (getActionHandler() != null) {
			return getActionHandler().perform(this, call);
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

	@Override
	public void setOwner(Object owner) {
		super.setOwner(owner);
		if (owner instanceof IActionHandler) {
			actionHandler = (IActionHandler) owner;
		}
	}

	@Override
	public String toString() {
		try {
			return "ActionHandlerAction '" + getId() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RuntimeException e) {
			return "<unprintable ActionSupportAction>"; //$NON-NLS-1$
		}
	}
}
