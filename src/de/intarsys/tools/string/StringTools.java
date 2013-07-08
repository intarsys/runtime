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
package de.intarsys.tools.string;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.intarsys.tools.file.FileTools;

/**
 * A tool class for the handling of strings.
 * 
 */
public class StringTools {
	public static final String CR = "\r"; //$NON-NLS-1$

	public static final String CRLF = "\r\n"; //$NON-NLS-1$

	public static final String LF = "\n"; //$NON-NLS-1$

	public static final String LS = System.getProperty("line.separator"); //$NON-NLS-1$

	public static final String DATEFORMAT_SIMPLE = "yy-MM-dd HH:mm:ss:SSSS"; //$NON-NLS-1$

	private static DateFormat DEFAULT_DATEFORMAT;

	/** An empty string constant */
	public static final String EMPTY = ""; //$NON-NLS-1$

	public static final String FORMAT_D = "d"; //$NON-NLS-1$

	public static final String FORMAT_F = "f"; //$NON-NLS-1$

	public static final String FORMAT_I = "i"; //$NON-NLS-1$

	public static final String FORMAT_P = "p"; //$NON-NLS-1$

	public static final String FORMAT_S = "s"; //$NON-NLS-1$

	private static final String[] NO_ARGS = new String[0];

	/** An single space constant */
	public static final String SPACE = " "; //$NON-NLS-1$

	public static final Map<Character, String> ESCAPES = new HashMap<Character, String>();

	static {
		// escape the escape
		StringTools.ESCAPES.put(Character.valueOf('\\'), "\\"); //$NON-NLS-1$
		// escape the escape
		StringTools.ESCAPES.put(Character.valueOf('"'), "\""); //$NON-NLS-1$
		// escape to insert whitespace
		StringTools.ESCAPES.put(Character.valueOf('n'), "\n"); //$NON-NLS-1$
		StringTools.ESCAPES.put(Character.valueOf('r'), "\r"); //$NON-NLS-1$
		StringTools.ESCAPES.put(Character.valueOf('t'), "\t"); //$NON-NLS-1$
		// escape to remove whitespace
		StringTools.ESCAPES.put(Character.valueOf('\n'), null);
		StringTools.ESCAPES.put(Character.valueOf('\r'), null);
		StringTools.ESCAPES.put(Character.valueOf('\t'), null);
		StringTools.ESCAPES.put(Character.valueOf(' '), null);
	}

	public static String breakForced(String value, int max, String breakPattern) {
		StringBuilder sb = new StringBuilder();
		if (value != null) {
			int begin = 0;
			int end = max;
			int length = value.length();
			int currentlength = length;
			while (currentlength > max) {
				sb.append(value.substring(begin, end));
				currentlength -= max;
				begin += max;
				end += max;
				if (currentlength > 0) {
					sb.append(breakPattern);
				}
			}
			if (end > length) {
				end = length;
			}
			sb.append(value.substring(begin, end));
		}
		return sb.toString();
	}

	/**
	 * Capitalizes the first letter of the text.
	 * 
	 * @param text
	 * @return a capitalized version of the text
	 */
	public static String capitalize(String text) {
		if (isEmpty(text)) {
			return EMPTY;
		}
		String first = text.substring(0, 1);
		first = first.toUpperCase();
		if (text.length() > 1) {
			return first + text.substring(1);
		} else {
			return first;
		}
	}

	/**
	 * clear character array content.
	 * 
	 * @param value
	 *            The value to be cleared
	 */
	static public void clear(char[] value) {
		if (value == null) {
			return;
		}
		Arrays.fill(value, '0');
	}

