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
import de.intarsys.tools.message.MessageTools;
import de.intarsys.tools.progress.IProgressMonitor;

/**
 * An {@link IReporter} that may be used to intercept the reporting information
 * and "replay" them later on on another {@link IReporter} instance.
 * <p>
 * This is useful if an object receiving reporting information may be
 * dynamically multiplexed on many reporting event emitting instances, for
 * example a window showing a currently active object.
 * 
 */
public class ReplayReporter implements IReporter, IReporterSupport {

	class ReportEvent {

		public static final int TYPE_ACTIVITY_END = 2;

		public static final int TYPE_ACTIVITY_START = 1;

		public static final int TYPE_ACTIVITY_PROGRESS_BEGIN = 6;

		public static final int TYPE_ACTIVITY_PROGRESS_SUBTASK = 7;

		public static final int TYPE_ACTIVITY_PROGRESS_WORKED = 8;

		public static final int TYPE_ERROR = 5;

		public static final int TYPE_MESSAGE = 4;

		public static final int TYPE_STATUS = 3;

		private final int style;

		private final Throwable throwable;

		private final String title;

		private final int type;

		private final Thread thread;

		private final IMessage message;

		private final String text;

		private final float amount;

		public ReportEvent() {
			this.type = TYPE_ACTIVITY_END;
			this.message = null;
			this.thread = Thread.currentThread();
			this.title = null;
			this.text = null;
			this.amount = 0;
			this.throwable = null;
			this.style = 0;
		}

		public ReportEvent(float amount) {
			this.type = TYPE_ACTIVITY_PROGRESS_WORKED;
			this.message = null;
			this.thread = Thread.currentThread();
			this.title = null;
			this.throwable = null;
			this.text = null;
			this.amount = amount;
			this.style = 0;
		}

		public ReportEvent(IMessage message, int style) {
			this.type = TYPE_ACTIVITY_START;
			this.message = message;
			this.thread = Thread.currentThread();
			this.title = null;
			this.text = null;
			this.amount = 0;
			this.throwable = null;
			this.style = style;
		}

		public ReportEvent(int type, String title, String text, Throwable t, int style) {
			this.type = type;
			this.message = MessageTools.createMessage(null, text);
			this.thread = Thread.currentThread();
			this.title = title;
			this.text = text;
			this.amount = 0;
			this.throwable = t;
			this.style = style;
		}

		public ReportEvent(String name) {
			this.type = TYPE_ACTIVITY_PROGRESS_SUBTASK;
			this.message = null;
			this.thread = Thread.currentThread();
			this.title = null;
			this.throwable = null;
			this.text = name;
			this.amount = 0;
			this.style = 0;
		}

		public ReportEvent(String name, float total) {
			this.type = TYPE_ACTIVITY_PROGRESS_BEGIN;
			this.message = null;
			this.thread = Thread.currentThread();
			this.title = null;
			this.throwable = null;
			this.text = name;
			this.amount = total;
			this.style = 0;
		}

		public float getAmount() {
			return amount;
		}

		public IMessage getMessage() {
			return message;
		}

		public int getStyle() {
			return style;
		}

		protected String getText() {
			return text;
		}

		public Thread getThread() {
			return thread;
		}

		public Throwable getThrowable() {
			return throwable;
		}

		public String getTitle() {
			return title;
		}

		public int getType() {
			return type;
		}

		public boolean isTypeActivity() {
			return type == TYPE_ACTIVITY_START;
		}

		public boolean isTypeError() {
			return type == TYPE_ERROR;
		}

		public boolean isTypeMessage() {
			return type == TYPE_MESSAGE;
		}

		public boolean isTypeStatus() {
			return type == TYPE_STATUS;
		}
	}

	private final Object owner;

	private final ReportDispatcher dispatcher;

	private final ReportEvent[] messages = new ReportEvent[10];

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

	@Override
	public void addReporter(IReporter reporter) {
		dispatcher.addReporter(reporter);
		replay(reporter);
	}

	public Object getOwner() {
		return owner;
	}

	@Override
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
				reporter.reportStatus(message.getText(), message.getStyle());
			} else if (message.isTypeMessage()) {
				reporter.reportMessage(message.getTitle(), message.getText(), message.getStyle());
			} else if (message.isTypeError()) {
				reporter.reportError(message.getTitle(), message.getText(), message.getThrowable(), message.getStyle());
			} else {
				// ignore
			}
		}
	}

	@Override
	public IProgressMonitor reportActivityStart(IMessage message, int style) {
		final IProgressMonitor nestedMonitor = dispatcher.reportActivityStart(message, style);
		ReportStatus<IActivity<?>> activity = new ReportStatus<IActivity<?>>(null) {
			@Override
			public void begin(String name, float totalWork) {
				nestedMonitor.begin(name, totalWork);
				addMessage(new ReportEvent(name, totalWork));
				super.begin(name, totalWork);
			}

			@Override
			protected void onFinally() {
				nestedMonitor.end();
				addMessage(new ReportEvent());
				super.onFinally();
			}

			@Override
			public void subTask(String name) {
				nestedMonitor.subTask(name);
				addMessage(new ReportEvent(name));
				super.subTask(name);
			}

			@Override
			public void worked(float amount) {
				nestedMonitor.worked(amount);
				addMessage(new ReportEvent(amount));
				super.worked(amount);
			}
		};
		activity.setMessage(message);
		activity.enter();
		addMessage(new ReportEvent(message, style));
		return activity;
	}

	@Override
	public void reportError(String title, String text, Throwable t, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_ERROR, title, text, t, style));
		dispatcher.reportError(title, text, t, style);
	}

	@Override
	public void reportMessage(String title, String text, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_MESSAGE, title, text, null, style));
		dispatcher.reportMessage(title, text, style);
	}

	@Override
	public void reportStatus(String text, int style) {
		addMessage(new ReportEvent(ReportEvent.TYPE_STATUS, null, text, null, style));
		dispatcher.reportStatus(text, style);
	}
}
