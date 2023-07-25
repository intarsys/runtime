package de.intarsys.tools.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.collection.IterableTools;
import de.intarsys.tools.converter.ConversionException;

public class Json {

	public static JsonArray copy(JsonArray array) {
		JsonArray copy = Json.createArray();
		for (Object value : IterableTools.in(array.iterator())) {
			value = copy(value);
			copy.basicAdd(value);
		}
		return copy;
	}

	public static JsonObject copy(JsonObject object) {
		JsonObject copy = Json.createObject();
		for (String name : object.names()) {
			Object value = object.get(name);
			value = copy(value);
			copy.basicPut(name, value);
		}
		return copy;
	}

	public static Object copy(Object jsonObject) {
		if (jsonObject instanceof JsonObject) {
			return copy((JsonObject) jsonObject);
		} else if (jsonObject instanceof JsonArray) {
			return copy((JsonArray) jsonObject);
		}
		return jsonObject;
	}

	public static JsonArray createArray() {
		JsonArray result = new JsonArray();
		return result;
	}

	public static JsonArray createArray(Collection collection) {
		JsonArray result = new JsonArray();
		if (collection != null) {
			Iterator iter = collection.iterator();
			while (iter.hasNext()) {
				result.basicAdd(Json.wrap(iter.next()));
			}
		}
		return result;
	}

	public static JsonObject createObject() {
		JsonObject result = new JsonObject();
		return result;
	}

	public static JsonObject createObject(Map<String, Object> map) {
		JsonObject result = new JsonObject();
		if (map != null) {
			Iterator<Map.Entry<String, Object>> i = map.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry<String, Object> e = i.next();
				Object value = e.getValue();
				if (value != null) {
					result.basicPut(e.getKey(), wrap(value));
				}
			}
		}
		return result;
	}

	public static String doubleToString(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return "null";
		}
		String string = Double.toString(d);
		if (string.indexOf('.') >= 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.length() == 0) {
				return "0";
			}
		}
		return string;
	}

	/**
	 * {@code true} if {@code value} seems to be a valid JSON composite object.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isJson(String value) {
		return startsWith(value, "{[");
	}

	/**
	 * {@code true} if {@code value} seems to be a valid JSON array.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isJsonArray(String value) {
		return startsWith(value, "[");
	}

	/**
	 * {@code true} if {@code value} seems to be a valid JSON object.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isJsonObject(String value) {
		return startsWith(value, "{");
	}

	public static String numberToString(Number number) {
		// Shave off trailing zeros and decimal point, if possible.
		String string = number.toString();
		if (string.indexOf('.') >= 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.length() == 0) {
				return "0";
			}
		}
		return string;
	}

	public static String quote(String string) {
		StringWriter w = new StringWriter();
		try {
			JsonWriter.quote(string, w);
			return w.toString();
		} catch (IOException ignored) {
			return "";
		}
	}

	protected static boolean startsWith(String value, String chars) {
		for (int i = 0; i < value.length(); i++) {
			if (Character.isWhitespace(value.charAt(i))) {
				continue;
			}
			if (chars.indexOf(value.charAt(i)) >= 0) {
				return true;
			}
			return false;
		}
		return false;
	}

	public static boolean toBoolean(Object object) throws ConversionException {
		if (object == null) {
			// fall through
		} else if (object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object).equalsIgnoreCase("false"))) {
			return false;
		} else if (object.equals(Boolean.TRUE) // NOSONAR false positive
				|| (object instanceof String && ((String) object).equalsIgnoreCase("true"))) {
			return true;
		}
		throw new ConversionException("not a boolean.");
	}

	public static double toDouble(Object object) throws ConversionException {
		try {
			if (object == null) {
				// empty
			} else if (object instanceof Number) {
				return ((Number) object).doubleValue();
			} else if (object instanceof String) {
				return Double.parseDouble((String) object);
			}
		} catch (Exception e) {
			//
		}
		throw new ConversionException("not a double");
	}

	public static int toInt(Object object) throws ConversionException {
		try {
			if (object == null) {
				// fall through
			} else if (object instanceof Number) {
				return ((Number) object).intValue();
			} else if (object instanceof String) {
				return Integer.parseInt((String) object);
			}
		} catch (Exception e) {
			//
		}
		throw new ConversionException("not an int");
	}

	public static Object toJava(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof JsonObject) {
			return ((JsonObject) value).toJava();
		}
		if (value instanceof JsonArray) {
			return ((JsonArray) value).toJava();
		}
		return value;
	}

	public static long toLong(Object object) throws ConversionException {
		try {
			if (object == null) {
				// fall through
			} else if (object instanceof Number) {
				return ((Number) object).longValue();
			} else if (object instanceof String) {
				return Long.parseLong((String) object);
			}
		} catch (Exception e) {
			//
		}
		throw new ConversionException("not an int");
	}

	public static String toString(Object object) throws ConversionException {
		if (object == null) {
			return null;
		} else if (object instanceof String) {
			return (String) object;
		} else {
			return object.toString();
		}
	}

	public static String valueToString(Object value) throws IOException {
		if (value == null) {
			return "null";
		}
		if (value instanceof Number) {
			return numberToString((Number) value);
		}
		if (value instanceof Boolean || value instanceof JsonObject || value instanceof JsonArray) {
			return value.toString();
		}
		return quote(value.toString());
	}

	public static Object wrap(Object object) {
		try {
			if (object == null) {
				return null;
			}
			if (object instanceof JsonObject || object instanceof JsonArray || object instanceof Number
					|| object instanceof Boolean || object instanceof String) {
				return object;
			}
			if (object instanceof Collection) {
				return createArray((Collection) object);
			}
			if (object instanceof Map) {
				return createObject((Map) object);
			}
			return object.toString();
		} catch (Exception exception) {
			return null;
		}
	}

	private Json() {
	}

}
