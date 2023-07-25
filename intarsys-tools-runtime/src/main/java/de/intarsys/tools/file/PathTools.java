package de.intarsys.tools.file;

import de.intarsys.tools.string.StringTools;

public final class PathTools {

	public static final char SEPARATOR_ALT_CHAR = '\\';

	public static final String SEPARATOR_STRING = "/";

	public static final char SEPARATOR_CHAR = '/';

	/**
	 * Get the local name in the directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * @param path
	 * @return The local name in the directory without the extension.
	 */
	public static String getBaseName(String path) {
		return getBaseName(path, null, StringTools.EMPTY);
	}

	/**
	 * Get the local name in the directory without the extension.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu"
	 * </pre>
	 * 
	 * The extensionPrefix may be used to designate a static prefix that should
	 * be considered part of the extension, not the base name. This is useful
	 * for handling "2nd order" extensions like "document.txt.p7s".
	 * 
	 * The special case ".name" is not treated like an extension and returned as
	 * the basename.
	 * 
	 * @param path
	 * @param extensionPrefix
	 *            An optional static prefix that should be considered part of
	 *            the extension, not the basename.
	 * @param defaultName
	 *            returned if base name is null or a empty String
	 * @return The local name in the directory without the extension.
	 */
	public static String getBaseName(String path, String extensionPrefix, String defaultName) {
		if (StringTools.isEmpty(path)) {
			return defaultName;
		}
		// first strip path prefixes
		int pos1 = path.lastIndexOf(SEPARATOR_CHAR);
		int pos2 = path.lastIndexOf(SEPARATOR_ALT_CHAR);
		int pos = Math.max(pos1, pos2);
		if (pos >= 0) {
			path = path.substring(pos + 1);
		}
		// then work on local name
		int dotPos = path.lastIndexOf('.');
		if (dotPos >= 1) {
			path = path.substring(0, dotPos);
			if (extensionPrefix != null && path.endsWith("." + extensionPrefix)) {
				path = path.substring(0, path.length() - extensionPrefix.length() - 1);
			}
		}
		return path;
	}

	/**
	 * Get the extension of the last path segment. If no extension is present,
	 * the empty string is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param path
	 * 
	 * @return The extension of the last path segment.
	 */
	public static String getExtension(String path) {
		return getExtension(path, null, StringTools.EMPTY);
	}

	/**
	 * Get the extension of the last path segment. If no extension is present, the
	 * defaultName is returned.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "txt"
	 * </pre>
	 * 
	 * @param path
	 * @param extensionPrefix An extension may use a prefix (by some unique
	 *                        timestamp e.g.) like
	 *                        &lt;name&gt;.&lt;prefix&gt;.&lt;extension&gt;, where
	 *                        prefix is considered being part of the extension. If
	 *                        the path is .&lt;prefix&gt;. &lt;extension&gt; or
	 *                        &lt;prefix&gt;.&lt;extension&gt;, the prefix is
	 *                        considered being the segment name!
	 * @param defaultName     returned if the extension is empty or null
	 * 
	 * @return The extension of the last path segment. If no extension is present,
	 *         the empty string is returned.
	 */
	public static String getExtension(String path, String extensionPrefix, String defaultName) {
		if (StringTools.isEmpty(path)) {
			return defaultName;
		}
		// first strip path prefixes
		int pos1 = path.lastIndexOf(SEPARATOR_CHAR);
		int pos2 = path.lastIndexOf(SEPARATOR_ALT_CHAR);
		int pos = Math.max(pos1, pos2);
		if (pos >= 0) {
			path = path.substring(pos + 1);
		}
		// then work on local name
		int dotPos = path.lastIndexOf('.');
		if (dotPos >= 1) {
			String temp = path.substring(dotPos + 1);
			int extPos = path.lastIndexOf("." + extensionPrefix);
			if (extensionPrefix != null && extPos >= 1 && extPos == dotPos - extensionPrefix.length() - 1) {
				temp = extensionPrefix + "." + temp;
			}
			return temp;
		}
		return defaultName;
	}

	/**
	 * Get the last path segment (with extension).
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu.txt"
	 * </pre>
	 * 
	 * @param path
	 * 
	 * @return The last path segment (with extension)
	 */
	public static String getName(String path) {
		return getName(path, StringTools.EMPTY);
	}

	/**
	 * Get the last path segment (with extension).
	 * 
	 * Example
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "gnu.txt"
	 * </pre>
	 * 
	 * @param path
	 * @param defaultName
	 * 
	 * @return The last path segment (with extension)
	 */
	public static String getName(String path, String defaultName) {
		if (StringTools.isEmpty(path)) {
			return defaultName;
		}
		int pos1 = path.lastIndexOf(SEPARATOR_CHAR);
		int pos2 = path.lastIndexOf(SEPARATOR_ALT_CHAR);
		int pos = Math.max(pos1, pos2);
		if (pos >= 0) {
			path = path.substring(pos + 1);
		}
		return path;
	}

