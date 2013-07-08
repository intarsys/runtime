package de.intarsys.tools.crypto;

/**
 * A common superclass for implementing {@link ICryptdecFactory}.
 * 
 */
abstract public class AbstractCryptdecFactory implements ICryptdecFactory {

	final private String id;

	public AbstractCryptdecFactory(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
