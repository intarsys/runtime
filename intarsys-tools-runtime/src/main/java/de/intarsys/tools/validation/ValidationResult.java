package de.intarsys.tools.validation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.notice.INotice;
import de.intarsys.tools.notice.INoticesSupport;
import de.intarsys.tools.notice.Notice;

/**
 * A simple concrete {@link IValidationResult} implementation.
 * 
 */
public class ValidationResult implements IValidationResult {

	private final Object target;

	private final long timestamp;

	private boolean hasInfo;

	private boolean hasError;

	private boolean hasWarning;

	private final List<INotice> notices = new CopyOnWriteArrayList<>();

	public ValidationResult(Object target) {
		super();
		this.target = target;
		this.timestamp = System.currentTimeMillis();
	}

	public void addError(String code, String text) {
		Notice note = new Notice(INotice.SEVERITY_ERROR, true, code, text, getTarget());
		addNotice(note);
	}

	public void addInfo(String code, String text) {
		Notice note = new Notice(INotice.SEVERITY_INFO, true, code, text, getTarget());
		addNotice(note);
	}

	@Override
	public void addNotice(INotice notification) {
		notices.add(notification);
		hasInfo |= notification.getSeverity() == INotice.SEVERITY_INFO;
		hasWarning |= notification.getSeverity() == INotice.SEVERITY_WARNING;
		hasError |= notification.getSeverity() == INotice.SEVERITY_ERROR;
	}

	public void addNotice(int severity, boolean sticky, IMessage message) {
		Notice note = new Notice(severity, sticky, message);
		addNotice(note);
	}

	public void addNotice(int severity, String code, String text, boolean sticky) {
		Notice note = new Notice(severity, sticky, code, text, getTarget());
		addNotice(note);
	}

	public void addNotices(INoticesSupport ns) {
		if (ns == null) {
			return;
		}
		for (INotice notice : ns.getNotices()) {
			addNotice(notice);
		}
	}

	public void addWarning(IMessage message) {
		Notice note = new Notice(INotice.SEVERITY_WARNING, true, message);
		addNotice(note);
	}

	public void addWarning(String code, String text) {
		Notice note = new Notice(INotice.SEVERITY_WARNING, true, code, text, getTarget());
		addNotice(note);
	}

	@Override
	public void clearNotices() {
		notices.clear();
	}

	@Override
	public List<INotice> getNotices() {
		return notices;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public boolean hasError() {
		return hasError;
	}

	@Override
	public boolean hasInfo() {
		return hasInfo;
	}

	@Override
	public boolean hasWarning() {
		return hasWarning;
	}

	@Override
	public boolean removeNotice(INotice notice) {
		return notices.remove(notice);
	}
}
