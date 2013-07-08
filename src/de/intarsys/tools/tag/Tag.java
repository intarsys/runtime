package de.intarsys.tools.tag;

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

	public static Tag[] create(String tagString) {
		String[] tagStrings = tagString.split("\\;");
		Tag[] tags = new Tag[tagStrings.length];
		for (int i = 0; i < tagStrings.length; i++) {
			String[] split = tagStrings[i].split("[\\:\\=]", 2);
			if (split.length > 1) {
				tags[i] = new Tag(split[0], split[1]);
			} else {
				tags[i] = new Tag(split[0]);
			}
		}
		return tags;
	}

	final private String key;

	final private String value;

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
			Tag in = (Tag) obj;
			return in.value.equals(this.value) && in.key.equals(this.key);
		}
		return false;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
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
