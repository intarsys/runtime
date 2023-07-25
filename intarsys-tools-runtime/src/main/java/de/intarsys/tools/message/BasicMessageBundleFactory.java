package de.intarsys.tools.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import de.intarsys.tools.reflect.ClassLoaderTools;
import de.intarsys.tools.string.StringTools;

/**
 * The most simple implementation for {@link IMessageBundleFactory}, finally relying on the plain Java
 * {@link ResourceBundle} and acting on a single {@link Locale}.
 * 
 * This implementation allows to override the {@link ClassLoader} to allow injecting a dedicated NLS classpath.
 * 
 */
public class BasicMessageBundleFactory extends CommonMessageBundleFactory {

	public static ClassLoader createClassLoader(ClassLoader parent, String path) throws IOException {
		if (StringTools.isEmpty(path)) {
			return null;
		}
		File file = new File(path);
		if (file.exists()) {
			URL[] urls = ClassLoaderTools.toURLs(file);
			ClassLoader cl = new URLClassLoader(urls, parent) {
				@Override
				public URL getResource(String name) {
					URL url;
					url = findResource(name);
					if (url == null) {
						if (getParent() != null) {
							url = getParent().getResource(name);
						}
					}
					return url;
				}
			};
			return cl;
		}
		return null;
	}

	private Locale locale;

	private ClassLoader classloader;

	@Override
	protected CommonMessageBundle createMessageBundle(String name, ClassLoader pClassloader) {
		ClassLoader activeClassLoader = (getClassloader() == null) ? pClassloader : getClassloader();
		Locale activeLocale = getLocale() == null ? Locale.getDefault() : getLocale();
		return new BasicMessageBundle(this, name, activeLocale, activeClassLoader);
	}

	public ClassLoader getClassloader() {
		return classloader;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setClassloader(ClassLoader classloader) {
		this.classloader = classloader;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
