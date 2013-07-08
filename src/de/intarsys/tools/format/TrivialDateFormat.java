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
package de.intarsys.tools.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

/**
 * A thread safe, quite trivial formatter for date/calendar objects.
 */
public class TrivialDateFormat extends Format {
	/**
	 * The default instance
	 * 
	 * @return The default instance
	 */
	public static TrivialDateFormat getInstance() {
		return new TrivialDateFormat();
	}

	/**
	 * Thread specific calendar instances.
	 * 
	 */
	private ThreadLocal calendar = new ThreadLocal() {
		@Override
		protected Object initialValue() {
			return Calendar.getInstance();
		}
	};

	/**
	 * Create a TrivialDateFormat
	 */
	public TrivialDateFormat() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer,
	 *      java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		Calendar theCalendar;
		if (obj instanceof Date) {
			theCalendar = (Calendar) calendar.get();
			theCalendar.setTime((Date) obj);
		} else if (obj instanceof Calendar) {
			theCalendar = (Calendar) obj;
		} else if (obj instanceof Long) {
			theCalendar = (Calendar) calendar.get();
			theCalendar.setTime(new Date(((Long) obj).longValue()));
		} else if (obj instanceof Integer) {
			theCalendar = (Calendar) calendar.get();
			theCalendar.setTime(new Date(((Integer) obj).longValue()));
		} else {
			return toAppendTo.append("<formatting error>");
		}
		int value;
		value = theCalendar.get(Calendar.DAY_OF_MONTH);
		if (value < 10) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		toAppendTo.append(".");
		value = theCalendar.get(Calendar.MONTH) + 1;
		if (value < 10) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		toAppendTo.append(".");
		value = theCalendar.get(Calendar.YEAR);
		toAppendTo.append(value);
		toAppendTo.append("-");
		value = theCalendar.get(Calendar.HOUR_OF_DAY);
		if (value < 10) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		toAppendTo.append(":");
		value = theCalendar.get(Calendar.MINUTE);
		if (value < 10) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		toAppendTo.append(":");
		value = theCalendar.get(Calendar.SECOND);
		if (value < 10) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		toAppendTo.append(":");
		value = theCalendar.get(Calendar.MILLISECOND);
		if (value < 10) {
			toAppendTo.append("0");
		}
		if (value < 100) {
			toAppendTo.append("0");
		}
		toAppendTo.append(value);
		return toAppendTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.Format#parseObject(java.lang.String,
	 *      java.text.ParsePosition)
	 */
	@Override
	public Object parseObject(String source, ParsePosition status) {
		return null;
	}
}
