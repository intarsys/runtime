package de.intarsys.tools.message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import de.intarsys.tools.yalf.api.ILogger;

public class CombinedResourceBundleControl extends ResourceBundle.Control {

	private static ILogger Log = PACKAGE.Log;

	private Properties load(String resourceName, ClassLoader loader) throws IOException {
		Properties result = new Properties();
		Enumeration<URL> urls = loader.getResources(resourceName);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			Log.debug("CombinedResourceBundleControl loading {} from {}", resourceName, url);
			Properties properties = new Properties();
			try (InputStream is = url.openStream()) {
				properties.load(is);
			}
			result.putAll(properties);
		}
		return result;
	}

	@Override
	public ResourceBundle newBundle(
			String baseName,
			Locale locale,
			String format,
			ClassLoader loader,
			boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if (!"java.properties".equals(format)) {
			return super.newBundle(baseName, locale, format, loader, reload);
		}
		Log.debug("CombinedResourceBundleControl loading {} for locale {}", baseName, locale);
		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, "properties");
		Properties properties = load(resourceName, loader);
		return properties.size() == 0 ? null : new CombinedResourceBundle(properties);
	}
}