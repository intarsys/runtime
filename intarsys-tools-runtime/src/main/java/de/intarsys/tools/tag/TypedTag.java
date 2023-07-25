package de.intarsys.tools.tag;

import java.io.IOException;
import java.util.List;

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
 * @deprecated
 */
@Deprecated
public class TypedTag extends Tag {

	public static TypedTag[] parse(String tagString) throws IOException {
		List<Tag> simpleTags = TagTools.parseTags(tagString);
		TypedTag[] tags = new TypedTag[simpleTags.size()];
		int i = 0;
		for (Tag simpleTag : simpleTags) {
			String[] keysplit = simpleTag.getKey().split("\\#"); //$NON-NLS-1$
			if (keysplit.length > 1) {
				tags[i] = new TypedTag(keysplit[0], keysplit[1], simpleTag.getValue());
			} else {
				tags[i] = new TypedTag(simpleTag.getKey(), simpleTag.getValue());
			}
			i++;
		}
		return tags;
	}

	private final String type;

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
		return super.equals(obj);
	}

	@Override
	protected boolean equalsFromTag(Tag obj) {
		if (obj instanceof TypedTag) {
			TypedTag in = (TypedTag) obj;
			return in.type.equals(this.type) && in.getValue().equals(this.getValue())
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
