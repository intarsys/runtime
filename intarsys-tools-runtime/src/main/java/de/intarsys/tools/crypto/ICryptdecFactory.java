package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;

/**
 * Create an {@link ICryptdec} for a concrete crypto operation.
 * 
 */
public interface ICryptdecFactory {

	/**
	 * Create a new {@link ICryptdec}.
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public ICryptdec createCryptdec() throws GeneralSecurityException;

	public String getId();

}
