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
import de.intarsys.tools.reflect.InvocableMethod;

/**
 * A common superclass for activities reporting information to the user.
 * 
 * The message (respective its code) can be used to uniquely identify the
 * reporting context.
 * 
 * @param <R>
 * @param <P>
 */
public abstract class Reporter<R, P extends IActivity<?>> extends CommonActivity<R, P> {

	private IMessage message;

	protected Reporter() {
		super();
	}

	protected Reporter(P parent) {
		super(parent);
	}

	@Override
	public String getLogLabel() {
		IMessage tmpMessage = getMessage();
		if (tmpMessage != null) {
			return super.getLogLabel() + "#" + tmpMessage.getCode();
		}
		return super.getLogLabel();
	}

	/**
	 * The message to be reported to the user.
	 * 
	 * The {@link IMessage#getCode()} may be used to programatically identify
	 * the report context.
	 * 
	 * @return
	 */
	@InvocableMethod
	public IMessage getMessage() {
		return message;
	}

	/**
	 * Assign the {@link IMessage} to be reported.
	 * 
	 * @param message
	 */
	public void setMessage(IMessage message) {
		IMessage oldValue = this.message;
		this.message = message;
		triggerChanged("message", oldValue, message);
	}

}
