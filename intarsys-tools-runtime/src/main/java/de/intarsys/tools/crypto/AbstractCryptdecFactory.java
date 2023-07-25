package de.intarsys.tools.crypto;

/**
 * A common superclass for implementing {@link ICryptdecFactory}.
 * 
 */
public abstract class AbstractCryptdecFactory implements ICryptdecFactory {

	private final String id;

	protected AbstractCryptdecFactory(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return getId();
	}
}
