/*
 * Copyright (c) 2014, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.state;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;

/**
 * A "mixin" implementation for {@link IStateHolder}. This implementation is
 * prepared for handling update propagation on behalf of the object where it is
 * plugged in.
 * 
 */
public class ComplexStateHolder implements IStateHolder, IMessageBundleSupport {

	private StateVector stateVector = new StateVector();

	private final Object owner;

	private final INotificationListener listener;

	private IMessageBundle messageBundle;

	public ComplexStateHolder(Object owner, INotificationListener listener) {
		this.owner = owner;
		this.listener = listener;
		enterState(AtomicState.NEW);
	}

	@Override
	public void enterState(IState pState) {
		if (pState == null) {
			throw new NullPointerException();
		}
		if (!(pState instanceof CommonState)) {
			throw new IllegalArgumentException();
		}
		IState oldState = null;
		IState newState;
		synchronized (this) {
			if (pState == this.stateVector) {
				// @todo
				return;
			}
			StateVector expiredState = stateVector;
			ConcurrentState cState = ConcurrentState.create((CommonState) pState);
			oldState = stateVector.lookupState(cState.getContext());
			cState = (ConcurrentState) cState.attach(this);
			this.stateVector = stateVector.putState(cState);
			newState = cState;
			expiredState.attach(null);
		}
		triggerChanged(IState.ATTR_STATE, oldState, newState);
	}

	@Override
	public IMessageBundle getMessageBundle() {
		if (messageBundle != null) {
			return messageBundle;
		}
		if (owner instanceof IMessageBundleSupport) {
			return ((IMessageBundleSupport) owner).getMessageBundle();
		}
		return null;
	}

	@Override
	public IState getState() {
		return getStateVector().unwrap();
	}

	protected StateVector getStateVector() {
		synchronized (this) {
			return stateVector;
		}
	}

	public void setMessageBundle(IMessageBundle messageBundle) {
		this.messageBundle = messageBundle;
	}

	@Override
	public String toString() {
		return "StateHolder@" + getStateVector().getId();

	}

	protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		if (listener != null) {
			listener.handleEvent(new AttributeChangedEvent(owner, attribute, oldValue, newValue));
		}
	}

}