	/**
	 * Create a string representation and format <code>value</code> according to
	 * the instructions in <code>format</code>.
	 * <p>
	 * If <code>value</code> is null, the empty string is returned.
	 * <p>
	 */
	public static String format(Object value, String format) {
		if (value == null) {
			return EMPTY;
		}
		if (format.startsWith(FORMAT_S)) {
			return formatString(value, format.substring(1));
		}
		if (format.startsWith(FORMAT_F)) {
			return formatFloat(value, format.substring(1));
		}
		if (format.startsWith(FORMAT_I)) {
			return formatInteger(value, format.substring(1));
		}
		if (format.startsWith(FORMAT_D)) {
			return formatDate(value, format.substring(1));
		}
		if (format.startsWith(FORMAT_P)) {
			return formatPath(value, format.substring(1));
		}

		// the default formattings
		if (value instanceof Date) {
			return new SimpleDateFormat(DATEFORMAT_SIMPLE).format(value);
		}
		if (value instanceof Long) {
			return Long.toString(((Long) value).longValue(), 32);
		}
		return value.toString();
	}

	/**
	 */
	protected static String formatDate(Object value, String format) {
		Date date = null;
		if (value instanceof Date) {
			date = (Date) value;
		} else if (value instanceof Number) {
			date = new Date(((Number) value).longValue());
		} else if (value instanceof String) {
			return (String) value;
		} else {
			return EMPTY;
		}
		if (StringTools.isEmpty(format)) {
			return formatDateDefault(date);
		}
		String pattern = parseArgsString(format);
		if (pattern == null) {
			char dateTime = '+';
			char command = format.charAt(0);
			if ((command == 'd') || (command == 't')) {
				// date or time only
				dateTime = command;
				if (format.length() > 1) {
					command = format.charAt(1);
				} else {
					command = 'f';
				}
			}
			if (dateTime == 'd') {
				if (command == 's') {
					DateFormat dateFormat = DateFormat
							.getDateInstance(DateFormat.SHORT);
					return dateFormat.format(date);
				} else if (command == 'm') {
					DateFormat dateFormat = DateFormat
							.getDateInstance(DateFormat.MEDIUM);
					return dateFormat.format(date);
				} else if (command == 'f') {
					DateFormat dateFormat = DateFormat
							.getDateInstance(DateFormat.FULL);
					return dateFormat.format(date);
				}
			} else if (dateTime == 't') {
				if (command == 's') {
					DateFormat dateFormat = DateFormat
							.getTimeInstance(DateFormat.SHORT);
					return dateFormat.format(date);
				} else if (command == 'm') {
					DateFormat dateFormat = DateFormat
							.getTimeInstance(DateFormat.MEDIUM);
					return dateFormat.format(date);
				} else if (command == 'f') {
					DateFormat dateFormat = DateFormat
							.getTimeInstance(DateFormat.FULL);
					return dateFormat.format(date);
				}
			} else {
				if (command == 's') {
					DateFormat dateFormat = DateFormat.getDateTimeInstance(
							DateFormat.SHORT, DateFormat.SHORT);
					return dateFormat.format(date);
				} else if (command == 'm') {
					DateFormat dateFormat = DateFormat.getDateTimeInstance(
							DateFormat.MEDIUM, DateFormat.MEDIUM);
					return dateFormat.format(date);
				} else if (command == 'f') {
					DateFormat dateFormat = DateFormat.getDateTimeInstance(
							DateFormat.FULL, DateFormat.FULL);
					return dateFormat.format(date);
				}
			}
			return formatDateDefault(date);
		} else {
			DateFormat tempFormat = new SimpleDateFormat(pattern);
			try {
				return tempFormat.format(date);
			} catch (Exception e) {
				return formatDateDefault(date);
			}
		}
	}

	/**
	 * @param value
	 */
	protected static synchronized String formatDateDefault(Date date) {
		if (DEFAULT_DATEFORMAT == null) {
			DEFAULT_DATEFORMAT = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss_SSS"); //$NON-NLS-1$
		}
		return DEFAULT_DATEFORMAT.format(date);
	}

