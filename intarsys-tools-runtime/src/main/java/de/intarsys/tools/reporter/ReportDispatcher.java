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
package de.intarsys.tools.reporter;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.ReportStatus;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.progress.IProgressMonitor;

/**
 * Helper object for implementing {@link IReporterSupport}.
 */
public class ReportDispatcher implements IReporterSupport, IReporter {

	private final Object owner;

	private IReporter[] reporters = new IReporter[2];

	public ReportDispatcher(Object pOwner) {
		super();
		owner = pOwner;
	}

	@Override
	public synchronized void addReporter(IReporter newReporter) {
		if (newReporter == null) {
			throw new NullPointerException("reporter may not be null");
		}
		int length = reporters.length;
		int i = 0;
		while (i < length) {
			if (reporters[i] == null) {
				break;
			}
			i++;
		}
		if (i >= length) {
			IReporter[] tempReporters = new IReporter[length + 4];
			System.arraycopy(reporters, 0, tempReporters, 0, length);
			reporters = tempReporters;
		}
		reporters[i] = newReporter;
	}

	public synchronized void attach(IReporterSupport support) {
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			support.addReporter(reporters[i]);
		}
	}

	public synchronized void clear() {
		reporters = new IReporter[4];
	}

	public synchronized void detach(IReporterSupport support) {
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			support.removeReporter(reporters[i]);
		}
	}

	public Object getOwner() {
		return owner;
	}

	public synchronized boolean isEmpty() {
		for (int i = 0; i < reporters.length; i++) {
			if (reporters[i] != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized void removeReporter(IReporter newReporter) {
		int length = reporters.length;
		int i = 0;
		while (i < length) {
			if (reporters[i] == newReporter) {
				reporters[i] = null;
				break;
			}
			i++;
		}
	}

	@Override
	public IProgressMonitor reportActivityStart(IMessage message, int style) {
		final List<IProgressMonitor> nestedMonitors = new ArrayList<>();
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			IReporter tempReporter = reporters[i];
			if (tempReporter == null) {
				continue;
			}
			nestedMonitors.add(tempReporter.reportActivityStart(message, style));
		}
		ReportStatus<IActivity<?>> activity = new ReportStatus<IActivity<?>>(null) {

			@Override
			public void begin(String name, float totalWork) {
				for (IProgressMonitor nestedMonitor : nestedMonitors) {
					nestedMonitor.begin(name, totalWork);
				}
				super.begin(name, totalWork);
			}

			@Override
			public void end() {
				for (IProgressMonitor nestedMonitor : nestedMonitors) {
					nestedMonitor.end();
				}
				super.end();
			}

			@Override
			public void subTask(String name) {
				for (IProgressMonitor nestedMonitor : nestedMonitors) {
					nestedMonitor.subTask(name);
				}
				super.subTask(name);
			}

			@Override
			public void worked(float amount) {
				for (IProgressMonitor nestedMonitor : nestedMonitors) {
					nestedMonitor.worked(amount);
				}
				super.worked(amount);
			}
		};
		activity.setMessage(message);
		activity.enter();
		return activity;
	}

	@Override
	public void reportError(String title, String message, Throwable t, int style) {
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			IReporter tempReporter = reporters[i];
			if (tempReporter == null) {
				continue;
			}
			tempReporter.reportError(title, message, t, style);
		}
	}

	@Override
	public void reportMessage(String title, String message, int style) {
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			IReporter tempReporter = reporters[i];
			if (tempReporter == null) {
				continue;
			}
			tempReporter.reportMessage(title, message, style);
		}
	}

	@Override
	public void reportStatus(String message, int style) {
		int length = reporters.length;
		for (int i = 0; i < length; i++) {
			IReporter tempReporter = reporters[i];
			if (tempReporter == null) {
				continue;
			}
			tempReporter.reportStatus(message, style);
		}
	}
}
