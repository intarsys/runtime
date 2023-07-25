package de.intarsys.tools.json;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.intarsys.tools.converter.ConversionException;

/**
 * After a painful search for alternatives, this is the low level implementation
 * of the JSON spec from ourselves. All others failed in terms of lightweight
 * implementation or usability.
 * 
 * Our apologize for yet another implementation.
 */
public class JsonObject {

	private final Map<String, Object> map;

	protected JsonObject() {
		this.map = new HashMap<>();
	}

	protected JsonObject basicPut(String key, Object value) {
		map.put(key, value);
		return this;
	}

	public void clear() {
		map.clear();
	}

	public Object get(String key) {
		return map.get(key);
	}

	public boolean getBoolean(String key) throws ConversionException {
		Object object = this.get(key);
		return Json.toBoolean(object);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		try {
			return this.getBoolean(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(String key) throws ConversionException {
		Object object = this.get(key);
		return Json.toDouble(object);
	}

	public double getDouble(String key, double defaultValue) {
		try {
			return this.getDouble(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public int getInt(String key) throws ConversionException {
		Object object = this.get(key);
		return Json.toInt(object);
	}

	public int getInt(String key, int defaultValue) {
		try {
			return this.getInt(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public JsonArray getJSONArray(String key) throws ConversionException {
		Object object = this.get(key);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		}
		throw new ConversionException("not a JSONArray.");
	}

	public JsonArray getJSONArray(String key, JsonArray defaultValue) {
		Object o = get(key);
		return o instanceof JsonArray ? (JsonArray) o : defaultValue;
	}

	public JsonObject getJSONObject(String key) throws ConversionException {
		Object object = this.get(key);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		}
		throw new ConversionException("not a JSONObject.");
	}

	public JsonObject getJSONObject(String key, JsonObject defaultValue) {
		Object object = this.map.get(key);
		return object instanceof JsonObject ? (JsonObject) object : defaultValue;
	}

	public long getLong(String key) throws ConversionException {
		Object object = this.get(key);
		return Json.toLong(object);

	}

	public long getLong(String key, long defaultValue) {
		try {
			return this.getLong(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getString(String key) throws ConversionException {
		Object object = this.get(key);
		return Json.toString(object);
	}

	public String getString(String key, String defaultValue) {
		try {
			return getString(key);
		} catch (ConversionException e) {
			return defaultValue;
		}
	}

	public boolean has(String key) {
		return this.map.containsKey(key);
	}

	public Set<String> names() {
		return map.keySet();
	}

	public JsonObject put(String key, boolean value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, double value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, int value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, JsonArray value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, JsonObject value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, long value) {
		return basicPut(key, value);
	}

	public JsonObject put(String key, String value) {
		return basicPut(key, value);
	}

	public JsonObject remove(String key) {
		map.remove(key);
		return this;
	}

	public int size() {
		return this.map.size();
	}

	public Map toJava() {
		Map result = new HashMap<>();
		for (Map.Entry entry : map.entrySet()) {
			result.put(entry.getKey(), Json.toJava(entry.getValue()));
		}
		return result;
	}

	@Override
	public String toString() {
		try {
			return this.toString(0);
		} catch (Exception e) {
			return "<unprintable>";
		}
	}

	public String toString(int indentFactor) {
		StringWriter w = new StringWriter();
		try {
			new JsonWriter(w).write(this, indentFactor, 0);
			return w.toString();
		} catch (Exception e) {
			return "<unprintable>";
		}
	}

}
