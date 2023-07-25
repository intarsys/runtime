package de.intarsys.tools.xfs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.intarsys.tools.string.StringTools;

public class XFSScanner {

	public interface FoundCallback {
		public void onFound(IXFSNode node) throws IOException;
	}

	private String rootPath = "";

	private ClassLoader classLoader;

	private String pattern;

	public XFSScanner() {
		setClassLoader(getClass().getClassLoader());
	}

	public XFSScanner(ClassLoader classLoader, String rootPath, String pattern) {
		super();
		this.classLoader = classLoader;
		this.rootPath = rootPath;
		this.pattern = pattern;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getPattern() {
		return pattern;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void scan(FoundCallback callback) throws IOException {
		IXFSNode node = new XFSClassLoaderNode(getClassLoader());
		IXFSNode rootNode;
		if (StringTools.isEmpty(getRootPath())) {
			rootNode = node;
		} else {
			rootNode = node.getChild(getRootPath());
		}
		if (getPattern() != null) {
			Pattern tmpPattern = Pattern.compile(getPattern());
			Matcher matcher = tmpPattern.matcher("");
			scan(callback, rootNode, matcher);
		} else {
			scan(callback, rootNode, null);
		}
	}

	protected void scan(FoundCallback callback, IXFSNode rootNode, Matcher matcher) throws IOException {
		if (rootNode == null) {
			return;
		}
		for (IXFSNode child : rootNode.getChildren()) {
			if (matcher == null) {
				callback.onFound(child);
			} else {
				String tmpName = child.getPath() + child.getName();
				matcher.reset(tmpName);
				if (matcher.matches()) {
					callback.onFound(child);
				} else if (matcher.hitEnd()) {
					scan(callback, child, matcher);
				}
			}
		}
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setRootPath(String root) {
		this.rootPath = root;
	}

}
