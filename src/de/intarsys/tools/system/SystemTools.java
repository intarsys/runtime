package de.intarsys.tools.system;

import de.intarsys.tools.string.StringTools;

public class SystemTools {

	private static final String Bindir;
	private static final String Basedir;
	private static final String Libdir;

	private final static String osName;
	private final static String osArch;

	private static boolean citrix = false;
	private static boolean windows = false;
	private static boolean windowsxp = false;
	private static boolean linux = false;
	private static boolean mac = false;

	static {
		String tempName = System.getProperty("de.intarsys.platform.os.name");
		String tempArch = System.getProperty("de.intarsys.platform.os.arch",
				"x86");
		if (tempName == null) {
			tempName = System.getProperty("os.name"); //$NON-NLS-1$
			tempArch = System.getProperty("os.arch"); //$NON-NLS-1$
		}
		osName = tempName;
		osArch = tempArch;
		String osNameLowerCase = osName.toLowerCase();

		// directory names are relative to some common root
		if (osNameLowerCase.startsWith("win")) { //$NON-NLS-1$
			windows = true;
			if (osNameLowerCase.equals("windows xp")) { //$NON-NLS-1$
				windowsxp = true;
			}
			Bindir = Libdir = "bin"; //$NON-NLS-1$
			Basedir = StringTools.EMPTY;
		} else if (osNameLowerCase.startsWith("linux")) { //$NON-NLS-1$
			linux = true;
			if ("amd64".equals(osArch)) {
				Bindir = "bin"; //$NON-NLS-1$
				Libdir = "lib/amd64"; //$NON-NLS-1$
			} else {
				Bindir = "bin"; //$NON-NLS-1$
				Libdir = "lib/i386"; //$NON-NLS-1$
			}
			Basedir = StringTools.EMPTY;
		} else if (osNameLowerCase.startsWith("mac")) { //$NON-NLS-1$
			mac = true;
			Bindir = Libdir = "MacOS"; //$NON-NLS-1$
			Basedir = "Resources"; //$NON-NLS-1$
		} else {
			Bindir = Libdir = Basedir = StringTools.EMPTY;
		}
		citrix = Boolean.getBoolean("de.intarsys.platform.citrix");
	}

	public static String getBasedir() {
		return Basedir;
	}

	public static String getBindir() {
		return Bindir;
	}

	public static String getLibdir() {
		return Libdir;
	}

	public static String getOSArch() {
		return osArch;
	}

	public static String getOSName() {
		return osName;
	}

	public static boolean isCitrix() {
		return citrix;
	}

	public static boolean isLinux() {
		return linux;
	}

	public static boolean isMac() {
		return mac;
	}

	public static boolean isWindows() {
		return windows;
	}

	public static boolean isWindowsXP() {
		return windowsxp;
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
		if (mapped.length() > 6
				&& mapped.substring(mapped.length() - 7).equals(".jnilib")) {
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
	 * @param libname
	 *            the version.
	 * @return a platform-dependent native library name.
	 * @see java.lang.System#mapLibraryName(java.lang.String)
	 */
	public static String mapLibraryName(String libname, int version) {
		String mapped = mapLibraryName(libname);
		if (mapped.length() > 2
				&& mapped.substring(mapped.length() - 3).equals(".so")) {
			mapped = mapped + "." + version;
		}
		return mapped;
	}

	private SystemTools() {
		// tools class
	}

}
