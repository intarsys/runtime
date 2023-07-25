package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.file.PathTools;

/**
 * A name swapping decoration.
 * 
 */
public class RenamedLocator extends ImmutableDelegatingLocator {

	private String name;

	public RenamedLocator(ILocator delegate) {
		super(delegate);
		this.name = delegate.getPath();
	}

	public RenamedLocator(ILocator delegate, String fullName) {
		super(delegate);
		this.name = fullName;
	}

	@Override
	public String getName() {
		return PathTools.getName(name);
	}

	@Override
	public String getPath() {
		return name;
	}

	@Override
	public void rename(String newName) throws IOException {
		this.name = newName;
	}

}
