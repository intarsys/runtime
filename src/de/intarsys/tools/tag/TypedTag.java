package de.intarsys.tools.tag;

import de.intarsys.tools.string.StringTools;

/**
 * A simple type/key/value pair that can be attached to POJO's.
 * <p>
 * Tags can be created using strings of the format <br>
 * <code>
 * [type#]key[=value]?[;[type#]key[=value]?]*
 * </code> or <code>
 * [type#]key[:value]?[;[type#]key[:value]?]*
 * </code>
 * 
 */
public class TypedTag extends Tag {

	public static TypedTag[] create(String tagString) {
		String[] tagStrings = tagString.split("\\;"); //$NON-NLS-1$
		TypedTag[] tags = new TypedTag[tagStrings.length];
		for (int i = 0; i < tagStrings.length; i++) {
			String[] split = tagStrings[i].split("[\\:\\=]", 2); //$NON-NLS-1$
			String[] keysplit = split[0].split("\\#"); //$NON-NLS-1$
			if (keysplit.length > 1) {
				if (split.length > 1) {
					tags[i] = new TypedTag(keysplit[0], keysplit[1], split[1]);
				} else {
					tags[i] = new TypedTag(keysplit[0], keysplit[1], ""); //$NON-NLS-1$
				}
			} else {
				if (split.length > 1) {
					tags[i] = new TypedTag(split[0], split[1]);
				} else {
					tags[i] = new TypedTag(split[0]);
				}
			}
		}
		return tags;
	}

	final private String type;

	public TypedTag(String key) {
		super(key);
		this.type = ""; //$NON-NLS-1$
	}

	public TypedTag(String key, String value) {
		super(key, value);
		this.type = ""; //$NON-NLS-1$
	}

	public TypedTag(String type, String key, String value) {
		super(key, value);
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypedTag) {
			TypedTag in = (TypedTag) obj;
			return in.type.equals(this.type)
					&& in.getValue().equals(this.getValue())
					&& in.getKey().equals(this.getKey());
		}
		return false;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return type.hashCode() + super.hashCode();
	}

	@Override
	public String toString() {
		if (StringTools.isEmpty(type)) {
			return super.toString();
		} else {
			if (StringTools.isEmpty(getValue())) {
				return type + '#' + getKey();
			} else {
				return type + '#' + getKey() + '=' + getValue();
			}
		}
	}

}
