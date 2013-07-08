/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.logging;

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

/**
 * A simple usable formatter for java logging
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
		SDC.get();
	}

	/** A thread safe date format. */
	private Format dateFormat = new TrivialDateFormat();

	/** The StringBuffer to format the message (this is not thread safe). */
	private final StringBuffer sb = new StringBuffer();

	private int widthSDC = 20;

	private boolean showDate = true;

	private boolean showLevel = true;

	private boolean showName = true;

	private boolean showSDC = false;

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
	public synchronized String format(LogRecord record) {
		sb.setLength(0);
		if (isShowDate()) {
			sb.append("[");
			int index = sb.length();
			dateFormat.format(new Date(record.getMillis()), sb, null);
			for (int i = sb.length() - index; i < 24; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowLevel()) {
			sb.append("[");
			String levelString = record.getLevel().toString();
			sb.append(levelString);
			for (int i = levelString.length(); i < widthLevel; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		if (isShowName()) {
			String loggerString = record.getLoggerName();
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
				threadString = StringTools.getTrailing(threadString,
						widthThread);
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
			String sdcString = String.valueOf(SDC.get());
			sb.append(sdcString);
			for (int i = sdcString.length(); i < widthSDC; i++) {
				sb.append(' ');
			}
			sb.append("]");
		}
		sb.append(" ");
		sb.append(record.getMessage());
		sb.append(LF);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
				sb.append(LF);
			} catch (Exception ex) {
				// ignore
			}
		}
		return sb.toString();
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
