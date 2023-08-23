/*
 * Copyright (c) 2012, intarsys GmbH
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
package de.intarsys.tools.functor.common;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.CommonFunctor;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.reporter.IReporter;
import de.intarsys.tools.reporter.Reporter;

/**
 * Report the fact that a requested feature is not available yet.
 * 
 */
public class NotImplementedFunctor extends CommonFunctor<Void> {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	public static void reportNotImplemented() {
		Reporter.get().reportMessage(Msg.getString("NotImplementedFunctor.Message.Title"), //$NON-NLS-1$
				Msg.getString("NotImplementedFunctor.Message.Label"), IReporter.STYLE_NONE); //$NON-NLS-1$
	}

	@Override
	public Void perform(IFunctorCall call) throws FunctorException {
		if (!ArgTools.getBoolStrict(call.getArgs(), "silent", false)) {
			reportNotImplemented();
		}
		return null;
	}

}
