package de.intarsys.tools.json;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.converter.ConversionException;

/**
 * After a painful search for alternatives, this is the low level implementation
 * of the JSON spec from ourselves. All others failed in terms of lightweight
 * implementation or usability.
 * 
 * Our apologize for yet another implementation.
 */
public class JsonArray {

	private final List<Object> list;

	protected JsonArray() {
		this.list = new ArrayList();
	}

	public JsonArray add(boolean value) {
		return basicAdd(value);
	}

	public JsonArray add(double value) {
		return basicAdd(value);
	}

	public JsonArray add(int value) {
		return basicAdd(value);
	}

	public JsonArray add(JsonArray value) {
		return basicAdd(value);
	}

	public JsonArray add(JsonObject value) {
		return basicAdd(value);
	}

	public JsonArray add(long value) {
		return basicAdd(value);
	}

	public JsonArray add(String value) {
		return basicAdd(value);
	}

	protected JsonArray basicAdd(Object value) {
		this.list.add(value);
		return this;
	}

	public void clear() {
		list.clear();
	}

	public Object get(int index) {
		return (index < 0 || index >= this.size()) ? null : list.get(index);
	}

	public boolean getBoolean(int index) throws ConversionException {
		Object object = this.get(index);
		return Json.toBoolean(object);
	}

	public boolean getBoolean(int index, boolean defaultValue) {
		try {
			return this.getBoolean(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(int index) throws ConversionException {
		Object object = this.get(index);
		return Json.toDouble(object);
	}

	public double getDouble(int index, double defaultValue) {
		try {
			return this.getDouble(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public int getInt(int index) throws ConversionException {
		Object object = this.get(index);
		return Json.toInt(object);
	}

	public int getInt(int index, int defaultValue) {
		try {
			return this.getInt(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public JsonArray getJSONArray(int index) throws ConversionException {
		Object object = this.get(index);
		if (object instanceof JsonArray) {
			return (JsonArray) object;
		}
		throw new ConversionException("not a JSONArray.");
	}

	public JsonArray getJSONArray(int index, JsonArray defaultValue) {
		Object o = get(index);
		return o instanceof JsonArray ? (JsonArray) o : defaultValue;
	}

	public JsonObject getJSONObject(int index) throws ConversionException {
		Object object = this.get(index);
		if (object instanceof JsonObject) {
			return (JsonObject) object;
		}
		throw new ConversionException("not a JSONObject.");
	}

	public JsonObject getJSONObject(int index, JsonObject defaultValue) {
		Object o = get(index);
		return o instanceof JsonObject ? (JsonObject) o : defaultValue;
	}

	public long getLong(int index) throws ConversionException {
		Object object = this.get(index);
		return Json.toLong(object);
	}

	public long getLong(int index, long defaultValue) {
		try {
			return this.getLong(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getString(int index) throws ConversionException {
		Object object = this.get(index);
		return Json.toString(object);
	}

	public String getString(int index, String defaultValue) {
		try {
			return getString(index);
		} catch (ConversionException e) {
			return defaultValue;
		}
	}

	public Iterator<Object> iterator() {
		return list.iterator();
	}

	public JsonArray remove(int index) {
		this.list.remove(index);
		return this;
	}

	public int size() {
		return this.list.size();
	}

	public List toJava() {
		List result = new ArrayList<>();
		for (Object value : list) {
			result.add(Json.toJava(value));
		}
		return result;
	}

	@Override
	public String toString() {
		return this.toString(0);
	}

	public String toString(int indentFactor) {
		StringWriter w = new StringWriter();
		try {
			new JsonWriter(w).write(this, indentFactor, 0);
			return w.toString();
		} catch (Exception e) {
			return "[ ]";
		}
	}

}
