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
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;

public abstract class ActionForwarder extends ActionAdapter {

	protected static final IAction NONE = new ActionAdapter() {
	};

	private IAction action;

	protected ActionForwarder() {
	}

	protected ActionForwarder(IAction action) {
		super();
		this.action = action;
	}

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		getAction().addNotificationListener(type, listener);
	}

	protected IAction getAction() {
		if (action == null) {
			action = resolve();
		}
		return action;
	}

	@Override
	public Object getAttribute(Object key) {
		return getAction().getAttribute(key);
	}

	@Override
	public String getDescription() {
		return getAction().getDescription();
	}

	@Override
	public String getIconName() {
		return getAction().getIconName();
	}

	@Override
	public String getId() {
		return getAction().getId();
	}

	@Override
	public String getLabel() {
		return getAction().getLabel();
	}

	@Override
	public String getTip() {
		return getAction().getTip();
	}

	@Override
	public boolean isChecked(IFunctorCall call) {
		return getAction().isChecked(call);
	}

	@Override
	public boolean isCheckStyle() {
		return getAction().isCheckStyle();
	}

	@Override
	public boolean isEnabled(IFunctorCall call) {
		return getAction().isEnabled(call);
	}

	@Override
	public boolean isPushStyle() {
		return getAction().isPushStyle();
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		return getAction().perform(call);
	}

	@Override
	public Object removeAttribute(Object key) {
		return getAction().removeAttribute(key);
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		getAction().removeNotificationListener(type, listener);
	}

	protected IAction resolve() {
		return NONE;
	}

	protected void setAction(IAction action) {
		this.action = action;
	}

	@Override
	public Object setAttribute(Object key, Object o) {
		return getAction().setAttribute(key, o);
	}

}
