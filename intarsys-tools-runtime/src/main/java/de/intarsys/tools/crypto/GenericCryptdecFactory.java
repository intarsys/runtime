package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

/**
 * Generic factory for {@link ICryptdec} instances.
 * 
 */
public class GenericCryptdecFactory extends AbstractCryptdecFactory {

	private final SecretKey key;

	private String algorithmTransformation = "AES/CBC/PKCS5Padding";

	public GenericCryptdecFactory(String id, SecretKey key)
			throws GeneralSecurityException {
		super(id);
		this.key = key;
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		return new DynamicCryptdec(getId(), getKey(), getAlgorithmTransformation());
	}

	public String getAlgorithmTransformation() {
		return algorithmTransformation;
	}

	private SecretKey getKey() {
		return key;
	}

	public void setAlgorithmTransformation(String algorithmTransformation) {
		this.algorithmTransformation = algorithmTransformation;
	}

}
