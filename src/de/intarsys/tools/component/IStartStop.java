/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.component;

import java.util.Set;

/**
 * This describes the components ability to explicitly start or stop its
 * lifecycle. This is an alternative and more detailed description than
 * supported by IStartable and should be preferred.
 * 
 */
public interface IStartStop {

	/**
	 * Answer <code>true</code> if this object is started
	 * 
	 * @return Answer <code>true</code> if this object is started.
	 */
	public boolean isStarted();

	/**
	 * Start the component lifecycle. A RuntimeException is expected when
	 * starting the component fails.
	 * 
	 */
	public void start();

	/**
	 * Stop the component lifecycle. All resources should be freed. A
	 * RuntimeException is expected when stopping the component fails.
	 */
	public void stop();

	/**
	 * Ask the component if it agrees to end its lifecycle at the very moment.
	 * The component may deny this request, but it must be prepared anyway to be
	 * stopped.
	 * 
	 * @param visited
	 *            The optional set of already visited objects in the stop
	 *            request cycle.
	 */
	public boolean stopRequested(Set visited);
}
