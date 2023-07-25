/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.intarsys.tools.format;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats messages according to very simple substitution rules. Substitutions
 * can be made 1, 2 or more arguments.
 * 
 * <p>
 * For example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;)
 * </pre>
 * 
 * will return the string "Hi there.".
 * <p>
 * The {} pair is called the <em>formatting anchor</em>. It serves to designate
 * the location where arguments need to be substituted within the message
 * pattern.
 * <p>
 * In case your message contains the '{' or the '}' character, you do not have
 * to do anything special unless the '}' character immediately follows '{'. For
 * example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Set {1,2,3} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * 
 * will return the string "Set {1,2,3} is not equal to 1,2.".
 * 
 * <p>
 * If for whatever reason you need to place the string "{}" in the message
 * without its <em>formatting anchor</em> meaning, then you need to escape the
 * '{' character with '\', that is the backslash character. Only the '{'
 * character should be escaped. There is no need to escape the '}' character.
 * For example,
 * 
 * <pre>
 * MessageFormatter.format(&quot;Set \\{} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * 
 * will return the string "Set {} is not equal to 1,2.".
 * 
 * <p>
 * The escaping behavior just described can be overridden by escaping the escape
 * character '\'. Calling
 * 
 * <pre>
 * MessageFormatter.format(&quot;File name is C:\\\\{}.&quot;, &quot;file.zip&quot;);
 * </pre>
 * 
 * will return the string "File name is C:\file.zip".
 * 
 * <p>
 * The formatting conventions are different than those of {@link MessageFormat}
 * which ships with the Java platform. This is justified by the fact that
 * SLF4J's implementation is 10 times faster than that of {@link MessageFormat}.
 * This local performance difference is both measurable and significant in the
 * larger context of the complete logging processing chain.
 * 
 * <p>
 * See also {@link #simple(String, Object...)} and
 * {@link #log(String, Object[])} methods for more details.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 */
public final class Format {

	private static final char BRACKET_RIGHT = ']';

	private static final char BRACKET_LEFT = '[';

	private static final String COMMA = ", ";

	private static final int BUFFER_SIZE = 50;

	static final char DELIM_START = '{';

	static final char DELIM_STOP = '}';
	static final String DELIM_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';

	private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	private static void charArrayAppend(StringBuilder sbuf, char[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	// special treatment of array values was suggested by 'lizongbo'
	private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
		if (o == null) {
			sbuf.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			safeObjectAppend(sbuf, o);
		} else {
			// check for primitive array types because they
			// unfortunately cannot be cast to Object[]
			if (o instanceof boolean[]) {
				booleanArrayAppend(sbuf, (boolean[]) o);
			} else if (o instanceof byte[]) {
				byteArrayAppend(sbuf, (byte[]) o);
			} else if (o instanceof char[]) {
				charArrayAppend(sbuf, (char[]) o);
			} else if (o instanceof short[]) {
				shortArrayAppend(sbuf, (short[]) o);
			} else if (o instanceof int[]) {
				intArrayAppend(sbuf, (int[]) o);
			} else if (o instanceof long[]) {
				longArrayAppend(sbuf, (long[]) o);
			} else if (o instanceof float[]) {
				floatArrayAppend(sbuf, (float[]) o);
			} else if (o instanceof double[]) {
				doubleArrayAppend(sbuf, (double[]) o);
			} else {
				objectArrayAppend(sbuf, (Object[]) o, seenMap);
			}
		}
	}

	private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	protected static Throwable getThrowableCandidate(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			return null;
		}

		final Object lastEntry = argArray[argArray.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}

	private static void intArrayAppend(StringBuilder sbuf, int[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	protected static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
		return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
	}

	protected static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {

		if (delimeterStartIndex == 0) {
			return false;
		}
		char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
		return potentialEscape == ESCAPE_CHAR;
	}

	/**
	 * Expand pattern based on the "{}" placeholder syntax known from slf4j and
	 * return a {@link LogTuple}, supporting an exception as the last parameter.
	 * 
	 * @param pattern
	 *            The message pattern which will be parsed and formatted
	 * @param argArray
	 *            An array of arguments to be substituted in place of formatting
	 *            anchors
	 * @return The formatted message
	 */
	public static LogTuple log(final String pattern, final Object[] argArray) {
		if (argArray == null) {
			return new LogTuple(pattern);
		}
		Throwable throwableCandidate = getThrowableCandidate(argArray);
		if (pattern == null) {
			return new LogTuple(null, throwableCandidate);
		}
		return new LogTuple(simple(pattern, argArray), throwableCandidate);
	}

	private static void longArrayAppend(StringBuilder sbuf, long[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
		sbuf.append(BRACKET_LEFT);
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				deeplyAppendParameter(sbuf, a[i], seenMap);
				if (i != len - 1) {
					sbuf.append(COMMA);
				}
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			sbuf.append("...");
		}
		sbuf.append(BRACKET_RIGHT);
	}

	/**
	 * Expand pattern based on the "%s" placeholder syntax known from Java
	 * {@link java.text.Format} and return the expanded string.
	 * 
	 * @param pattern
	 * @param argArray
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String printf(final String pattern, final Object... argArray) {
		return new Formatter().format(pattern, argArray).toString();
	}

	private static void safeObjectAppend(StringBuilder sbuf, Object o) {
		try {
			String oAsString = o.toString();
			sbuf.append(oAsString);
		} catch (Throwable t) {
			sbuf.append("[FAILED toString()]");
		}

	}

	private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
		sbuf.append(BRACKET_LEFT);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1) {
				sbuf.append(COMMA);
			}
		}
		sbuf.append(BRACKET_RIGHT);
	}

	/**
	 * Expand pattern based on the "{}" placeholder syntax known from slf4j and
	 * return the string created.
	 * 
	 * @param pattern
	 *            The message pattern which will be parsed and formatted
	 * @param argArray
	 *            An array of arguments to be substituted in place of formatting
	 *            anchors
	 * @return The formatted message
	 */
	public static String simple(final String pattern, final Object... argArray) {
		if (argArray == null || argArray.length == 0) {
			return pattern;
		}

		int i = 0;
		int j;
		// use string builder for better multicore performance
		StringBuilder sbuf = new StringBuilder(pattern.length() + BUFFER_SIZE);

		int k;
		for (k = 0; k < argArray.length; k++) {

			j = pattern.indexOf(DELIM_STR, i);

			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return pattern;
				} else { // add the tail string which contains no variables and
							// return
					// the result.
					sbuf.append(pattern.substring(i, pattern.length()));
					return sbuf.toString();
				}
			} else {
				if (isEscapedDelimeter(pattern, j)) {
					if (!isDoubleEscaped(pattern, j)) {
						k--; // DELIM_START was escaped, thus should not be
								// incremented
						sbuf.append(pattern.substring(i, j - 1));
						sbuf.append(DELIM_START);
						i = j + 1;
					} else {
						// The escape character preceding the delimiter start is
						// itself escaped: "abc x:\\{}"
						// we have to consume one backward slash
						sbuf.append(pattern.substring(i, j - 1));
						deeplyAppendParameter(sbuf, argArray[k], new HashMap<>());
						i = j + 2;
					}
				} else {
					// normal case
					sbuf.append(pattern.substring(i, j));
					deeplyAppendParameter(sbuf, argArray[k], new HashMap<>());
					i = j + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		sbuf.append(pattern.substring(i, pattern.length()));
		return sbuf.toString();
	}

	private Format() {
	}
}
