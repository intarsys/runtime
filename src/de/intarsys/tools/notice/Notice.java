package de.intarsys.tools.notice;

/**
 * A message abstraction.
 * 
 */
public class Notice {

	public static final int SEVERITY_DEBUG = 0;

	public static final int SEVERITY_INFO = 10;

	public static final int SEVERITY_WARNING = 20;

	public static final int SEVERITY_ERROR = 30;

	final private int severity;

	final private String text;

	final private Object detail;

	final private boolean sticky;

	public Notice(int severity, String text, Object detail, boolean sticky) {
		super();
		this.severity = severity;
		this.text = text;
		this.detail = detail;
		this.sticky = sticky;
	}

	/**
	 * The optional detail information for the {@link Notice}. This is for
	 * example an exception when creating an error message.
	 * 
	 * @return The optional detail information.
	 */
	public Object getDetail() {
		return detail;
	}

	/**
	 * The severity of the {@link Notice}. This is one of the SEVERITY_*
	 * constants or a user defined value.
	 * 
	 * @return The severity of the {@link Notice}
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * The textual message.
	 * 
	 * @return The textual message.
	 */
	public String getText() {
		return text;
	}

	public boolean isDebug() {
		return getSeverity() == Notice.SEVERITY_DEBUG;
	}

	public boolean isError() {
		return getSeverity() == Notice.SEVERITY_ERROR;
	}

	public boolean isInfo() {
		return getSeverity() == Notice.SEVERITY_INFO;
	}

	public boolean isSticky() {
		return sticky;
	}

	public boolean isWarning() {
		return getSeverity() == Notice.SEVERITY_WARNING;
	}

}
