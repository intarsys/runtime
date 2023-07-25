package de.intarsys.tools.notice;

import de.intarsys.tools.exception.UnreachableCodeError;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.MessageTools;

/**
 * A concrete {@link INotice} implementation.
 * 
 */
public class Notice implements INotice {

	private final int severity;

	private final IMessage message;

	private final boolean sticky;

	public Notice(IMessage message) {
		this(INotice.SEVERITY_INFO, false, message);
	}

	public Notice(IMessageBundle bundle, int severity, boolean sticky, String code, Object... args) {
		this(severity, sticky, bundle.getMessage(code, args));
	}

	public Notice(IMessageBundle bundle, String code, Object... args) {
		this(INotice.SEVERITY_INFO, false, bundle.getMessage(code, args));
	}

	public Notice(int severity, boolean sticky, IMessage message) {
		super();
		this.severity = severity;
		this.message = message;
		this.sticky = sticky;
	}

	public Notice(int severity, boolean sticky, String code, String text, Object... args) {
		this(severity, sticky, MessageTools.createMessage(code, text, args));
	}

	public Notice(String code, String text, Object... args) {
		this(INotice.SEVERITY_INFO, false, code, text, args);
	}

	@Override
	public Object getArgumentAt(int index) {
		return message.getArgumentAt(index);
	}

	@Override
	public int getArgumentSize() {
		return message.getArgumentSize();
	}

	@Override
	public String getCode() {
		return message.getCode();
	}

	/**
	 * Legacy (scripting) code may access this method.
	 * 
	 * @return
	 */
	public Object getDetail() {
		return getArgumentSize() > 0 ? getArgumentAt(0) : null;
	}

	@Override
	public String getPattern() {
		return message.getPattern();
	}

	@Override
	public int getSeverity() {
		return severity;
	}

	@Override
	public String getString() {
		return message.getString();
	}

	/**
	 * Legacy (scripting) code may access this method.
	 * 
	 * @return
	 */
	public String getText() {
		return message.getString();
	}

	public boolean isDebug() {
		return getSeverity() == INotice.SEVERITY_DEBUG;
	}

	/**
	 * Legacy (scripting) code may access this method.
	 * 
	 * @return
	 */
	public boolean isError() {
		return getSeverity() == INotice.SEVERITY_ERROR;
	}

	/**
	 * Legacy (scripting) code may access this method.
	 * 
	 * @return
	 */
	public boolean isInfo() {
		return getSeverity() == INotice.SEVERITY_INFO;
	}

	@Override
	public boolean isSticky() {
		return sticky;
	}

	/**
	 * Legacy (scripting) code may access this method.
	 * 
	 * @return
	 */
	public boolean isWarning() {
		return getSeverity() == INotice.SEVERITY_WARNING;
	}

	@Override
	public String toString() {
		String severityPrefix;
		switch (severity) {
		case INotice.SEVERITY_DEBUG:
			severityPrefix = "DEBUG ";
			break;
		case INotice.SEVERITY_INFO:
			severityPrefix = "INFO ";
			break;
		case INotice.SEVERITY_WARNING:
			severityPrefix = "WARNING ";
			break;
		case INotice.SEVERITY_ERROR:
			severityPrefix = "ERROR ";
			break;
		default:
			throw new UnreachableCodeError();
		}

		return severityPrefix + message.toString();
	}
}
