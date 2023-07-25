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

import java.util.Arrays;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.message.CommonMessages;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.notice.INotice;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * The representation of a confirmation activity. The user is requested to
 * select one out of many options to proceed.
 * 
 * @param <P>
 */
public class RequestConfirmation<P extends IActivity<?>> extends Requester<IMessage, P> {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	public static final IMessage OPTION_OK = Msg.getMessage("RequestConfirmation.ok"); //$NON-NLS-1$

	public static final IMessage OPTION_CANCEL = Msg.getMessage("RequestConfirmation.cancel"); //$NON-NLS-1$

	public static final IMessage OPTION_YES = Msg.getMessage("RequestConfirmation.yes"); //$NON-NLS-1$

	public static final IMessage OPTION_NO = Msg.getMessage("RequestConfirmation.no"); //$NON-NLS-1$

	public static final IMessage OPTION_PROCEED = Msg.getMessage("RequestConfirmation.proceed"); //$NON-NLS-1$

	protected static final IMessage[] OPTIONS_OK = new IMessage[] { OPTION_OK };

	protected static final IMessage[] OPTIONS_CANCEL = new IMessage[] { OPTION_CANCEL };

	protected static final IMessage[] OPTIONS_OK_CANCEL = new IMessage[] { OPTION_OK, OPTION_CANCEL };

	protected static final IMessage[] OPTIONS_YES_NO = new IMessage[] { OPTION_YES, OPTION_NO };

	protected static final IMessage[] OPTIONS_YES_NO_CANCEL = new IMessage[] { OPTION_YES, OPTION_NO, OPTION_CANCEL };

	private static final ILogger Log = PACKAGE.Log;

	/**
	 * Create and process a choice between available options.
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param severity
	 * @param options
	 * @param defaultOption
	 * @param toggleMessage
	 * @param toggleValue
	 * @return
	 */
	public static IMessage choose(IActivity<?> parent, IMessage title, IMessage message, int severity,
			IMessage[] options, IMessage defaultOption, IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		RequestConfirmation<?> confirm = create(parent, title, message, severity, options, defaultOption, toggleMessage,
				toggleValue);
		try {
			return ExceptionTools.futureSimpleGet(confirm.enter());
		} catch (Exception e) {
			return confirm.getDefaultOption();
		}
	}

	public static RequestConfirmation<?> create(IActivity<?> parent, IMessage title, IMessage message, int severity,
			IMessage[] options, IMessage defaultOption, IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		RequestConfirmation<?> confirm = new RequestConfirmation<>(parent, options);
		confirm.setTitle(title);
		confirm.setMessage(message);
		confirm.setSeverity(severity);
		if (defaultOption == null) {
			defaultOption = options[0];
		}
		if (!Arrays.asList(options).contains(defaultOption)) {
			throw new IllegalArgumentException("unsuitable default option");
		}
		confirm.setDefaultOption(defaultOption);
		confirm.setModal(true);
		confirm.setToggleMessage(toggleMessage);
		confirm.setToggleValue(toggleValue);
		return confirm;
	}

	public static IMessage[] getOptionsCancel() {
		return OPTIONS_CANCEL.clone();
	}

	public static IMessage[] getOptionsOk() {
		return OPTIONS_OK.clone();
	}

	public static IMessage[] getOptionsOkCancel() {
		return OPTIONS_OK_CANCEL.clone();
	}

	public static IMessage[] getOptionsYesNo() {
		return OPTIONS_YES_NO.clone();
	}

	public static IMessage[] getOptionsYesNoCancel() {
		return OPTIONS_YES_NO_CANCEL.clone();
	}

	/**
	 * Show non blocking message with the option "OK".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param severity
	 */
	public static void prompt(IActivity<?> parent, IMessage title, IMessage message, int severity) {
		RequestConfirmation confirm = new RequestConfirmation(parent, RequestConfirmation.OPTIONS_OK);
		confirm.setTitle(title);
		confirm.setMessage(message);
		confirm.setSeverity(severity);
		confirm.setDefaultOption(RequestConfirmation.OPTION_OK);
		confirm.setModal(true);
		confirm.setBlock(false);
		try {
			confirm.enter();
		} catch (Exception e) {
			//
		}
	}

	/**
	 * Show a blocking message with the option "OK".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param severity
	 * @return
	 */
	public static IMessage requestAcknowledge(IActivity<?> parent, IMessage title, IMessage message, int severity) {
		return choose(parent, title, message, severity, RequestConfirmation.OPTIONS_OK, RequestConfirmation.OPTION_OK,
				null, null);
	}

