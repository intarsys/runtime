package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This is the "version 1" encryption implementation. Do not use anymore
 * 
 * @deprecated
 */
@Deprecated
public class PBDesCryptdecFactory extends PBCryptdecFactory {

	private byte[] initializationVector;

	private SecretKey key;

	private Secret password;

	private byte[] salt;

	private int iterationCount;

	public PBDesCryptdecFactory(String id, byte[] iv, Secret password, byte[] salt, int iterationCount)
			throws GeneralSecurityException {
		super(id);
		this.initializationVector = iv;
		this.password = password;
		this.salt = salt;
		this.iterationCount = iterationCount;
		init();
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
		Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		return new StaticCryptdec(getId(), ecipher, dcipher);
	}

	public byte[] getInitializationVector() {
		return initializationVector;
	}

	public int getIterationCount() {
		return iterationCount;
	}

	public SecretKey getKey() {
		return key;
	}

	protected Secret getPassword() {
		return password;
	}

	public byte[] getSalt() {
		return salt;
	}

	private void init() throws GeneralSecurityException {
		KeySpec keySpec = new PBEKeySpec(password.getChars(), salt, iterationCount);
		key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
	}
}
