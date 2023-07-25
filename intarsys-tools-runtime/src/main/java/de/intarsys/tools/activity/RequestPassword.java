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
package de.intarsys.tools.activity;

import de.intarsys.tools.authenticate.IPasswordProvider;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A password entry activity.
 * 
 * @param <P>
 */
public class RequestPassword<P extends IActivity<?>> extends Requester<Secret, P> {

	/**
	 * An accompanying {@link IPasswordProvider} to the {@link RequestPassword}
	 * activity.
	 *
	 */
	public static class PasswordProvider implements IPasswordProvider, IPrompter {

		private IMessage title;

		private IMessage message;

		public IMessage getMessage() {
			return message;
		}

		@Override
		public Secret getPassword() {
			Log.log(Level.DEBUG, "enter PIN start"); //$NON-NLS-1$
			RequestPassword<?> requester = new RequestPassword<>(null);
			requester.setTitle(getTitle());
			requester.setMessage(getMessage());
			requester.enter();
			try {
				Secret result = ExceptionTools.futureSimpleGet(requester);
				if (result == null) {
					return Secret.EMPTY;
				}
				return result;
			} catch (Exception e) {
				return Secret.EMPTY;
			} finally {
				Log.log(Level.DEBUG, "enter PIN ready"); //$NON-NLS-1$
			}
		}

		public IMessage getTitle() {
			return title;
		}

		@Override
		public void setMessage(IMessage message) {
			this.message = message;
		}

		@Override
		public void setTitle(IMessage title) {
			this.title = title;
		}
	}

	private static final ILogger Log = PACKAGE.Log;

	public RequestPassword(P parent) {
		super(parent);
	}

}
