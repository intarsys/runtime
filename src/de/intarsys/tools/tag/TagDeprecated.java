package de.intarsys.tools.tag;

/**
 * Indicate that a component or feature should no longer be used.
 * 
 */
public class TagDeprecated {

	/**
	 * "true" if not empty
	 */
	public static String TAG_DEPRECATED = "de.intarsys.deprecated";

	public static boolean isDeprecated(Object target) {
		return TagTools.getTag(target, TAG_DEPRECATED) != null;
	}

}
