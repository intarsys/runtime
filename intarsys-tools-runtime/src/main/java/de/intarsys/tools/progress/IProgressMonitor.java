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
package de.intarsys.tools.progress;

/**
 * An object that is able to monitor the progress of an activity.
 * 
 */
public interface IProgressMonitor {

	/**
	 * Mark the beginning of the activity.
	 * 
	 * @param name
	 * @param totalWork
	 */
	public void begin(String name, float totalWork);

	/**
	 * Notify that the end of the activity has come.
	 */
	public void end();

	/**
	 * <code>true</code> if this activity should be cancelled.
	 * 
	 * @return
	 */
	public boolean isCancelled();

	/**
	 * Mark the beginning of a named subtask within the activity.
	 * 
	 * @param name
	 */
	public void subTask(String name);

	/**
	 * Mark that we have finished <code>amount</code> units of work. amount is
	 * the relative measure only, accumulation is done in the monitor itself! Be
	 * aware that amount may be a negative value!
	 * 
	 * If for example you have a loop within a task, the best practice code
	 * pattern looks like this:
	 * 
	 * <pre>
	 * monitor.begin(&quot;task&quot;, n);
	 * try {
	 * 	for (int i = 0; i &lt; n; i++) {
	 * 		monitor.subTask(&quot;doing something on &quot; + i);
	 * 		doSomething();
	 * 		monitor.worked(1); // not i!
	 * 	}
	 * } finally {
	 * 	monitor.end();
	 * }
	 * </pre>
	 * 
	 * @param amount
	 */
	public void worked(float amount);
}
