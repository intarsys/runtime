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
package de.intarsys.tools.number;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A parser able to read a definition of numbers.
 * <p>
 * The parser supports single numbers, enumeration of numbers and intervals.
 * </p>
 * 
 * <pre>
 * S ::= NumberString
 * NumberString ::= ( Number | Interval) [ &quot;;&quot; (Number | Interval) ]*
 * Interval ::= Number &quot;-&quot; Number
 * Number ::= a valid number literal
 * </pre>
 * 
 */
public class NumberParser {

	public static NumberWrapper parse(String value) throws IOException {
		if (value == null) {
			return null;
		}
		return new NumberParser(false).parse(new StringReader(value));
	}

	public static NumberWrapper parseInteger(String value) throws IOException {
		if (value == null) {
			return null;
		}
		return new NumberParser(true).parse(new StringReader(value));
	}

	final private boolean integer;

	protected NumberParser(boolean integer) {
		super();
		this.integer = integer;
	}

	public boolean isInteger() {
		return integer;
	}

	protected boolean isIntervalSeparator(char c) {
		return (c == NumberInterval.SEPARATOR);
	}

	protected boolean isListSeparator(char c) {
		return (c == NumberList.SEPARATOR);
	}

	protected boolean isNumberChar(char c) {
		return c == '.' || c >= '0' || c <= '9' || c == '-' || c == '+'
				|| (c == ',' && !isInteger());
	}

	protected NumberWrapper parse(StringReader r) throws IOException {
		NumberList numberList = new NumberList();
		parse(r, numberList);
		return numberList;
	}

	protected void parse(StringReader r, NumberList numberList)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		Double number = null;
		NumberInterval interval = null;
		for (int i = r.read(); i > -1; i = r.read()) {
			char c = (char) i;
			if (Character.isWhitespace(c)) {
				//
			} else if (isListSeparator(c)) {
				if (interval != null) {
					if (interval.getTo() == null) {
						throw new IOException("number expected");
					}
					numberList.add(interval);
					interval = null;
					number = null;
				} else if (number != null) {
					numberList.add(new NumberInstance(number));
					number = null;
				}
			} else if (number != null && c == '-') {
				interval = new NumberInterval();
				interval.setFrom(number);
				number = null;
			} else if (isNumberChar(c)) {
				if (number != null) {
					throw new IOException("invalid char '" + c + "'");
				}
				sb.setLength(0);
				sb.append(c);
				number = parseNumber(r, sb);
				if (interval != null) {
					interval.setTo(number);
				}
			} else {
			}
		}
		if (interval != null) {
			if (interval.getTo() == null) {
				throw new IOException("number expected");
			}
			numberList.add(interval);
		} else if (number != null) {
			numberList.add(new NumberInstance(number));
		}
	}

	protected Double parseNumber(Reader r, StringBuilder sb) throws IOException {
		r.mark(1);
		int i = r.read();
		while (i != -1) {
			char c = (char) i;
			if (Character.isWhitespace(c)) {
				break;
			} else if (isListSeparator(c)) {
				break;
			} else if (isIntervalSeparator(c)) {
				break;
			} else if (isNumberChar(c)) {
				sb.append(c);
			} else {
				throw new IOException("invalid char '" + c + "'");
			}
			r.mark(1);
			i = r.read();
		}
		r.reset();
		try {
			return toNumber(sb);
		} catch (Exception e) {
			throw new IOException("number format exception");
		}
	}

	protected double toNumber(StringBuilder sb) {
		return Double.parseDouble(sb.toString());
	}
}
