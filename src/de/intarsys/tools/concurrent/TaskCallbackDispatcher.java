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

import java.util.concurrent.ExecutionException;

/**
 * Helper object for implementing {@link ITaskListenerSupport}.
 */
public class TaskCallbackDispatcher implements ITaskListenerSupport,
		ITaskListener {

	private final Object owner;

	private ITaskListener[] listeners = new ITaskListener[2];

	public TaskCallbackDispatcher(Object pOwner) {
		super();
		owner = pOwner;
	}

	public void addTaskListener(ITaskListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener may not be null");
		}
		int length = listeners.length;
		int i = 0;
		while (i < length) {
			if (listeners[i] == null) {
				break;
			}
			i++;
		}
		if (i >= length) {
			ITaskListener[] templisteners = new ITaskListener[length + 4];
			System.arraycopy(listeners, 0, templisteners, 0, length);
			listeners = templisteners;
		}
		listeners[i] = listener;
	}

	synchronized public void attach(ITaskListenerSupport support) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			support.addTaskListener(listeners[i]);
		}
	}

	public synchronized void clear() {
		listeners = new ITaskListener[4];
	}

	synchronized public void detach(ITaskListenerSupport support) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			support.removeTaskListener(listeners[i]);
		}
	}

	public Object getOwner() {
		return owner;
	}

	public synchronized boolean isEmpty() {
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null) {
				return false;
			}
		}
		return true;
	}

	public void removeTaskListener(ITaskListener listener) {
		int length = listeners.length;
		int i = 0;
		while (i < length) {
			if (listeners[i] == listener) {
				listeners[i] = null;
				break;
			}
			i++;
		}
	}

	public void taskCancelled(Object task) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			ITaskListener templistener = listeners[i];
			if (templistener == null) {
				continue;
			}
			templistener.taskCancelled(task);
		}
	}

	public void taskFailed(Object task, ExecutionException exception) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			ITaskListener templistener = listeners[i];
			if (templistener == null) {
				continue;
			}
			templistener.taskFailed(task, exception);
		}
	}

	public void taskFinished(Object task, Object result) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			ITaskListener templistener = listeners[i];
			if (templistener == null) {
				continue;
			}
			templistener.taskFinished(task, result);
		}
	}

	public void taskStarted(Object task) {
		int length = listeners.length;
		for (int i = 0; i < length; i++) {
			ITaskListener templistener = listeners[i];
			if (templistener == null) {
				continue;
			}
			templistener.taskStarted(task);
		}
	}

}
