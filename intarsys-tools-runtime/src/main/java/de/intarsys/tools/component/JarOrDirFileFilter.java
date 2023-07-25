package de.intarsys.tools.component;

import java.io.File;
import java.io.FileFilter;

public class JarOrDirFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			return false;
		}

		if (pathname.isDirectory()) {
			return true;
		}

		return pathname.getName().endsWith(".jar"); //$NON-NLS-1$
	}
}
