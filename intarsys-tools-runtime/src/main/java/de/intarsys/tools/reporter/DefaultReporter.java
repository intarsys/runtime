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

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.activity.ReportStatus;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.progress.IProgressMonitor;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * The default reporting implementation for the Stage kernel. This one is just
 * logging to java standard logging.
 * 
 */
public class DefaultReporter implements IReporter {

	private final ILogger logger;

	public DefaultReporter() {
		this(LogTools.getLogger(DefaultReporter.class));
	}

	public DefaultReporter(ILogger logger) {
		super();
		this.logger = logger;
	}

	protected ILogger getLogger() {
		return logger;
	}

	@Override
	public IProgressMonitor reportActivityStart(IMessage msg, int style) {
		ReportStatus<IActivity<?>> activity = new ReportStatus<IActivity<?>>(null) {

			@Override
			public void begin(String name, float totalWork) {
				getLogger().trace("{} begin", getFullName()); //$NON-NLS-1$
				super.begin(name, totalWork);
			}

			@Override
			protected void onFinally() {
				getLogger().trace("{} end", getFullName()); //$NON-NLS-1$
				super.onFinally();
			}

			@Override
			public void subTask(String name) {
				getLogger().trace("{} subtask {}", getFullName(), name); //$NON-NLS-1$
				super.subTask(name);
			}

			@Override
			public void worked(float amount) {
				getLogger().trace("{} worked {}%", getFullName(), getWorkedPercent()); //$NON-NLS-1$
				super.worked(amount);
			}
		};
		activity.setMessage(msg);
		activity.enter();
		if (getLogger().isLoggable(Level.TRACE)) {
			getLogger().log(Level.TRACE, "start activity " + msg); //$NON-NLS-1$
		}
		return activity;
	}

	@Override
	public void reportError(String title, String message, Throwable t, int style) {
		getLogger().log(Level.SEVERE, message, t);
	}

	@Override
	public void reportMessage(String title, String message, int style) {
		getLogger().log(Level.INFO, message);
	}

	@Override
	public void reportStatus(String text, int style) {
		getLogger().log(Level.INFO, text);
	}
}
