package de.intarsys.tools.tag;

import de.intarsys.tools.lang.LangTools;

/**
 * Indicate the visibility of a component or feature for the user.
 * 
 */
public class TagVisible {

	/**
	 * One of never | lab | expert | basic
	 */
	public static final String TAG_VISIBLE = "de.intarsys.visible";

	public static final String TAGV_VISIBLE_BASIC = "basic";
	public static final String TAGV_VISIBLE_EXPERT = "expert";
	public static final String TAGV_VISIBLE_LAB = "lab";
	public static final String TAGV_VISIBLE_NEVER = "never";

	public static String getVisible(Object target) {
		return TagTools.getTagValue(target, TAG_VISIBLE, null);
	}

	public static boolean isVisibleAt(Object target, String level) {
		String value = TagTools.getTagValue(target, TAG_VISIBLE, null);
		if (value == null || TAGV_VISIBLE_BASIC.equals(value)) {
			return true;
		}
		if (TAGV_VISIBLE_EXPERT.equals(value)) {
			return TAGV_VISIBLE_EXPERT.equals(level)
					|| TAGV_VISIBLE_LAB.equals(level)
					|| TAGV_VISIBLE_NEVER.equals(level);
		}
		if (TAGV_VISIBLE_LAB.equals(value)) {
			return TAGV_VISIBLE_LAB.equals(level)
					|| TAGV_VISIBLE_NEVER.equals(level);
		}
		if (TAGV_VISIBLE_NEVER.equals(value)) {
			return TAGV_VISIBLE_NEVER.equals(level);
		}
		return false;
	}

	public static boolean isVisibleEqual(Object target, String level) {
		String value = TagTools.getTagValue(target, TAG_VISIBLE, null);
		return LangTools.equals(value, level);
	}

}
