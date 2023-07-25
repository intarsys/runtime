/*
 * intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import de.intarsys.tools.file.PathTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 */
public class ComponentInfoCollector {

	private static final String SUFFIX_JAR = ".jar";

	private static final String PATH_SEPARATORS = ":;,";

	private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

	private static final ILogger Log = LogTools.getLogger(ComponentInfoCollector.class);

	private final List<File> paths = new ArrayList();

	private final List<ClassLoader> classLoaders = new ArrayList();

	private final ComponentInfo root;

	public ComponentInfoCollector() {
		this(new ComponentInfo());
	}

	public ComponentInfoCollector(ComponentInfo componentInfo) {
		super();
		this.root = componentInfo;
	}

	public void addClassLoader(ClassLoader loader) {
		classLoaders.add(loader);
	}

	public void addPath(File... files) {
		for (File file : files) {
			getPaths().add(file);
		}
	}

	public void addPath(String... paths) {
		for (String path : paths) {
			StringTokenizer tokenizer = new StringTokenizer(path, PATH_SEPARATORS, false);
			while (tokenizer.hasMoreTokens()) {
				File singlePath = new File(tokenizer.nextToken().trim());
				addPath(singlePath);
			}
		}
	}

	public void collect() throws IOException {
		Log.trace("component info loading...");
		for (Iterator<File> i = getJars().iterator(); i.hasNext();) {
			getComponentInfos(getRoot(), i.next());
		}
		for (Iterator<ClassLoader> i = getClassLoaders().iterator(); i.hasNext();) {
			getComponentInfos(getRoot(), i.next());
		}
		Log.trace("component info loaded");
	}

	protected ComponentInfo createComponentInfo(ComponentInfo parent, URL url, String name, Attributes attributes) {
		ComponentInfo info = new ComponentInfo();
		info.setName(name);
		info.setUrl(url);
		if (parent != null) {
			parent.addChild(info);
		}
		info.readFromManifestAttributes(attributes);
		return info;
	}

	protected void createComponentInfos(ComponentInfo parent, URL url, String name, Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();
		ComponentInfo tmpParent = createComponentInfo(parent, url, name, attributes);
		Map entries = manifest.getEntries();
		for (Iterator it = entries.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			attributes = (Attributes) entry.getValue();
			if (isValidChildComponent(attributes)) {
				createComponentInfo(tmpParent, url, (String) entry.getKey(), attributes);
			}
		}
		Log.trace("component info loaded from {}", url);
	}

	public List<ClassLoader> getClassLoaders() {
		return classLoaders;
	}

	protected void getComponentInfos(ComponentInfo parent, ClassLoader loader) throws IOException {
		Enumeration<URL> e = loader.getResources(MANIFEST_FILE);
		while (e.hasMoreElements()) {
			URL url = e.nextElement();
			getComponentInfos(parent, url);
		}
	}

	protected void getComponentInfos(ComponentInfo parent, File jarFile) {
		JarFile jar = null;
		try {
			jar = new JarFile(jarFile);
			Manifest manifest = jar.getManifest();
			if (manifest != null) {
				URL url = jarFile.toURI().toURL();
				String name = jarFile.getName();
				createComponentInfos(parent, url, name, manifest);
			}
		} catch (Exception ex) {
			Log.info("component info loading manifest from {} failed ({})", jarFile, ex.getMessage());
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					//
				}
			}
		}
	}

	protected void getComponentInfos(ComponentInfo parent, URL url) {
		InputStream is = null;
		try {
			is = url.openStream();
			Manifest manifest = new Manifest(is);
			URL containerUrl = ClassTools.getContainerResourceUrl(url);
			String name = PathTools.getName(containerUrl.toString());
			createComponentInfos(parent, containerUrl, name, manifest);
		} catch (Exception ex) {
			Log.info("component info loading manifest from {} failed ({})", url, ex.getMessage());
		} finally {
			StreamTools.close(is);
		}
	}

	protected List<File> getJars() {
		List jarList = new ArrayList();
		for (Iterator i = getPaths().iterator(); i.hasNext();) {
			File path = (File) i.next();
			jarList.addAll(getJars(path));
		}
		return jarList;
	}

	protected List<File> getJars(File path) {
		List jarList = new ArrayList();
		if (path.isFile() && path.getPath().endsWith(SUFFIX_JAR)) {
			jarList.add(path);
		} else {
			if (path.isDirectory()) {
				File[] files = path.listFiles(new JarOrDirFileFilter());
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						jarList.addAll(getJars(files[i]));
					}
				}
			}
		}
		return jarList;
	}

	public List getPaths() {
		return paths;
	}

	public ComponentInfo getRoot() {
		return root;
	}

	protected boolean isValidChildComponent(Attributes attributes) {
		return !StringTools.isEmpty(attributes.getValue(ComponentInfo.IMPLEMENTATION_VERSION));
	}

}
