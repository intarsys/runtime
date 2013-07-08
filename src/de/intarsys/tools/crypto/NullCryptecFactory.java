package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;

public class NullCryptecFactory extends AbstractCryptdecFactory {

	public NullCryptecFactory(String id) {
		super(id);
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		return new NullCryptdec(getId());
	}
}
