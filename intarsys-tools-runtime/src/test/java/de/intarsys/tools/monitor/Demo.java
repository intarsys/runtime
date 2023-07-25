/*
 * Copyright (c) 2008, intarsys GmbH
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
package de.intarsys.tools.monitor;

import de.intarsys.tools.yalf.api.Level;

/**
 * A simple code example for the monitor framework.
 */
@SuppressWarnings("java:S2925")
public class Demo {

	public static void main(String[] args) {
		try {
			new Demo().testSimple();
			new Demo().testSample();
			new Demo().testTag();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	public Demo() {
		super();
	}

	public void testSample() throws InterruptedException {
		IMonitor monitor = new TimeMonitor("example"); //$NON-NLS-1$

		// create a trace with additional samples
		monitor.attach();
		Thread.sleep(10);
		Trace.get().sample(Level.INFO, "in between"); //$NON-NLS-1$
		Thread.sleep(10);
		monitor.detach();
	}

	public void testSimple() throws InterruptedException {
		IMonitor monitor = new TimeMonitor("example"); //$NON-NLS-1$

		// create a simple trace
		monitor.attach();
		Thread.sleep(10);
		monitor.detach();
	}

	public void testTag() throws InterruptedException {
		IMonitor monitor = new TimeMonitor("example"); //$NON-NLS-1$

		// create a trace with additional samples
		monitor.attach();
		Trace.get().tag("user", "dummy"); //$NON-NLS-1$ //$NON-NLS-2$
		Thread.sleep(10);
		Trace.get().sample(Level.INFO, "in between"); //$NON-NLS-1$
		Thread.sleep(10);
		monitor.detach();
	}
}
