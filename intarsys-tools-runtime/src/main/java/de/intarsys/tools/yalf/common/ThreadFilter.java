/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.yalf.common;

import de.intarsys.tools.yalf.api.IFilter;

/**
 * Only accept log requests within a dedicated thread.
 * <p>
 * This is for example suitable in a scenario where a log should be associated
 * with the processing of a functor object but the functor is executed in
 * different worker threads. If the functor activates its filter upon entry, all
 * activities can be sent to the same log, even from different workers. In this
 * case the thread associated with the filter is switched with "activate" and
 * "deactivate". These methods should always be used in a "finally" style block.
 * <p>
 * Another scenario is where a thread has a dedicated task and all its
 * activities have to be logged. Here the thread is associated at startup and
 * remains the same.
 * 
 */
public class ThreadFilter<R> implements IFilter<R> {

	private ThreadLocal<Integer> threadActivation = ThreadLocal.withInitial(() -> 0);

	public ThreadFilter(boolean activate) {
		super();
		if (activate) {
			activate();
		}
	}

	/**
	 * Mark the filter a active for the calling thread. This method returns true
	 * if this is the first attachment for this thread.
	 * 
	 * @return
	 */
	public boolean activate() {
		// you may be tempted to synchronize this, but you dont need to
		// use of tempActivation is confined to a single thread
		int tempActivation = threadActivation.get();
		threadActivation.set(tempActivation + 1);
		return tempActivation == 0;
	}

	/**
	 * Mark the filter as inactive for the calling thread. This method returns
	 * true if this was the last attachment for this thread.
	 * 
	 * @return
	 */
	public boolean deactivate() {
		int tempActivation = threadActivation.get();
		if (tempActivation == 1) {
			threadActivation.remove();
		} else {
			threadActivation.set(tempActivation - 1);
		}
		return tempActivation == 1;
	}

	public boolean isActive() {
		return threadActivation.get() > 0;
	}

	@Override
	public boolean isLoggable(R event) {
		return threadActivation.get() > 0;
	}

	public void reset() {
		threadActivation.remove();
	}

}
