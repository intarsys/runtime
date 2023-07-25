///*
// * Copyright (c) 2007, intarsys GmbH
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * - Redistributions of source code must retain the above copyright notice,
// *   this list of conditions and the following disclaimer.
// *
// * - Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * - Neither the name of intarsys nor the names of its contributors may be used
// *   to endorse or promote products derived from this software without specific
// *   prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
package de.intarsys.tools.activity;

import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.notice.INotice;
import de.intarsys.tools.reflect.InvocableMethod;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.valueholder.ObjectHolder;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * An abstract activity that interacts with a user and requests some
 * information.
 * 
 * The interaction may be decorated with an
 * <ul>
 * <li>optional title</li>
 * <li>optional message/li>
 * <li>optional severity/li>
 * </ul>
 * 
 * @param <R>
 * @param <P>
 */
public abstract class Requester<R, P extends IActivity<?>> extends CommonActivity<R, P> {

	private static final ILogger Log = PACKAGE.Log;

	private static final IMessageBundle Msg = PACKAGE.Messages;

	public static final IMessage TOGGLE_MESSAGE = Msg.getMessage("Requester.ToggleMessage"); //$NON-NLS-1$

	private int severity = INotice.SEVERITY_INFO;

	private IMessage message;

	private IValueHolder<Boolean> toggleValue = new ObjectHolder<>(Boolean.FALSE);

	private IMessage toggleMessage;

	private IMessage title;

	protected Requester(P parent) {
		super(parent);
	}

	/**
	 * The optional message associated with this request.
	 * 
	 * @return
	 */
	@InvocableMethod
	public IMessage getMessage() {
		return message;
	}

	/**
	 * The severity associated with this request.
	 * 
	 * @return
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * The optional title associated with this request.
	 * 
	 * @return
	 */
	public IMessage getTitle() {
		return title;
	}

	public IMessage getToggleMessage() {
		return toggleMessage;
	}

	public boolean isToggleValue() {
		if (toggleValue == null) {
			return false;
		}
		return toggleValue.get().booleanValue();
	}

	@Override
	protected void logEnterAfter() {
		Log.info("{} request '{}', '{}'", getLogLabel(), getTitle(), getMessage());
	}

	public void setMessage(IMessage message) {
		IMessage oldValue = this.message;
		this.message = message;
		triggerChanged("message", oldValue, message);
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public void setTitle(IMessage title) {
		this.title = title;
	}

	public void setToggleMessage(IMessage toggleMessage) {
		this.toggleMessage = toggleMessage;
	}

	public void setToggleValue(boolean toggleValue) {
		if (this.toggleValue == null) {
			return;
		}
		this.toggleValue.set(toggleValue);
	}

	public void setToggleValue(IValueHolder<Boolean> toggleValue) {
		this.toggleValue = toggleValue;
	}
}
