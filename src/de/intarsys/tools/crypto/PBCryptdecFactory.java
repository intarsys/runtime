package de.intarsys.tools.crypto;

/**
 * An abstract superclass for implementing password based {@link ICryptdec}.
 * 
 */
abstract public class PBCryptdecFactory extends AbstractCryptdecFactory {

	public PBCryptdecFactory(String id) {
		super(id);
	}

}
