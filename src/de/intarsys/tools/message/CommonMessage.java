package de.intarsys.tools.message;

public class CommonMessage {

	public final static MessageBundle Messages = MessageBundleTools
			.getMessageBundle(CommonMessage.class);

	public static final Message InternalError = new Message(Messages,
			"CommonMessage.InternalError"); //$NON-NLS-1$

	public static final Message InternalErrorTitle = new Message(Messages,
			"CommonMessage.InternalErrorTitle"); //$NON-NLS-1$

	public static final Message ErrorTitle = new Message(Messages,
			"CommonMessage.ErrorTitle"); //$NON-NLS-1$

}
