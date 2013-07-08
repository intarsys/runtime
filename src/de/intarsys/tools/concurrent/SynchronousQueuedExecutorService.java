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
package de.intarsys.tools.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An {@link ExecutorService} that enqueues all commmands.
 * <p>
 * The commands are performed synchronously upon calling "drain".
 */
public class SynchronousQueuedExecutorService extends AbstractExecutorService {

	volatile private boolean shutdown = false;

	volatile private boolean terminated = false;

	private List<Runnable> queue = new ArrayList<Runnable>();

	private Object lock = new Object();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#awaitTermination(long,
	 * java.util.concurrent.TimeUnit)
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return true;
	}

	public void drain() {
		List<Runnable> tempQueue;
		synchronized (lock) {
			tempQueue = new ArrayList<Runnable>(queue);
			queue.clear();
		}
		for (Iterator it = tempQueue.iterator(); it.hasNext();) {
			Runnable command = (Runnable) it.next();
			command.run();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
	 */
	public void execute(Runnable command) {
		synchronized (lock) {
			if (shutdown || terminated) {
				return;
			}
			queue.add(command);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#isShutdown()
	 */
	public boolean isShutdown() {
		synchronized (lock) {
			return shutdown;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#isTerminated()
	 */
	public boolean isTerminated() {
		synchronized (lock) {
			return terminated;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 */
	public void shutdown() {
		synchronized (lock) {
			shutdown = true;
			terminated = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 */
	public List shutdownNow() {
		synchronized (lock) {
			shutdown = true;
			terminated = true;
			return new ArrayList<Runnable>(queue);
		}
	}
}
