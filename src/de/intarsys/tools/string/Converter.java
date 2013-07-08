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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.reader.ReaderTools;

/**
 * Simple string conversion utilities;
 */
public class Converter {

	public static final String ELEMENT_SEPARATOR = ";"; //$NON-NLS-1$

	public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	public static boolean asBoolean(String booleanString)
			throws ConverterException {
		booleanString = booleanString.toLowerCase().trim();
		if (booleanString.equals("false") || booleanString.equals("f") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("no") || booleanString.equals("n") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("0") || booleanString.equals("falsch") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("nein")) { //$NON-NLS-1$
			return false;
		}
		if (booleanString.equals("true") || booleanString.equals("t") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("yes") || booleanString.equals("y") //$NON-NLS-1$//$NON-NLS-2$
				|| booleanString.equals("1") || booleanString.equals("wahr") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("w") || booleanString.equals("ja") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("j")) { //$NON-NLS-1$
			return true;
		}
		throw new ConverterException("Can't parse boolean string: " //$NON-NLS-1$
				+ booleanString + " to a legal value"); //$NON-NLS-1$
	}

	public static boolean asBoolean(String booleanString, boolean defaultValue) {
		if (StringTools.isEmpty(booleanString)) {
			return defaultValue;
		}
		try {
			return asBoolean(booleanString);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static boolean asBooleanChecked(String booleanString,
			boolean defaultValue) throws ConverterException {
		if (StringTools.isEmpty(booleanString)) {
			return defaultValue;
		}
		return asBoolean(booleanString);
	}

	public static byte[] asBytes(String str) throws ConverterException {
		if (str.startsWith("[a]")) { //$NON-NLS-1$
			// dedicated ascii
			try {
				return str.substring(3).getBytes("ASCII");
			} catch (UnsupportedEncodingException e) {
				throw new ConverterException(e);
			}
		} else if (str.startsWith("[x]")) { //$NON-NLS-1$
			// dedicated hex
			return HexTools.hexStringToBytes(str.substring(3));
		} else if (str.startsWith("[@]")) { //$NON-NLS-1$
			// dedicated base 64
			return Base64.decode(str.substring(3));
		} else {
			// default base 64
			return Base64.decode(str.substring(3));
		}
	}

	public static double asDouble(String str) throws ConverterException {
		try {
			str = str.trim();
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			throw new ConverterException("Can't parse number string: " + str //$NON-NLS-1$
					+ " to a legal value"); //$NON-NLS-1$
		}
	}

	public static double asDouble(String str, double defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return asDouble(str);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static double asDoubleChecked(String str, double defaultValue)
			throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asDouble(str);
	}

	public static File asFile(String pValue) throws ConverterException {
		String str = pValue.trim();
		str = FileTools.trimPath(str);
		if (str.equals("")) { //$NON-NLS-1$
			throw new ConverterException("Parameter " + pValue //$NON-NLS-1$
					+ " is not a valid Filename"); //$NON-NLS-1$
		}
		try {
			return new File(str);
		} catch (NullPointerException e) {
			throw new ConverterException("Can't parse file string: " + str //$NON-NLS-1$
					+ " to a file"); //$NON-NLS-1$
		}
	}

	public static float asFloat(String str) throws ConverterException {
		try {
			str = str.trim();
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			throw new ConverterException("Can't parse number string: " + str //$NON-NLS-1$
					+ " to a legal value"); //$NON-NLS-1$
		}
	}

	public static float asFloat(String str, float defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return asFloat(str);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static float[] asFloatArray(String value) {
		if (value == null) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR,
				false);
		float[] result = new float[tk.countTokens()];
		int i = 0;
		try {
			while (tk.hasMoreTokens()) {
				String token = tk.nextToken();
				result[i] = Float.parseFloat(token.trim());
				i++;
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return result;
	}

	public static float asFloatChecked(String str, float defaultValue)
			throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asFloat(str);
	}

	public static int[] asIntArray(String value) {
		if (value == null) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR,
				false);
		int[] result = new int[tk.countTokens()];
		int i = 0;
		try {
			while (tk.hasMoreTokens()) {
				String token = tk.nextToken();
				result[i] = Integer.parseInt(token.trim());
				i++;
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return result;
	}

	public static int asInteger(String str) throws ConverterException {
		try {
			str = str.trim();
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new ConverterException("Can't parse integer string: " + str //$NON-NLS-1$
					+ " to a legal value"); //$NON-NLS-1$
		}
	}

	public static int asInteger(String str, int defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return asInteger(str);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static int asIntegerChecked(String str, int defaultValue)
			throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asInteger(str);
	}

	public static List<String> asList(String string) {
		List<String> result = new ArrayList<String>();
		Reader r = new StringReader(string);
		try {
			while (true) {
				String value = ReaderTools.readToken(r, ';');
				if (value == null) {
					break;
				}
				result.add(value);
			}
		} catch (IOException e) {
			// no io exception from string reader
		}
		return result;
	}

	public static long asLong(String str) throws ConverterException {
		try {
			str = str.trim();
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			throw new ConverterException("Can't parse integer string: " + str //$NON-NLS-1$
					+ " to a legal value"); //$NON-NLS-1$
		}
	}

	public static long asLong(String str, long defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return asLong(str);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static long asLongChecked(String str, long defaultValue)
			throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asLong(str);
	}

	/**
	 * Read a {@link Map} from string.
	 * 
	 * The string is a ";" separated key/value list.<br>
	 * Example
	 * 
	 * <pre>
	 * a=b;foo=bar
	 * </pre>
	 * 
	 * The values are plain literals or quoted strings (@see
	 * {@link ReaderTools#readEntry(Reader, char)}.
	 * 
	 * The resulting map is "flat" (not nested).
	 * 
	 * <pre>
	 * string ::= entry [ ';' entry ]*
	 * entry ::= key [ '=' value ]
	 * key ::= id [ '.' id ]
	 * </pre>
	 * 
	 * @param string
	 * @return The parsed {@link Map}
	 */
	public static Map<String, String> asMap(String string) {
		Map<String, String> map = new HashMap<String, String>(5);
		if (StringTools.isEmpty(string)) {
			return map;
		}
		Reader r = new StringReader(string);
		try {
			while (true) {
				Map.Entry<String, String> entry = ReaderTools.readEntry(r, ';');
				if (entry == null) {
					break;
				}
				if (entry.getKey() != null) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
		} catch (IOException e) {
			// no io exception from string reader
		}
		return map;
	}

	public static Map<String, Boolean> asMapBoolean(String string) {
		Map<String, String> stringMap = asMap(string);
		Map<String, Boolean> boolMap = new HashMap<String, Boolean>(
				stringMap.size() * 2);
		for (Map.Entry<String, String> entry : stringMap.entrySet()) {
			Boolean value = Boolean.valueOf(entry.getValue());
			boolMap.put(entry.getKey(), value);
		}
		return boolMap;
	}

	public static String asString(String str, String defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		} else {
			return str.trim();
		}
	}

	public static String[] asStringArray(String value) {
		if (value == null) {
			return null;
		}
		return value.split(ELEMENT_SEPARATOR);
	}

	public static Boolean asThreeState(String booleanString)
			throws ConverterException {
		booleanString = booleanString.toLowerCase().trim();
		if (booleanString.equals("false") || booleanString.equals("f") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("no") || booleanString.equals("n") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("0") || booleanString.equals("falsch") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("nein")) { //$NON-NLS-1$
			return Boolean.FALSE;
		}
		if (booleanString.equals("true") || booleanString.equals("t") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("yes") || booleanString.equals("y") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("1") || booleanString.equals("wahr") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("w") || booleanString.equals("ja") //$NON-NLS-1$ //$NON-NLS-2$
				|| booleanString.equals("j")) { //$NON-NLS-1$
			return Boolean.TRUE;
		}
		if (booleanString.equals("undeterminate") //$NON-NLS-1$
				|| booleanString.equals("undefined") //$NON-NLS-1$
				|| booleanString.equals("?") || booleanString.equals("u")) { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		throw new ConverterException("Can't parse boolean string: " //$NON-NLS-1$
				+ booleanString + " to a legal value"); //$NON-NLS-1$
	}

	public static Boolean asThreeState(String booleanString,
			Boolean defaultValue) {
		if (StringTools.isEmpty(booleanString)) {
			return defaultValue;
		}
		try {
			return asBoolean(booleanString);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	private Converter() {
		super();
	}

}
