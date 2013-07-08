package de.intarsys.tools.locator;

import java.io.IOException;
import java.io.InputStream;

/**
 * Locate resources contained within a zip file.
 * <p>
 * Full syntax <br>
 * <code>
 * <zip locator>#<path>
 * </code> <br>
 * 
 * The zip locator itself may be any locator that can be resolved to an
 * {@link InputStream}.
 */
public class ZipFileLocatorFactory extends CommonLocatorFactory {

	@Override
	protected ILocator basicCreateLocator(String location)
			throws IOException {
		int pos = location.lastIndexOf('#');
		String zipLocation;
		String path;
		if (pos >= 0) {
			zipLocation = location.substring(0, pos);
			path = location.substring(pos + 1);
		} else {
			zipLocation = location;
			path = "";
		}
		ILocator zipLocator = LocatorFactory.get().createLocator(zipLocation);
		return new ZipFileLocator(zipLocator, path);
	}

}
