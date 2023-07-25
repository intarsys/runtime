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
package de.intarsys.tools.string;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.json.Json;
import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectTools;

/**
 * Simple string conversion utilities;
 */
public class Converter {

	/*
	 * Thread safe static field initialization, see
	 * https://www.oracle.com/technical-resources/articles/javase/bloch-effective-08-qa.html
	 */
	private static class FunctionAsMapHolder {
		static final Function<String, Map<String, Object>> FIELD = createFunctionAsMap();
	}

	public static final String ELEMENT_SEPARATOR = ";"; //$NON-NLS-1$

	public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	public static boolean asBoolean(String booleanString, boolean defaultValue) {
		try {
			return asBooleanStrict(booleanString, defaultValue);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static boolean asBooleanStrict(String booleanString, boolean defaultValue) throws ConverterException {
		if (StringTools.isEmpty(booleanString)) {
			return defaultValue;
		}
		return toBoolean(booleanString);
	}

	@SuppressWarnings({ "java:S1168" })
	public static byte[] asBytes(String str) throws ConverterException {
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return new byte[0];
		}
		if (str.charAt(0) == '[') {
			if (str.startsWith("[a]")) { //$NON-NLS-1$
				// dedicated ascii
				return str.substring(3).getBytes(StandardCharsets.US_ASCII);
			} else if (str.startsWith("[x]")) { //$NON-NLS-1$
				// dedicated hex
				return HexTools.hexStringToBytes(str.substring(3));
			} else if (str.startsWith("[@]")) { //$NON-NLS-1$
				// dedicated base 64
				try {
					return Base64.decode(str.substring(3));
				} catch (IOException e) {
					throw new ConverterException(e.getMessage(), e);
				}
			} else {
				// unknown
				throw new ConverterException("unknown conversion instruction " + str);
			}
		}
		// default base 64
		try {
			return Base64.decode(str);
		} catch (IOException e) {
			throw new ConverterException(e.getMessage(), e);
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

	public static double asDoubleStrict(String str, double defaultValue) throws ConverterException {
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

	@SuppressWarnings({ "java:S1168" })
	public static float[] asFloatArray(String value) {
		if (value == null) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR, false);
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

	public static float asFloatStrict(String str, float defaultValue) throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asFloat(str);
	}

	@SuppressWarnings({ "java:S1168" })
	public static int[] asIntArray(String value) {
		if (value == null) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR, false);
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
			if (str.startsWith("0x")) {
				return Integer.parseInt(str.substring(2), 16);
			}
			if (str.startsWith("0o")) {
				return Integer.parseInt(str.substring(2), 8);
			}
			if (str.startsWith("0b")) {
				return Integer.parseInt(str.substring(2), 2);
			}
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

	public static int asIntegerStrict(String str, int defaultValue) throws ConverterException {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		}
		return asInteger(str);
	}

	public static List<String> asList(String string) {
		List<String> result = new ArrayList<>();
		if (string == null) {
			return result;
		}
		Reader r = new StringReader(string);
		try {
			while (true) {
				Token value = ReaderTools.readToken(r, ';');
				if (value == null) {
					break;
				}
				result.add(value.getValue());
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

	public static long asLongStrict(String str, long defaultValue) throws ConverterException {
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
	 * a=b;foo.bar=gnu
	 * </pre>
	 * 
	 * The values are plain literals or quoted strings ({@link ReaderTools#readEntry(Reader, char)}.
	 * 
	 * The resulting map is "flat" (no nested maps), even with "." separated
	 * keys.
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
	public static Map<String, Object> asMap(String string) {
		if (Json.isJson(string)) {
			return asMapFromJson(string);
		} else {
			return asMapFromKeyValue(string);
		}
	}

	protected static Map<String, Object> asMapFromJson(String string) {
		return getFunctionAsMap().apply(string);
	}

	protected static Map<String, Object> asMapFromKeyValue(String string) {
		Map<String, Object> map = new HashMap<>(5);
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

	public static String asString(String str, String defaultValue) {
		if (StringTools.isEmpty(str)) {
			return defaultValue;
		} else {
			return str.trim();
		}
	}

	@SuppressWarnings({ "java:S1168" })
	public static String[] asStringArray(String value) {
		if (value == null) {
			return null;
		}
		return value.split(ELEMENT_SEPARATOR);
	}

	@SuppressWarnings("java:S2447")
	public static Boolean asThreeState(String booleanString) throws ConverterException {
		if (StringTools.isEmpty(booleanString)) {
			return null; // NOSPOTBUGS - intentionally null
		}
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

	public static Boolean asThreeState(String booleanString, Boolean defaultValue) {
		if (StringTools.isEmpty(booleanString)) {
			return defaultValue;
		}
		try {
			return toBoolean(booleanString);
		} catch (ConverterException e) {
			return defaultValue;
		}
	}

	public static Function<String, Map<String, Object>> createFunctionAsMap() {
		try {
			Class<?> clazz = ClassTools.createClass("de.intarsys.tools.jackson.JsonTools", Object.class, null);
			Method method = ObjectTools.findMethod(clazz, "asMap", false, String.class);
			return (value) -> {
				try {
					return (Map<String, Object>) method.invoke(null, value);
				} catch (Exception e) {
					throw new CompletionException(ExceptionTools.unwrap(e));
				}
			};
		} catch (Exception e) {
			return (value) -> new HashMap<>();
		}
	}

	protected static Function<String, Map<String, Object>> getFunctionAsMap() {
		return FunctionAsMapHolder.FIELD;
	}

	protected static boolean toBoolean(String booleanString) throws ConverterException {
		booleanString = booleanString.toLowerCase().trim();
		if ("false".equals(booleanString) || "f".equals(booleanString) //$NON-NLS-1$ //$NON-NLS-2$
				|| "no".equals(booleanString) || "n".equals(booleanString) //$NON-NLS-1$ //$NON-NLS-2$
				|| "0".equals(booleanString)) { //$NON-NLS-1$
			return false;
		}
		if ("true".equals(booleanString) || "t".equals(booleanString) //$NON-NLS-1$ //$NON-NLS-2$
				|| "yes".equals(booleanString) || "y".equals(booleanString) //$NON-NLS-1$//$NON-NLS-2$
				|| "1".equals(booleanString)) { //$NON-NLS-1$
			return true;
		}
		throw new ConverterException("Can't parse boolean string: " //$NON-NLS-1$
				+ booleanString + " to a legal value"); //$NON-NLS-1$
	}

	private Converter() {
		super();
	}

}
