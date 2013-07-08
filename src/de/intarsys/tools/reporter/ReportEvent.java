package de.intarsys.tools.reporter;

public class ReportEvent {

	public static final int TYPE_ACTIVITY_END = 2;

	public static final int TYPE_ACTIVITY_START = 1;

	public static final int TYPE_ERROR = 5;

	public static final int TYPE_MESSAGE = 4;

	public static final int TYPE_PROGRESS = 0;

	public static final int TYPE_STATUS = 3;

	final private int style;

	final private Throwable throwable;

	final private String title;

	final private int type;

	final private Thread thread;

	private String message;

	private int percent;

	private int index;

	private Object data;

	public ReportEvent(int type) {
		this.type = type;
		this.index = 0;
		this.message = null;
		this.thread = Thread.currentThread();
		this.title = null;
		this.throwable = null;
		this.style = 0;
	}

	public ReportEvent(int type, String message, int style) {
		this.type = type;
		this.index = 0;
		this.message = message;
		this.thread = Thread.currentThread();
		this.title = null;
		this.throwable = null;
		this.style = style;
	}

	public ReportEvent(int type, String message, int percent, int style) {
		this.type = type;
		this.index = 0;
		this.message = message;
		this.percent = percent;
		if (percent > 0) {
			index++;
		}
		this.thread = Thread.currentThread();
		this.title = null;
		this.throwable = null;
		this.style = style;
	}

	public ReportEvent(int type, String title, String message, int style) {
		this.type = type;
		this.index = 0;
		this.message = message;
		this.thread = Thread.currentThread();
		this.title = title;
		this.throwable = null;
		this.style = style;
	}

	public ReportEvent(int type, String title, String message, Throwable t,
			int style) {
		this.type = type;
		this.index = 0;
		this.message = message;
		this.thread = Thread.currentThread();
		this.title = title;
		this.throwable = t;
		this.style = style;
	}

	public Object getData() {
		return data;
	}

	public int getIndex() {
		return index;
	}

	public String getMessage() {
		return message;
	}

	public int getPercent() {
		return percent;
	}

	public int getStyle() {
		return style;
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

	public boolean isTypeProgress() {
		return type == TYPE_PROGRESS;
	}

	public boolean isTypeStatus() {
		return type == TYPE_STATUS;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPercent(int pPercent) {
		if (pPercent == 0) {
			index = 0;
		} else if (pPercent > percent) {
			index++;
		} else if (pPercent < percent) {
			index--;
		}
		this.percent = pPercent;
	}
}