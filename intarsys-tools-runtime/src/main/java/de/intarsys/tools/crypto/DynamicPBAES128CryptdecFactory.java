package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Factory for {@link ICryptdec} instances, based on AES128. The secret is
 * derived from a password using a key derivation function (PBKDF2WithHmacSHA1).
 * 
 */
public class DynamicPBAES128CryptdecFactory extends PBCryptdecFactory {

	private SecretKey key;

	public DynamicPBAES128CryptdecFactory(String id, Secret password, byte[] salt, int iterationCount)
			throws GeneralSecurityException {
		super(id);
		SecretKeyFactory pbKeyFactory = SecretKeyFactory.getInstance(getPBKeyFactoryId());
		PBEKeySpec pbKeySpec = new PBEKeySpec(password.getChars(), salt, iterationCount, getKeyLength());
		try {
			SecretKey pbSecret = pbKeyFactory.generateSecret(pbKeySpec);
			key = new SecretKeySpec(pbSecret.getEncoded(), getCipherAlgorithmId());
		} finally {
			pbKeySpec.clearPassword();
		}
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		return new DynamicCryptdec(getId(), getKey(), getCipherId());
	}

	protected String getCipherAlgorithmId() {
		return "AES";
	}

	protected String getCipherId() {
		return "AES/CBC/PKCS5Padding";
	}

	private SecretKey getKey() {
		return key;
	}

	protected int getKeyLength() {
		return 128;
	}

	protected String getPBKeyFactoryId() {
		return "PBKDF2WithHmacSHA1";
	}

}