	/**
	 */
	protected static String formatFloat(Object value, String format) {
		if (value == null) {
			return EMPTY;
		}
		double number = 0;
		if (value instanceof Number) {
			number = ((Number) value).doubleValue();
		} else {
			if (value instanceof Date) {
				number = ((Date) value).getTime();
			} else {
				if (value instanceof String) {
					try {
						number = Double.parseDouble((String) value);
					} catch (NumberFormatException e) {
						return (String) value;
					}
				} else {
					return value.toString();
				}
			}
		}
		String pattern = parseArgsString(format);
		NumberFormat tempFormat = null;
		if (pattern == null) {
			tempFormat = NumberFormat.getNumberInstance();
		} else {
			tempFormat = new DecimalFormat(pattern);
		}
		try {
			return tempFormat.format(number);
		} catch (Exception e) {
			return value.toString();
		}
	}

	/**
	 */
	protected static String formatInteger(Object value, String format) {
		if (value == null) {
			return EMPTY;
		}
		char base = 'd';
		if (format.length() > 1) {
			base = format.charAt(1);
		}
		long number = 0;
		if (value instanceof Number) {
			number = ((Number) value).longValue();
		} else {
			if (value instanceof Date) {
				number = ((Date) value).getTime();
			} else {
				if (value instanceof String) {
					try {
						number = Long.parseLong((String) value);
					} catch (NumberFormatException e) {
						return (String) value;
					}
				} else {
					return value.toString();
				}
			}
		}
		try {
			if (base == 'b') {
				return Long.toString(number, 2);
			}
			if (base == 'o') {
				return Long.toString(number, 8);
			}
			if (base == 'd') {
				return Long.toString(number, 10);
			}
			if (base == 'x') {
				return Long.toString(number, 16);
			}
			return Long.toString(number, 10);
		} catch (Exception e) {
			return value.toString();
		}
	}

	protected static String formatPath(Object value, String format) {
		String trimmed = FileTools.trimPath(StringTools.safeString(value));
		if (format.length() >= 3) {
			// first remove all artifacts
			if (format.indexOf("p-/") >= 0) {
				// ensure prefix slash removed, except we're root
				if (trimmed.length() > 1 && trimmed.startsWith("/")
						|| trimmed.startsWith("\\")) {
					trimmed = trimmed.substring(1);
				}
			}
			if (format.indexOf("p-.") >= 0) {
				// ensure prefix dot removed
				if (trimmed.startsWith(".")) {
					trimmed = trimmed.substring(1);
				}
			}
			if (format.indexOf("s-/") >= 0) {
				// ensure suffix slash removed, except we're root
				if (trimmed.length() > 1 && trimmed.endsWith("/")
						|| trimmed.endsWith("\\")) {
					trimmed = trimmed.substring(0, trimmed.length() - 1);
				}
			}
			if (format.indexOf("s-.") >= 0) {
				// ensure suffix dot removed
				if (trimmed.endsWith(".")) {
					trimmed = trimmed.substring(0, trimmed.length() - 1);
				}
			}
			if (format.indexOf("p+/") >= 0) {
				// ensure prefix with slash, except we're empty
				if (trimmed.length() > 0 && !trimmed.startsWith("/")
						&& !trimmed.startsWith("\\")) {
					trimmed = "/" + trimmed;
				}
			}
			if (format.indexOf("s+/") >= 0) {
				// ensure suffix with slash, except we're empty
				if (trimmed.length() > 0 && !trimmed.endsWith("/")
						&& !trimmed.endsWith("\\")) {
					trimmed = trimmed + "/";
				}
			}
			if (format.indexOf("p+.") >= 0) {
				// ensure prefix with dot, except we're empty
				if (trimmed.length() > 0 && !trimmed.startsWith(".")) {
					trimmed = "." + trimmed;
				}
			}
			if (format.indexOf("s+.") >= 0) {
				// ensure suffix with dot, except we're empty
				if (trimmed.length() > 0 && !trimmed.endsWith(".")) {
					trimmed = trimmed + ".";
				}
			}
		}
		return trimmed;
	}

