package de.intarsys.tools.xfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;

public class XFSClassLoaderNode implements IXFSNode {

	private final ClassLoader classLoader;

	private final String path;

	private final String metaPath;

	private final String name;

	private final String container;

	private List<IXFSNode> children;

	public XFSClassLoaderNode(ClassLoader classLoader) {
		this(classLoader, null, "", "META-INF/x-fs/ROOT", "");
	}

	protected XFSClassLoaderNode(ClassLoader classLoader, String container, String path, String metaPath, String name) {
		super();
		this.classLoader = classLoader;
		this.container = container;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		this.path = path;
		this.metaPath = metaPath;
		this.name = name;
	}

	protected XFSClassLoaderNode basicCreateChildNode(String container, String childName) {
		String pathName = getPath() + getName() + "/";
		String metaPathName = getMetaPath() + getName() + "/children/";
		XFSClassLoaderNode childNode = new XFSClassLoaderNode(getClassLoader(), container, pathName, metaPathName,
				childName);
		return childNode;
	}

	@Override
	public int compareTo(IXFSNode o) {
		return getName().compareTo(o.getName());
	}

	protected XFSClassLoaderNode createChild(String container, String childName) {
		if (StringTools.isEmpty(childName)) {
			return null;
		}
		String[] segments = childName.split("[\\\\/]");
		XFSClassLoaderNode childNode = this;
		for (String segment : segments) {
			childNode = childNode.basicCreateChildNode(container, segment);
		}
		return childNode;
	}

	protected List<IXFSNode> createChildren() throws IOException {
		List<String> containerNames = new ArrayList<>();
		List<IXFSNode> result = new ArrayList<>();
		String dirname = getMetaPath() + getName() + "/directory.txt";
		Enumeration<URL> urls = getClassLoader().getResources(dirname);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			String containerName = url.toString();
			int index = containerName.indexOf(dirname);
			if (index >= 0) {
				containerName = containerName.substring(0, index);
			}
			/* classpath might have more than one entry for same jar -> filter */
			if (!containerNames.contains(containerName)) {
				containerNames.add(containerName);
				InputStream is = url.openStream();
				if (is != null) {
					String directory = StreamTools.getString(is, StandardCharsets.UTF_8);
					String[] childNames = directory.split("\\n");
					for (String childName : childNames) {
						XFSClassLoaderNode childNode = createChild(containerName, childName.trim());
						if (childNode != null) {
							result.add(childNode);
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public boolean exists() {
		String filename = getPath() + getName();
		InputStream is = getClassLoader().getResourceAsStream(filename);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				//
			}
			return true;
		}
		return false;
	}

	@Override
	public XFSClassLoaderNode getChild(String childName) {
		return createChild(null, childName);
	}

	@Override
	public List<IXFSNode> getChildren() throws IOException {
		synchronized (this) {
			if (children == null) {
				children = createChildren();
			}
			return children;
		}
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getContainer() {
		return container;
	}

	public String getMetaPath() {
		return metaPath;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "" + getContainer() + getPath() + getName();
	}
}
