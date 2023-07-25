package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;

public class NullCryptdecFactory extends AbstractCryptdecFactory {

	public NullCryptdecFactory(String id) {
		super(id);
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		return new NullCryptdec(getId());
	}
}
