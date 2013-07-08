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
package de.intarsys.tools.reporter;

/**
 * An {@link IReporter} that may be used to intercept the reporting information
 * and "replay" them later on on another {@link IReporter} instance.
 * <p>
 * This is useful if an object receiving reporting information may be
 * dynamically multiplexed on many reporting events emitting instances, for
 * example a window showing a currently active object.
 */
public class ReplayReporter implements IReporter, IReporterSupport {

	private final Object owner;

	final private ReportDispatcher dispatcher;

	final private ReportEvent[] messages = new ReportEvent[10];

	private int first = 0;

	public ReplayReporter(Object owner) {
		super();
		this.owner = owner;
		this.dispatcher = new ReportDispatcher(owner);
	}

	protected void addMessage(ReportEvent message) {
		messages[first] = message;
		first = (first + 1) % messages.length;
	}

	public void addReporter(IReporter reporter) {
		dispatcher.addReporter(reporter);
		replay(reporter);
	}

	public Object getOwner() {
		return owner;
	}

	public void removeReporter(IReporter reporter) {
		dispatcher.removeReporter(reporter);
	}

	protected void replay(IReporter reporter) {
		for (int i = 0; i < messages.length; i++) {
			int index = (i + first) % messages.length;
			ReportEvent message = messages[index];
			if (message == null) {
				continue;
			}
			if (message.isTypeStatus()) {
				reporter.reportStatus(message.getMessage(), message.getStyle());
			} else if (message.isTypeMessage()) {
				reporter.reportMessage(message.getTitle(),
						message.getMessage(), message.getStyle());
			} else if (message.isTypeError()) {
				reporter.reportError(message.getTitle(), message.getMessage(),
						message.getThrowable(), message.getStyle());
			} else {
				// ignore
			}
		}
	}

	public void reportActivityEnd() {
		addMessage(new ReportEvent(ReportEvent.TYPE_ACTIVITY_END));
		dispatcher.reportActivityEnd();
	}

	public void reportActivityStart(String activity, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_ACTIVITY_START, activity,
				style));
		dispatcher.reportActivityStart(activity, style);
	}

	public void reportError(String title, String message, Throwable t, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_ERROR, title, message, t,
				style));
		dispatcher.reportError(title, message, t, style);
	}

	public void reportMessage(String title, String message, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_MESSAGE, title, message,
				style));
		dispatcher.reportMessage(title, message, style);
	}

	public void reportProgress(String message, int percent, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_PROGRESS, message, percent,
				style));
		dispatcher.reportProgress(message, percent, style);
	}

	public void reportStatus(String message, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_STATUS, message, style));
		dispatcher.reportStatus(message, style);
	}

}
