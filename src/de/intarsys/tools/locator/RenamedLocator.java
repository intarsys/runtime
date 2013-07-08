package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.file.FileTools;

/**
 * A name swapping decoration.
 * 
 */
public class RenamedLocator extends ImmutableDelegatingLocator {

	private String name;

	public RenamedLocator(ILocator delegate) {
		super(delegate);
		this.name = delegate.getFullName();
	}

	public RenamedLocator(ILocator delegate, String fullName) {
		super(delegate);
		this.name = fullName;
	}

	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public String getLocalName() {
		return FileTools.getBaseName(name);
	}

	@Override
	public String getTypedName() {
		return FileTools.getFileName(name);
	}

	@Override
	public void rename(String newName) throws IOException {
		this.name = newName;
	}

}
