package de.intarsys.tools.message;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.reflect.ClassLoaderTools;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IMessageClassLoaderProvider} that maps an external directory as a {@link ClassLoader}.
 * 
 */
public class DirectoryMessageClassLoaderProvider implements IMessageClassLoaderProvider {

	private String path;

	private boolean useParent = false;

	private ClassLoader classLoader = null;

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getPath() {
		return path;
	}

	public boolean isUseParent() {
		return useParent;
	}

	public void setPath(String value) {
		this.path = PathTools.withTrailingSeparator(value);
		if (!StringTools.isEmpty(getPath())) {
			URL[] urls = ClassLoaderTools.toURLs(new File(getPath()));
			ClassLoader parent = isUseParent() ? Thread.currentThread().getContextClassLoader() : null;
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
			classLoader = cl;
		} else {
			classLoader = null;
		}
	}

	public void setUseParent(boolean useParent) {
		this.useParent = useParent;
	}
}
