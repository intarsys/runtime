/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.locator;

import java.io.IOException;
import java.util.function.Consumer;

import de.intarsys.tools.file.IPathFilter;
import de.intarsys.tools.string.StringTools;

/**
 * A utility class to simplify the task of visiting nodes in an {@link ILocator}
 * hierarchy.
 * 
 * The root hierarchy level is always enumerated. use
 * {@link #setRecursive(boolean)} to force recursive enumeration.
 * 
 * By default, file nodes are reported to the consumer, directories not. Use
 * {@link #setVisitDirectories(boolean)} and {@link #setVisitFiles(boolean)} to
 * change this behavior.
 * 
 */
public class LocatorWalker {

	/**
	 * Control Flow - break out of sibling enumeration
	 *
	 */
	public static class Break extends RuntimeException {
	}

	/**
	 * Control Flow - continue with next sibling (do not descend)
	 *
	 */
	public static class Continue extends RuntimeException {

	}

	public static class LocatorWalkerNode {
		public final String path;
		public final ILocator locator;

		public LocatorWalkerNode(ILocator locator, String path) {
			super();
			this.locator = locator;
			this.path = path;
		}
	}

	/**
	 * Control Flow - return from tree walk
	 *
	 */
	public static class Return extends RuntimeException {

	}

	public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	public static final IPathFilter ANY_FILTER = new IPathFilter() {
		@Override
		public boolean accept(String path) {
			return true;
		}
	};

	private IPathFilter filter;

	private boolean recursive = true;

	private boolean visitDirectories;

	private boolean visitFiles = true;

	private final ILocator root;

	public LocatorWalker(ILocator root) {
		if (root == null) {
			throw new IllegalArgumentException("root cannot be null");
		}
		this.root = root;
	}

	public void forEach(Consumer<LocatorWalkerNode> consumer) throws IOException {
		try {
			visit(getRoot(), StringTools.EMPTY, consumer, true);
		} catch (Return e) {
			//
		}
	}

	public IPathFilter getFilter() {
		return filter;
	}

	public ILocator getRoot() {
		return root;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public boolean isVisitDirectories() {
		return visitDirectories;
	}

	public boolean isVisitFiles() {
		return visitFiles;
	}

	public void setFilter(IPathFilter filter) {
		this.filter = filter;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public void setVisitDirectories(boolean visitDirectories) {
		this.visitDirectories = visitDirectories;
	}

	public void setVisitFiles(boolean visitFiles) {
		this.visitFiles = visitFiles;
	}

	protected void visit(ILocator locator, String path, Consumer<LocatorWalkerNode> consumer, boolean isSearchRoot)
			throws IOException {
		if (!isSearchRoot && getFilter() != null && !getFilter().accept(path)) {
			return;
		}
		if (locator.isDirectory()) {
			visitContainer(locator, path, consumer, isSearchRoot);
		} else {
			visitLeaf(locator, path, consumer);
		}
	}

	protected void visitContainer(ILocator locator, String path, Consumer<LocatorWalkerNode> consumer,
			boolean isSearchRoot) throws IOException {
		if (!isSearchRoot && isVisitDirectories()) {
			try {
				consumer.accept(new LocatorWalkerNode(locator, path));
			} catch (Continue e) {
				return;
			}
		}
		if (!isRecursive() && !isSearchRoot) {
			return;
		}
		ILocator[] children = locator.listLocators(null);
		for (int i = 0; i < children.length; i++) {
			ILocator child = children[i];
			String newPath = path;
			if (!StringTools.isEmpty(newPath)) {
				newPath += PATH_SEPARATOR;
			}
			newPath = newPath + child.getName();
			try {
				visit(child, newPath, consumer, false);
			} catch (Break e) {
				return;
			}
		}
	}

	protected void visitLeaf(ILocator locator, String path, Consumer<LocatorWalkerNode> consumer)
			throws IOException {
		if (isVisitFiles()) {
			consumer.accept(new LocatorWalkerNode(locator, path));
		}
	}

}
