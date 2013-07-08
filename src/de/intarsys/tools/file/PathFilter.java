package de.intarsys.tools.file;

import java.util.ArrayList;
import java.util.List;

public class PathFilter implements IPathFilter {

	private List<String> includes = new ArrayList<String>();

	private List<String> excludes = new ArrayList<String>();

	private static final WildcardMatch matcher = new WildcardMatch();

	public PathFilter() {
		super();
	}

	@Override
	public boolean accept(String path) {
		return includes(path) && !excludes(path);
	}

	public void addExclude(String name) {
		excludes.add(name);
	}

	public void addInclude(String name) {
		includes.add(name);
	}

	protected boolean excludes(String path) {
		if (excludes.isEmpty()) {
			return false;
		}
		for (String exclude : getExcludes()) {
			if (match(exclude, path)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getExcludes() {
		return new ArrayList<String>(excludes);
	}

	public List<String> getIncludes() {
		return new ArrayList<String>(includes);
	}

	protected boolean includes(String path) {
		if (includes.isEmpty()) {
			return true;
		}
		for (String include : getIncludes()) {
			if (match(include, path)) {
				return true;
			}
		}
		return false;
	}

	protected boolean match(String pattern, String path) {
		return matcher.match(pattern, path);
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

}
