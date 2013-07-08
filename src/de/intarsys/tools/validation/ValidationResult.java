package de.intarsys.tools.validation;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.notice.INoticesSupport;
import de.intarsys.tools.notice.Notice;

/**
 * The result state of a validation process performed by an {@link IValidator}.
 * 
 */
public class ValidationResult implements INoticesSupport {

	private final Object target;

	private final long timestamp;

	private boolean hasInfo = false;

	private boolean hasError = false;

	private boolean hasWarning = false;

	private final List<Notice> notices = new ArrayList<Notice>();

	public ValidationResult(Object target) {
		super();
		this.target = target;
		this.timestamp = System.currentTimeMillis();
	}

	public void addError(String text, Object detail) {
		Notice note = new Notice(Notice.SEVERITY_ERROR, text, detail, true);
		addNotice(note);
	}

	public void addInfo(String text, Object detail) {
		Notice note = new Notice(Notice.SEVERITY_INFO, text, detail, true);
		addNotice(note);
	}

	public void addNotice(int severity, String text, Object detail,
			boolean sticky) {
		Notice note = new Notice(severity, text, detail, sticky);
		addNotice(note);
	}

	public void addNotice(Notice notification) {
		notices.add(notification);
		hasInfo |= notification.isInfo();
		hasWarning |= notification.isWarning();
		hasError |= notification.isError();
	}

	public void addNotices(INoticesSupport ns) {
		for (Notice notice : ns.getNotices()) {
			addNotice(notice);
		}
	}

	public void addWarning(String text, Object detail) {
		Notice note = new Notice(Notice.SEVERITY_WARNING, text, detail, true);
		addNotice(note);
	}

	@Override
	public void clearNotices() {
		notices.clear();
	}

	@Override
	public List<Notice> getNotices() {
		return notices;
	}

	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		for (Notice note : notices) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(note.getText());
		}
		return sb.toString();
	}

	public Object getTarget() {
		return target;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean hasError() {
		return hasError;
	}

	public boolean hasInfo() {
		return hasInfo;
	}

	public boolean hasNotices() {
		return notices.size() > 0;
	}

	public boolean hasWarning() {
		return hasWarning;
	}

	@Override
	public boolean removeNotice(Notice notice) {
		return notices.remove(notice);
	}
}