	protected static String formatString(Object value, String format) {
		String result = null;
		if (value == null) {
			result = EMPTY;
		} else {
			result = value.toString();
		}
		String[] args = parseArgs(format);
		if (args.length == 0) {
			return result;
		}
		int rangeStart = 0;
		int rangeStop = result.length() - 1;
		try {
			rangeStart = Integer.parseInt(args[0]);
			if (args.length == 2) {
				rangeStop = Integer.parseInt(args[1]);
			}
		} catch (NumberFormatException e) {
			return result;
		}
		if (rangeStart < 0) {
			rangeStart = result.length() + rangeStart;
		}
		if (rangeStop < 0) {
			rangeStop = result.length() + rangeStop;
		}
		if (rangeStart < 0) {
			rangeStart = 0;
		}
		if (rangeStop < 0) {
			rangeStop = 0;
		}
		if (rangeStart > rangeStop) {
			rangeStop = rangeStart - 1;
		}
		if (rangeStart > result.length()) {
			rangeStart = result.length();
		}
		if (rangeStop >= result.length()) {
			rangeStop = result.length() - 1;
		}
		return result.substring(rangeStart, rangeStop + 1);
	}

	/**
	 * Get the common prefix contained both in a and b.
	 * 
	 * Example:
	 * 
	 * <pre>
	 * a "foo"
	 * b "bar"
	 * -> ""
	 * </pre>
	 * 
	 * <pre>
	 * a "gnu"
	 * b "gnat"
	 * -> "gn"
	 * </pre>
	 * 
	 * @param a
	 * @param b
	 * @param ignoreCase
	 * @return The common prefix contained both in a and b.
	 */
	public static String getCommonPrefix(String a, String b, boolean ignoreCase) {
		int lengthA = a.length();
		int lengthB = b.length();
		int max = lengthA > lengthB ? lengthB : lengthA;
		int i = 0;
		String tmpA;
		String tmpB;
		if (ignoreCase) {
			tmpA = a.toLowerCase();
			tmpB = b.toLowerCase();
		} else {
			tmpA = a;
			tmpB = b;
		}
		for (; i < max; i++) {
			if (tmpA.charAt(i) != tmpB.charAt(i)) {
				break;
			}
		}
		return a.substring(0, i);
	}

	/**
	 * The first line of text (all characters up to the first occurrence of
	 * either "\n" or "\r".
	 * 
	 * @param text
	 *            The text where the first line is looked up.
	 * 
	 * @return The first line of text
	 */
	public static String getFirstLine(String text) {
		if (text == null) {
			return EMPTY;
		}

		int indexCR = text.indexOf(CR);
		if (indexCR == -1) {
			indexCR = text.length();
		}
		int indexLF = text.indexOf(LF);
		if (indexLF == -1) {
			indexLF = text.length();
		}

		return text.substring(0, (indexCR > indexLF) ? indexLF : indexCR);
	}

	public static String getLeading(String value, int count) {
		if (value == null || value.length() <= count || count <= 3) {
			return value;
		}
		return value.substring(0, count - 3) + "...";
	}

