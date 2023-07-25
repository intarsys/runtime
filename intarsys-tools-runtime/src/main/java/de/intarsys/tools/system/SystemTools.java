package de.intarsys.tools.system;

import de.intarsys.tools.string.StringTools;

public class SystemTools {

	private static final String BINDIR;
	private static final String BASEDIR;
	private static final String LIBDIR;

	private static final String OS_NAME;
	private static final String OS_ARCH;

	private static boolean Citrix;
	private static boolean Windows;
	private static boolean Windows_XP; // NNOSONAR
	private static boolean Linux;
	private static boolean Mac;

	static {
		String tempName = System.getProperty("de.intarsys.platform.os.name");
		String tempArch = System.getProperty("de.intarsys.platform.os.arch", "x86");
		if (tempName == null) {
			tempName = System.getProperty("os.name"); //$NON-NLS-1$
			tempArch = System.getProperty("os.arch"); //$NON-NLS-1$
		}
		OS_NAME = tempName;
		OS_ARCH = tempArch;
		String osNameLowerCase = OS_NAME.toLowerCase();

		// directory names are relative to some common root
		if (osNameLowerCase.startsWith("win")) { //$NON-NLS-1$
			Windows = true;
			if ("windows xp".equals(osNameLowerCase)) { //$NON-NLS-1$
				Windows_XP = true;
			}
			BINDIR = LIBDIR = "bin"; //$NON-NLS-1$
			BASEDIR = StringTools.EMPTY;
		} else if ("linux".startsWith(osNameLowerCase)) { //$NON-NLS-1$
			Linux = true;
			if ("amd64".equals(OS_ARCH)) {
				BINDIR = "bin"; //$NON-NLS-1$
				LIBDIR = "lib/amd64"; //$NON-NLS-1$
			} else {
				BINDIR = "bin"; //$NON-NLS-1$
				LIBDIR = "lib/i386"; //$NON-NLS-1$
			}
			BASEDIR = StringTools.EMPTY;
		} else if ("mac".startsWith(osNameLowerCase)) { //$NON-NLS-1$
			Mac = true;
			BINDIR = LIBDIR = "MacOS"; //$NON-NLS-1$
			BASEDIR = "Resources"; //$NON-NLS-1$
		} else {
			BINDIR = LIBDIR = BASEDIR = StringTools.EMPTY;
		}
		Citrix = Boolean.getBoolean("de.intarsys.platform.citrix");
	}

	public static final String DEFAULT_PROFILEDIR_MAC = "${properties.user.home}/Library/Application Support/${app.name:t}_${app.major}.${app.minor}"; //$NON-NLS-1$

	public static final String DEFAULT_PROFILEDIR_NIX = "${properties.user.home}/.${app.name:t:.toLowerCase}-${app.major}.${app.minor}"; //$NON-NLS-1$

	public static final String DEFAULT_PROFILEDIR_WINDOWS = "${properties.user.home}/.${app.name:t}_${app.major}.${app.minor}"; //$NON-NLS-1$

	public static final String DEFAULT_PROFILEDIR_MAC_WO_VERSION = "${properties.user.home}/Library/Application Support/${app.name:t}"; //$NON-NLS-1$

	public static final String DEFAULT_PROFILEDIR_NIX_WO_VERSION = "${properties.user.home}/.${app.name:t:.toLowerCase}"; //$NON-NLS-1$

	public static final String DEFAULT_PROFILEDIR_WINDOWS_WO_VERSION = "${properties.user.home}/.${app.name:t}"; //$NON-NLS-1$

	public static String getBasedir() {
		return BASEDIR;
	}

	public static String getBindir() {
		return BINDIR;
	}

	public static String getDebugSetting(String feature) {
		return System.getProperty("de.intarsys.debug." + feature);
	}

	public static String getLibdir() {
		return LIBDIR;
	}

	public static String getOSArch() {
		return OS_ARCH;
	}

	public static String getOSName() {
		return OS_NAME;
	}

	public static boolean isCitrix() {
		return Citrix;
	}

	public static boolean isDebug(String feature) {
		return "true".equals(System.getProperty("de.intarsys.debug." + feature));
	}

	public static boolean isLinux() {
		return Linux;
	}

	public static boolean isMac() {
		return Mac;
	}

	public static boolean isWindows() {
		return Windows;
	}

	public static boolean isWindowsXP() {
		return Windows_XP;
	}

	/**
	 * Maps a library name into a platform-specific string representing a native
	 * library. This one returns "dylib" as on Mac OS X instead of System's
	 * "jnilib".
	 * 
	 * @param libname
	 *            the name of the library.
	 * @return a platform-dependent native library name.
	 * @see java.lang.System#mapLibraryName(java.lang.String)
	 */
	public static String mapLibraryName(String libname) {
		String mapped = System.mapLibraryName(libname);
		if (mapped.length() > 6 && mapped.substring(mapped.length() - 7).equals(".jnilib")) {
			mapped = mapped.substring(0, mapped.length() - 7);
			mapped = mapped + ".dylib";
		}
		return mapped;
	}

	/**
	 * Maps a library name into a platform-specific string representing a native
	 * library. This one returns "dylib" as on Mac OS X instead of System's
	 * "jnilib" and appends a version suffix for "so".
	 * 
	 * @param libname
	 *            the name of the library.
	 * @param version
	 *            the version.
	 * @return a platform-dependent native library name.
	 * @see java.lang.System#mapLibraryName(java.lang.String)
	 */
	public static String mapLibraryName(String libname, String version) {
		String mapped = mapLibraryName(libname);
		if (mapped.length() > 2 && mapped.substring(mapped.length() - 3).equals(".so")) {
			mapped = mapped + "." + version;
		}
		return mapped;
	}

	private SystemTools() {
		// tools class
	}

}
