package de.intarsys.tools.locator;

public abstract class AbstractLocator implements ILocator {

	private boolean readOnly;

	/**
	 * @deprecated use getPath();
	 */
	@Deprecated
	public String getFullName() {
		return getPath();
	}

	/**
	 * @deprecated use LocatorTools.getBaseName()
	 */
	@Deprecated
	public String getLocalName() {
		return LocatorTools.getBaseName(this);
	}

	/**
	 * @deprecated use LocatorTools.getExtension()
	 */
	@Deprecated
	public String getType() {
		return LocatorTools.getExtension(this);
	}

	/**
	 * @deprecated use getName();
	 */
	@Deprecated
	public String getTypedName() {
		return getName();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly() {
		setReadOnly(true);
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