	/**
	 * The number of lines in <code>text</code>. This is 1 + the number of "\n"
	 * in <code>text</code>.
	 * 
	 * @param text
	 *            The text where the lines are counted.
	 * @return The number of lines in <code>text</code>. This is 1 + the number
	 *         of "\n" in <code>text</code>.
	 */
	public static int getLineCount(String text) {
		int count = 1;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\n') {
				count++;
			}
		}
		return count;
	}

	/**
	 * Get a value for the command line option pattern "-<option> <value>".
	 * 
	 * @param option
	 *            the name of the option
	 * @param args
	 *            command line args to search
	 * @return the options value or <code>null</code>
	 */
	public static String getOptionValue(String option, String[] args) {
		String lookupArg = "-" + option; //$NON-NLS-1$
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (lookupArg.equals(arg)) {
				return args[++i];
			}
		}
		return null;
	}

	public static String getTrailing(String value, int count) {
		if (value == null || value.length() <= count) {
			return value;
		}
		return "..."
				+ value.substring(value.length() - count + 3, value.length());
	}

	/**
	 * <code>true</code> if <code>value</code> is "empty" in any respect.
	 * <p>
	 * This is the case when value == null, value has no characters or only
	 * whitespace.
	 * 
	 * @param value
	 *            The value to be inspected for emptyness.
	 * @return <code>true</code> if <code>value</code> is "empty" in any
	 *         respect.
	 */
	static public boolean isEmpty(char[] value) {
		if ((value == null) || (value.length == 0)) {
			return true;
		}
		for (int i = 0; i < value.length; i++) {
			if (!Character.isWhitespace(value[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <code>true</code> if <code>value</code> is "empty" in any respect.
	 * <p>
	 * This is the case when value == null, value has no characters or only
	 * whitespace.
	 * 
	 * @param value
	 *            The value to be inspected for emptyness.
	 * @return <code>true</code> if <code>value</code> is "empty" in any
	 *         respect.
	 */
	static public boolean isEmpty(String value) {
		return (value == null) || (value.length() == 0)
				|| (value.trim().length() == 0);
	}

	/**
	 * <code>true</code> if <code>value</code> seems to be a numeric value. To
	 * qualify only the first character is examined. <code>value</code>
	 * qualifies as numeric if the first character is a digit, a "+", a "-" or a
	 * ".".
	 * 
	 * @param value
	 * @return <code>true</code> if <code>value</code> seems to be numeric.
	 */
	public static boolean isNumeric(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		char c = value.charAt(0);
		return Character.isDigit(c) || c == '-' || c == '+' || c == '.';
	}

	/**
	 * Joins a List of Strings to a single one. All elements are separated by
	 * the <code>separator</code>.
	 * 
	 * @param elements
	 *            List of Strings
	 * @param separator
	 *            a String used as a separator between two elements, e.g. "\n"
	 *            for a new line. May be <code>null</code>.
	 * @return the joined string
	 */
	public static String join(List<String> elements, String separator) {
		return join(elements.toArray(new String[elements.size()]), separator);
	}

	/**
	 * Joins an array of Strings to a single one. All elements are separated by
	 * the <code>separator</code>.
	 * 
	 * @param elements
	 *            array of Strings
	 * @param separator
	 *            a String used as a separator between two elements, e.g. "\n"
	 *            for a new line. May be <code>null</code>.
	 * @return the joined string
	 */
	public static String join(String[] elements, String separator) {
		int last = elements.length - 1;
		if (last == -1) {
			return EMPTY;
		}
		if (last == 0) {
			return elements[0];
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < last; i++) {
			buffer.append(elements[i]);
			if (separator != null) {
				buffer.append(separator);
			}
		}
		buffer.append(elements[last]);
		return buffer.toString();
	}

	public static String padLeft(String value, int count) {
		if (value == null || value.length() >= count) {
			return value;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = count - value.length(); i > 0; i--) {
			sb.append(" ");
		}
		sb.append(value);
		return sb.toString();
	}

	public static String padRight(String value, int count) {
		if (value == null || value.length() >= count) {
			return value;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		for (int i = count - value.length(); i > 0; i--) {
			sb.append(" ");
		}
		return sb.toString();
	}

	protected static String[] parseArgs(String string) {
		String tempArgs = parseArgsString(string);
		if (tempArgs == null) {
			return NO_ARGS;
		}
		return tempArgs.split(",");
	}

	protected static String parseArgsString(String string) {
		int open = string.indexOf('(');
		int close = string.indexOf(')');
		if ((open == -1) || (close == -1) || (open > close)) {
			return null;
		}
		return string.substring(open + 1, close);
	}

	/**
	 * Parse a commandline string with the default escape character.
	 * 
	 * @param line
	 *            The commandline string.
	 * @return The array of string tokens in the commandline string.
	 */
	static public String[] parseCommandline(String line) {
		return parseCommandline(line, '\\');
	}

	/**
	 * Parse a commandline string.
	 * 
	 * @param line
	 *            The commandline string.
	 * @return The array of string tokens in the commandline string.
	 */
	static public String[] parseCommandline(String line, char escape) {
		List result = new ArrayList();
		StringBuilder sb = new StringBuilder();
		boolean escaped = false;
		boolean quoted = false;
		boolean commented = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (commented) {
				if (c == '\n') {
					commented = false;
				}
				continue;
			}
			if (escaped) {
				escaped = false;
				if (c != '"') {
					sb.append(escape);
				}
				sb.append(c);
				continue;
			}
			if (c == escape) {
				escaped = true;
				continue;
			}
			if (c == '"') {
				quoted = !quoted;
				continue;
			}
			if (!quoted && (c == '#')) {
				if (sb.length() > 0) {
					result.add(sb.toString());
					sb.setLength(0);
				}
				commented = true;
				continue;
			}
			if (!quoted && Character.isWhitespace(c)) {
				if (sb.length() > 0) {
					result.add(sb.toString());
					sb.setLength(0);
				}
				continue;
			}
			sb.append(c);
		}
		if (escaped) {
			sb.append(escape);
		}
		if (sb.length() > 0) {
			result.add(sb.toString());
			sb.setLength(0);
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Create a new path from the concatenation of prefix and name.
	 * 
	 * @param prefix
	 * @param separator
	 * @param name
	 * @return The new path
	 */
	public static String pathAppend(String prefix, String separator, String name) {
		if (StringTools.isEmpty(prefix)) {
			return name;
		} else {
			return prefix + separator + name;
		}
	}

	static public String quote(String value) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			// todo Escapes are hardcoded here!
			if (c == '"') {
				sb.append('\\');
			} else if (c == '\\') {
				sb.append('\\');
			} else if (c == '\r') {
				sb.append('\\');
				c = 'r';
			} else if (c == '\n') {
				sb.append('\\');
				c = 'n';
			} else if (c == '\t') {
				sb.append('\\');
				c = 't';
			}
			sb.append(c);
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * Create a string representation of <code>object</code> that is guaranteed
	 * not to fail in any case.
	 * 
	 * @param object
	 *            The object to be printed.
	 * @return Create a string representation of <code>object</code> that is
	 *         guaranteed not to fail in any case.
	 */
	public static String safeString(Object object) {
		try {
			return String.valueOf(object);
		} catch (RuntimeException e) {
			return "<unprintable>"; //$NON-NLS-1$
		}
	}

	/**
	 * Create a byte array from the string. This is simply a fast version of
	 * getBytes, ignoring any encoding.
	 * <p>
	 * If you use this, you should be sure you will encounter valid ascii
	 * characters only!
	 * 
	 * @param value
	 * @return A byte array created from value, ignoring high byte.
	 */
	static public byte[] toByteArray(String value) {
		byte[] result = new byte[value.length()];
		value.getBytes(0, result.length, result, 0);
		return result;
	}

	/**
	 * Convert value to camel case notation.
	 * <p>
	 * value is tokenized at non alphanumeric characters, separator characters
	 * are ignored. all tokens are converted to lowercase and then for all
	 * tokens except the first the first character is uppercased.
	 * <p>
	 * The implementation tries to detect if value is already a camel case
	 * string and preserves it.
	 * 
	 * @param value
	 * @return value in camel case notation
	 */
	static public String toCamelCase(String value, String allowed) {
		StringTokenizer st = new StringTokenizer(value, allowed, true);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			String element = st.nextToken();
			if (allowed.indexOf(element) >= 0) {
				continue;
			}
			String delimiter = "";
			if (st.hasMoreTokens()) {
				delimiter = st.nextToken();
			}
			String[] tokens = element.split("[^a-zA-Z0-9]");
			int length = tokens.length;
			if (length == 1) {
				String token = tokens[0];
				if (token.length() > 0) {
					if (Character.isLowerCase(token.charAt(0))) {
						// assume already in camel case...
						sb.append(token);
					} else {
						sb.append(token.toLowerCase());
					}
				} else {
					sb.append(token.toLowerCase());
				}
			} else {
				for (int i = 0; i < length; i++) {
					String token = tokens[i];
					if (token.length() > 0) {
						if (i == 0) {
							sb.append(Character.toLowerCase(token.charAt(0)));
						} else {
							sb.append(Character.toUpperCase(token.charAt(0)));
						}
						sb.append(token.substring(1).toLowerCase());
					}
				}
			}
			sb.append(delimiter);
		}
		return sb.toString();
	}

	/**
	 * Create a string from the byte array. This is simply a fast version of new
	 * String(), ignoring any encoding.
	 * <p>
	 * If you use this, you should be sure you will encounter valid ascii
	 * characters only!
	 * 
	 * @param value
	 * @return A string created from the byte values in value.
	 */
	static public String toString(byte[] value) {
		return new String(value, 0);
	}

	/**
	 * Create a string containing only the alphanumeric content of
	 * <code>value</code>.
	 * 
	 * @param value
	 *            The string to be trimmed.
	 * @return A string containing only the alphanumeric content of
	 *         <code>value</code>.
	 */
	static public String trimAlphaNumeric(String value) {
		StringReader reader;
		StringWriter writer;
		char[] buffer;

		reader = new StringReader(value);
		buffer = new char[1];
		writer = new StringWriter();

		try {
			while (reader.read(buffer) != -1) {
				if (Character.isLetterOrDigit(buffer[0])) {
					writer.write(buffer);
				}
			}
		} catch (IOException ex) {
			// working in memory; ignore
		}
		return writer.toString();
	}

	/**
	 * Create a new string from <code>value</code> without leading whitespace.
	 * 
	 * @param value
	 *            The string to be stripped.
	 * 
	 * @return A copy of <code>value</code> with leading whitespace stripped.
	 */
	public static String trimLeft(String value) {
		int i = 0;
		int len = value.length();
		while (i < len) {
			if (Character.isWhitespace(value.charAt(i))) {
				i++;
				continue;
			}
			break;
		}
		return value.substring(i);
	}

	/**
	 * Create a new string from <code>value</code> without trailing whitespace.
	 * 
	 * @param value
	 *            The string to be stripped.
	 * 
	 * @return A copy of <code>value</code> with trailing whitespace stripped.
	 */
	public static String trimRight(String value) {
		int i = value.length() - 1;
		while (i >= 0) {
			if (Character.isWhitespace(value.charAt(i))) {
				i--;
				continue;
			}
			break;
		}
		return value.substring(0, i + 1);
	}

	/**
	 * Remove the quotes around a string. Any character escaped by "\" within
	 * the quoted string is embedded unescaped.
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	static public String unquote(String value) throws IOException {
		char[] chars = value.toCharArray();
		int i = 0;
		if (i == chars.length) {
			return "";
		}
		char c = chars[i++];
		if (c != '"') {
			// not quoted
			return value;
		}
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (i == chars.length) {
				throw new IOException("preliminary end of input");
			}
			c = chars[i++];
			if (c == '\\') {
				if (i == chars.length) {
					throw new IOException("preliminary end of input");
				}
				c = chars[i++];
				String escaped = ESCAPES.get(Character.valueOf(c));
				if (escaped != null) {
					sb.append(escaped);
				}
				continue;
			}
			if (c == '"') {
				if (i != chars.length) {
					throw new IOException("preliminary end of quoted string");
				}
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
