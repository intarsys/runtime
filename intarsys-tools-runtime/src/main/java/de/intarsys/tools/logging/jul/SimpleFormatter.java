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
package de.intarsys.tools.logging.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import de.intarsys.tools.format.TrivialDateFormat;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.Yalf;

/**
 * A simple formatter for java.util.logging.
 * 
 * Configuration can be done using a quite simple, position agnostic pattern
 * string:
 * 
 * <pre>
 * show date: %d
 * show level: %p
 * show name: %c
 * show context: %X
 * show thread: %t
 * </pre>
 */
public class SimpleFormatter extends Formatter {

	/** The line separator. */
	private static final String LF = StringTools.LS;

	static {
		// force classloading - strange problems may arise when logging within
		// classloader
		new TrivialDateFormat();
		StringTools.isEmpty("");
		Calendar.getInstance();
		Yalf.get();
	}

	/**
	 * Try to derive formatter from a logback style pattern string
	 * 
	 * @param pattern
	 * @return
	 */
	public static SimpleFormatter parse(String pattern) {
		if (StringTools.isEmpty(pattern)) {
			return null;
		}
		SimpleFormatter formatter = new SimpleFormatter();
		formatter.setShowDate(false);
		formatter.setShowLevel(false);
		formatter.setShowName(false);
		formatter.setShowSDC(false);
		formatter.setShowThread(false);
		char c;
		int index = 0;
		int len = pattern.length();
		boolean percent = false;
		while (index < len) {
			c = pattern.charAt(index++);
			if (percent) {
				if (c == '-' || c == '.' || c == '+' || Character.isDigit(c)) {
					// ignore formatting
				} else if (c == 'd') {
					formatter.setShowDate(true);
				} else if (c == 'p') {
					formatter.setShowLevel(true);
				} else if (c == 'c') {
					formatter.setShowName(true);
				} else if (c == 'X') {
					formatter.setShowSDC(true);
				} else if (c == 't') {
					formatter.setShowThread(true);
				} else {
					// ignore anything else
				}
			} else {
				if (c == '%') {
					percent = true;
				} else {
					// ignore anything else
				}
			}
		}
		return formatter;
	}

	/** A thread safe date format. */
	private Format dateFormat = new TrivialDateFormat();

	private int widthSDC = 20;

	private boolean showDate = true;

	private boolean showLevel = true;

	private boolean showName = true;

	private boolean showSDC;

	private boolean showThread = true;

	private int widthLevel = 7;

	private int widthName = 30;

	private int widthThread = 15;

	/**
	 * 
	 */
	public SimpleFormatter() {
		super();
		configure();
	}

	public String asPattern() {
		return null;
	}

	protected void configure() {
		String cname = getClass().getName();
		setShowSDC(getBooleanProperty(cname + ".sdc.show", isShowSDC()));
		setWidthSDC(getIntProperty(cname + ".sdc.width", getWidthSDC()));
		setShowThread(getBooleanProperty(cname + ".thread.show", isShowThread()));
		setWidthThread(getIntProperty(cname + ".thread.width", getWidthThread()));
		setShowDate(getBooleanProperty(cname + ".date.show", isShowDate()));
		setShowLevel(getBooleanProperty(cname + ".level.show", isShowLevel()));
		setWidthLevel(getIntProperty(cname + ".level.width", getWidthLevel()));
		setShowName(getBooleanProperty(cname + ".name.show", isShowName()));
		setWidthName(getIntProperty(cname + ".name.width", getWidthName()));
	}

	@Override
	public String format(LogRecord event) {
		return format(event, new StringBuffer()).toString();
	}

	public StringBuffer format(Object obj, StringBuffer sb) {
		LogRecord event = (LogRecord) obj;
		if (isShowDate()) {
			sb.append("[");
			int index = sb.length();
			dateFormat.format(new Date(event.getMillis()), sb, null);
			for (int i = sb.length() - index; i < 24; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowLevel()) {
			sb.append("[");
			String levelString = event.getLevel().toString();
			sb.append(levelString);
			for (int i = levelString.length(); i < widthLevel; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowName()) {
			String loggerString = event.getLoggerName();
			if (loggerString == null) {
				loggerString = "<unknown>";
			}
			if (loggerString.length() > widthName) {
				loggerString = StringTools.getTrailing(loggerString, widthName);
			}
			sb.append("[");
			sb.append(loggerString);
			for (int i = loggerString.length(); i < widthName; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowThread()) {
			String threadString = Thread.currentThread().getName();
			if (threadString.length() > widthThread) {
				threadString = StringTools.getTrailing(threadString, widthThread);
			}
			sb.append("[");
			sb.append(threadString);
			for (int i = threadString.length(); i < widthThread; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowSDC()) {
			sb.append("[");
			String sdcString = String.valueOf(Yalf.get().getMDC().get("sdc"));
			sb.append(sdcString);
			for (int i = sdcString.length(); i < widthSDC; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		sb.append(" ");
		sb.append(formatMessage(event));
		sb.append(LF);
		if (event.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				event.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
				sb.append(LF);
			} catch (Exception ex) {
				// ignore
			}
		}
		return sb;
	}

	protected boolean getBooleanProperty(String name, boolean defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String property = manager.getProperty(name);
		if (StringTools.isEmpty(property)) {
			return defaultValue;
		}
		return Boolean.valueOf(property.trim());
	}

	protected int getIntProperty(String name, int defaultValue) {
		LogManager manager = LogManager.getLogManager();
		String property = manager.getProperty(name);
		if (StringTools.isEmpty(property)) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(property.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int getWidthLevel() {
		return widthLevel;
	}

	public int getWidthName() {
		return widthName;
	}

	public int getWidthSDC() {
		return widthSDC;
	}

	public int getWidthThread() {
		return widthThread;
	}

	public boolean isShowDate() {
		return showDate;
	}

	public boolean isShowLevel() {
		return showLevel;
	}

	public boolean isShowName() {
		return showName;
	}

	public boolean isShowSDC() {
		return showSDC;
	}

	public boolean isShowThread() {
		return showThread;
	}

	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}

	public void setShowLevel(boolean showLevel) {
		this.showLevel = showLevel;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}

	public void setShowSDC(boolean showSDC) {
		this.showSDC = showSDC;
	}

	public void setShowThread(boolean showThread) {
		this.showThread = showThread;
	}

	public void setWidthLevel(int widthLevel) {
		this.widthLevel = widthLevel;
	}

	public void setWidthName(int widthLoggername) {
		this.widthName = widthLoggername;
	}

	public void setWidthSDC(int widthSDC) {
		this.widthSDC = widthSDC;
	}

	public void setWidthThread(int widthThread) {
		this.widthThread = widthThread;
	}
}
