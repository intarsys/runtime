package de.intarsys.tools.tag;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.string.StringTools;

/**
 * A simple key/value pair that can be attached to POJO's.
 * <p>
 * Tags can be created using strings of the format <br>
 * <code>
 * key[=value]?[;key[=value]?]*
 * </code> or <code>
 * key[:value]?[;key[:value]?]*
 * </code>
 * 
 */
public class Tag {

	private final String key;

	private final String value;

	private Map<String, Object> properties;

	public Tag(String key) {
		super();
		this.key = key;
		this.value = "";
	}

	public Tag(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			return equalsFromTag((Tag) obj);
		}
		return false;
	}

	protected boolean equalsFromTag(Tag obj) {
		return isEqual(obj.value, this.value) && isEqual(obj.key, this.key);
	}

	public String getKey() {
		return key;
	}

	public Object getProperty(String name) {
		if (properties == null) {
			return null;
		}
		return properties.get(name);
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}

	protected boolean isEqual(String s1, String s2) {
		if (s1 == null) {
			return s2 == null;
		}
		return s1.equals(s2);
	}

	public void setProperty(String name, Object value) {
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(name, value);
	}

	@Override
	public String toString() {
		if (StringTools.isEmpty(value)) {
			return key;
		} else {
			return key + "=" + value;
		}
	}

}
