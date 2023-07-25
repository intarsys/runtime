/*
 * Copyright (c) 2014, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.activity;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.functor.ArgsBuilder;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.MessageTools;
import de.intarsys.tools.progress.BaseProgressMonitor;
import de.intarsys.tools.progress.IProgressMonitor;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * {@link ReportStatus} may be used to provide information on an ongoing
 * activity that does not require interaction.
 */
public class ReportStatus<P extends IActivity<?>> extends Reporter<Void, P> implements IProgressMonitor {

	private static final String ARG_SUBTASK = "subTask"; //$NON-NLS-1$
	private static final String ARG_WORK = "work"; //$NON-NLS-1$
	private static final String ARG_WORKED = "worked"; //$NON-NLS-1$

	private static final ILogger Log = PACKAGE.Log;

	private static final IMessageBundle Msg = PACKAGE.Messages;

	private int style;

	private final BaseProgressMonitor progressMonitor = new BaseProgressMonitor();

	public ReportStatus(P parent) {
		super(parent);
	}

	@Override
	public void begin(String name, float totalWork) {
		progressMonitor.begin(name, totalWork);
	}

	@Override
	public void end() {
		finish(null);
	}

	@Override
	protected IMessageBundle getDefaultMessageBundle() {
		return Msg;
	}

	public String getFullName() {
		return progressMonitor.getFullName();
	}

	@Override
	public IMessage getMessage() {
		IMessage message = super.getMessage();
		String subTaskNameArg = getSubTaskName();
		if (subTaskNameArg == null) {
			subTaskNameArg = "";
		}
		IArgs args = new ArgsBuilder() //
				.put(ARG_SUBTASK, subTaskNameArg) //
				.put(ARG_WORK, getWork()) //
				.put(ARG_WORKED, getWorked()) //
				.getArgs();
		try {
			String value = (String) TemplateEvaluator.get(Mode.UNTRUSTED).evaluate(message.getString(), args);
			// TODO do we need args?
			return MessageTools.createMessage(message.getCode(), value);
		} catch (EvaluationException e) {
			return message;
		}
	}

	public int getStyle() {
		return style;
	}

	public String getSubTaskName() {
		return progressMonitor.getSubTaskName();
	}

	public String getTaskName() {
		return progressMonitor.getTaskName();
	}

	public float getWork() {
		return progressMonitor.getWork();
	}

	public float getWorked() {
		return progressMonitor.getWorked();
	}

	public float getWorkedPercent() {
		return progressMonitor.getWorkedPercent();
	}

	@Override
	protected void logEnterAfter() {
		Log.debug("{} report '{}'", getLogLabel(), getMessage());
	}

	@Override
	protected void onFinally() {
		progressMonitor.end();
		super.onFinally();
	}

	public void setStyle(int style) {
		this.style = style;
	}

	@Override
	public void subTask(String name) {
		progressMonitor.subTask(name);
		triggerChanged(ARG_SUBTASK, null, name);
	}

	@Override
	public void worked(float amount) {
		progressMonitor.worked(amount);
		triggerChanged("worked", 0, amount);
	}
}