	/**
	 * Show a blocking message with the option "OK".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param severity
	 * @param toggleMessage
	 * @param toggleValue
	 * @return
	 */
	public static IMessage requestAcknowledge(IActivity<?> parent, IMessage title, IMessage message, int severity,
			IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		return choose(parent, title, message, severity, RequestConfirmation.OPTIONS_OK, RequestConfirmation.OPTION_OK,
				toggleMessage, toggleValue);
	}

	/**
	 * Show a blocking message with the option "OK".
	 * 
	 * @param parent
	 * @param code
	 * @return
	 */
	public static IMessage requestAcknowledge(IActivity<?> parent, String code) {
		IMessage message = ((IMessageBundleSupport) parent).getMessageBundle().getMessage(code);
		return requestAcknowledge(parent, CommonMessages.EMPTY, message, INotice.SEVERITY_INFO);
	}

	/**
	 * Show a blocking message with the options "OK" and "Cancel".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @return
	 */
	public static IMessage requestConfirm(IActivity<?> parent, IMessage title, IMessage message,
			IMessage defaultOption) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_OK_CANCEL,
				defaultOption, null, null);
	}

	/**
	 * Show a blocking message with the options "OK" and "Cancel".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @param toggleMessage
	 * @param toggleValue
	 * @return
	 */
	public static IMessage requestConfirm(IActivity<?> parent, IMessage title, IMessage message, IMessage defaultOption,
			IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_OK_CANCEL,
				defaultOption, toggleMessage, toggleValue);
	}

	/**
	 * Show a blocking message with the options "Yes" and "No".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @return
	 */
	public static IMessage requestYesNo(IActivity<?> parent, IMessage title, IMessage message, IMessage defaultOption) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_YES_NO, defaultOption,
				null, null);
	}

	/**
	 * Show a blocking message with the options "Yes" and "No".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @param toggleMessage
	 * @param toggleValue
	 * @return
	 */
	public static IMessage requestYesNo(IActivity<?> parent, IMessage title, IMessage message, IMessage defaultOption,
			IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_YES_NO, defaultOption,
				toggleMessage, toggleValue);
	}

	/**
	 * Show a blocking message with the options "Yes", "No" and "Cancel".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @return
	 */
	public static IMessage requestYesNoCancel(IActivity<?> parent, IMessage title, IMessage message,
			IMessage defaultOption) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_YES_NO_CANCEL,
				defaultOption, null, null);
	}

	/**
	 * Show a blocking message with the options "Yes", "No" and "Cancel".
	 * 
	 * @param parent
	 * @param title
	 * @param message
	 * @param defaultOption
	 * @param toggleMessage
	 * @param toggleValue
	 * @return
	 */
	public static IMessage requestYesNoCancel(IActivity<?> parent, IMessage title, IMessage message,
			IMessage defaultOption, IMessage toggleMessage, IValueHolder<Boolean> toggleValue) {
		return choose(parent, title, message, INotice.SEVERITY_INFO, RequestConfirmation.OPTIONS_YES_NO_CANCEL,
				defaultOption, toggleMessage, toggleValue);
	}

	private final IMessage[] options;

	private IMessage selection;

	private IMessage defaultOption;

	public RequestConfirmation(P parent, IMessage[] options) {
		super(parent);
		this.options = options;
		this.defaultOption = options[0];
	}

	public IMessage getDefaultOption() {
		return defaultOption;
	}

	public int getDefaultOptionIndex() {
		for (int i = 0; i < options.length; i++) {
			if (options[i] == defaultOption) {
				return i;
			}
		}
		return 0;
	}

	@Override
	protected IMessage getDefaultResult() {
		return getSelection();
	}

	public IMessage[] getOptions() {
		return options;
	}

	public IMessage getSelection() {
		return selection;
	}

	@Override
	protected void logFinish() {
		Log.info("{} selected '{}'", getLogLabel(), getSelection());
	}

	public void setDefaultOption(IMessage defaultSelection) {
		this.defaultOption = defaultSelection;
	}

	public void setSelection(IMessage selection) {
		this.selection = selection;
	}

	public void setSelectionIndex(int index) {
		if (options.length <= index || 0 > index) {
			setSelection(null);
		} else {
			setSelection(options[index]);
		}
	}
}