	/**
	 * Get the parent of the path.
	 * 
	 * The result never ends with a separator, except when it designates the
	 * root.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "gnu.txt" -> ""
	 * </pre>
	 * 
	 * <pre>
	 * "/gnu.txt" -> "/"
	 * </pre>
	 * 
	 * <pre>
	 * "a/gnu.txt" -> "a"
	 * </pre>
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "/a"
	 * </pre>
	 * 
	 * 
	 * @param path
	 * 
	 * @return The parent of the path
	 */
	public static String getParent(String path) {
		return getParent(path, StringTools.EMPTY);
	}

	/**
	 * Get the parent of the path.
	 * 
	 * The result never ends with a separator, except when it designates the
	 * root.
	 * 
	 * Example
	 * 
	 * <pre>
	 * "gnu.txt" -> ""
	 * </pre>
	 * 
	 * <pre>
	 * "/gnu.txt" -> "/"
	 * </pre>
	 * 
	 * <pre>
	 * "a/gnu.txt" -> "a"
	 * </pre>
	 * 
	 * <pre>
	 * "/a/gnu.txt" -> "/a"
	 * </pre>
	 * 
	 * @param parent
	 * @param defaultName
	 * 
	 * @return The parent of the path
	 */
	public static String getParent(String parent, String defaultName) {
		if (StringTools.isEmpty(parent)) {
			return defaultName;
		}
		int pos1 = parent.lastIndexOf(SEPARATOR_CHAR);
		int pos2 = parent.lastIndexOf(SEPARATOR_ALT_CHAR);
		int pos = Math.max(pos1, pos2);
		if (pos > 0) {
			return parent.substring(0, pos);
		} else if (pos == 0) {
			return SEPARATOR_STRING;
		} else {
			return defaultName;
		}
	}

	public static boolean hasLeadingSeparator(String path) {
		return path.startsWith("\\") || path.startsWith(SEPARATOR_STRING);
	}

	public static boolean hasTrailingSeparator(String path) {
		return (path.endsWith(SEPARATOR_STRING) || path.endsWith("\\"));
	}

	public static boolean isRoot(String path) {
		return path.length() == 1 && (path.endsWith(SEPARATOR_STRING) || path.endsWith("\\"));
	}

	/**
	 * Join all segments to a valid path.
	 * 
	 * @param segment
	 *            Array of path segments
	 * @return
	 */
	public static String join(String... segment) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		boolean sbTrailingSeparator = false;
		boolean leadingSeparator = false;
		boolean trailingSeparator = false;
		for (String string : segment) {
			if (string == null || string.length() == 0) {
				continue;
			}
			leadingSeparator = hasLeadingSeparator(string);
			trailingSeparator = hasTrailingSeparator(string);
			if (sbTrailingSeparator && leadingSeparator) {
				string = withoutLeadingSeparator(string);
			}
			if (!first && !sbTrailingSeparator && !leadingSeparator) {
				sb.append(SEPARATOR_STRING);
			}
			sb.append(string);
			first = false;
			sbTrailingSeparator = trailingSeparator;
		}
		return sb.toString();
	}

	/**
	 * Ensure the separator character is canonical
	 * 
	 * @param path
	 * @return
	 */
	public static String toCanonicalSeparator(String path) {
		return path.replace(SEPARATOR_ALT_CHAR, SEPARATOR_CHAR);
	}

	/**
	 * Ensure path has a leading path separator char.
	 * 
	 * @param path
	 * @return
	 */
	public static String withLeadingSeparator(String path) {
		if (StringTools.isEmpty(path)) {
			return SEPARATOR_STRING;
		}
		if (path.startsWith(SEPARATOR_STRING)) {
			return path;
		}
		return SEPARATOR_STRING + path;
	}

	/**
	 * Remove the last segment from the path
	 * 
	 * @param path
	 * @return
	 */
	public static String withoutLastSegment(String path) {
		if (StringTools.isEmpty(path)) {
			return "";
		}
		int index = path.lastIndexOf(SEPARATOR_STRING);
		if (index >= 0) {
			return path.substring(0, index);
		}
		return "";
	}

	/**
	 * Ensure path has no leading path separator char. Root "/" will be stripped
	 * of, too.
	 * 
	 * @param path
	 * @return
	 */
	public static String withoutLeadingSeparator(String path) {
		if (StringTools.isEmpty(path)) {
			return "";
		}
		while (path.startsWith(SEPARATOR_STRING)) {
			path = path.substring(1);
		}
		return path;
	}

	/**
	 * Ensure path has no trailing separator char.
	 * 
	 * @param path
	 * @return
	 */
	public static String withoutTrailingSeparator(String path) {
		if (StringTools.isEmpty(path)) {
			return "";
		}
		while (hasTrailingSeparator(path)) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * Ensure path has a trailing separator char.
	 * 
	 * @param path
	 * @return
	 */
	public static String withTrailingSeparator(String path) {
		if (StringTools.isEmpty(path)) {
			return "";
		}
		if (hasTrailingSeparator(path)) {
			return path;
		}
		if (isRoot(path)) {
			return path;
		}
		return path + SEPARATOR_STRING;
	}

	private PathTools() {
	}

}
