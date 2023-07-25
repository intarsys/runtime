package de.intarsys.tools.tlv.common;

import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.string.StringTools;

public abstract class TlvBasedObject {

	private static final String NEWLINE = "\n";

	/**
	 * Generic "toString" of value.
	 * 
	 * The string representation is appended to sb, ensuring an indent level.
	 * 
	 * @param sb
	 * @param level
	 * @param value
	 */
	public static void toStringValue(StringBuilder sb, int level, Object value) {
		try {
			if (value instanceof byte[]) {
				sb.append(HexTools.bytesToHexString((byte[]) value));
			} else if (value instanceof TlvBasedObject) {
				((TlvBasedObject) value).toString(sb, level);
			} else {
				sb.append(StringTools.safeString(value));
			}
		} catch (Exception e) {
			sb.append("<error");
		}
	}

	protected String getTagName() {
		return ClassTools.getUnqualifiedName(getClass());
	}

	@Override
	public final String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			toString(sb, 0);
			return sb.toString();
		} catch (Exception e) {
			return "<error>";
		}
	}

	public final void toString(StringBuilder sb, int level) {
		toStringContent(sb, level);
	}

	protected void toStringClose(StringBuilder sb, int level) {
		toStringIndent(sb, level);
		sb.append("}");
	}

	protected void toStringContent(StringBuilder sb, int level) {
		int reset = sb.length();
		int myLevel = level;
		toStringOpen(sb, myLevel++);
		int temp = sb.length();
		toStringMembers(sb, myLevel);
		if (temp == sb.length()) {
			sb.setLength(reset);
			toStringPrimitive(sb, --myLevel);
		} else {
			toStringClose(sb, --myLevel);
		}
	}

	protected void toStringIndent(StringBuilder sb, int level) {
		for (int i = 0; i < level; i++) {
			sb.append("   ");
		}
	}

	protected void toStringMember(StringBuilder sb, int level, String name, Object value, String valueLabel) {
		toStringIndent(sb, level);
		sb.append(name);
		sb.append(" = ");
		if (valueLabel != null) {
			sb.append("<");
			sb.append(valueLabel);
			sb.append("> ");
		}
		toStringValue(sb, level, value);
		sb.append(NEWLINE);
	}

	protected void toStringMembers(StringBuilder sb, int level) {
	}

	/**
	 * @param sb
	 *            The output buffer
	 * @param level
	 *            The indentation
	 */
	protected void toStringOpen(StringBuilder sb, int level) {
		sb.append("{");
		sb.append(NEWLINE);
	}

	protected abstract void toStringPrimitive(StringBuilder sb, int i);

}
