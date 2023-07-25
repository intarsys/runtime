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

import java.util.concurrent.Future;

import de.intarsys.tools.concurrent.ITaskCallbackSupport;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.state.IStateHolder;

/**
 * The abstraction of an activity pattern. Depending on the
 * {@link IActivityHandler}, the pattern may have different implementations.
 * 
 * The {@link IActivity} can be both modal or modeless, so that {@link #enter()}
 * returns either after the required interaction is finished or immediately.
 * Blocking behavior is modeled using the {@link Future} and
 * {@link ITaskCallbackSupport} interface.
 * 
 * Modeless {@link IActivity} instances may be stateful and can be changed in
 * their lifetime (e.g. {@link ReportStatus}). These objects SHOULD implement
 * {@link INotificationSupport}.
 * 
 * @param <R>
 *            The result type for the activity.
 */
public interface IActivity<R> extends Future<R>, ITaskCallbackSupport<R>, IStateHolder {

	/**
	 * Start the activity lifecycle.
	 * 
	 * @return The receiver is returned
	 */
	public IActivity<R> enter();

}
