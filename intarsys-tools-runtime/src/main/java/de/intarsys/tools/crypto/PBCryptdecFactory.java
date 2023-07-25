package de.intarsys.tools.crypto;

/**
 * An abstract superclass for implementing password based {@link ICryptdec}.
 * 
 */
public abstract class PBCryptdecFactory extends AbstractCryptdecFactory {

	protected PBCryptdecFactory(String id) {
		super(id);
	}

}
