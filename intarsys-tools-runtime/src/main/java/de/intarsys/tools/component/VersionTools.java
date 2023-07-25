package de.intarsys.tools.component;

import de.intarsys.tools.string.StringTools;

/**
 * Some simple string handling routines handling version strings.
 * 
 */
public final class VersionTools {

	public static String getMajor(String version) {
		if (StringTools.isEmpty(version)) {
			return "";
		}
		int pos = version.indexOf('.');
		if (pos > 0) {
			return version.substring(0, pos).trim();
		}
		return version.trim();
	}

	public static String getMicro(String version) {
		if (StringTools.isEmpty(version)) {
			return "";
		}
		int pos = version.indexOf('.');
		if (pos >= 0) {
			version = version.substring(pos + 1);
		} else {
			return "";
		}
		pos = version.indexOf('.');
		if (pos >= 0) {
			version = version.substring(pos + 1);
		} else {
			return "";
		}
		pos = version.indexOf('.');
		if (pos > 0) {
			return version.substring(0, pos).trim();
		}
		return version.trim();
	}

	public static String getMinor(String version) {
		if (StringTools.isEmpty(version)) {
			return "";
		}
		int pos = version.indexOf('.');
		if (pos >= 0) {
			version = version.substring(pos + 1);
		} else {
			return "";
		}
		pos = version.indexOf('.');
		if (pos > 0) {
			return version.substring(0, pos).trim();
		}
		return version.trim();
	}

	private VersionTools() {
	}

}
