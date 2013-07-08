package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * FActory for {@link ICryptdec} instances, based on AES128. The secret is
 * derived from a password using a key derivation function (PBKDF2WithHmacSHA1).
 * 
 */
public class PBAES128CryptdecFactory extends PBCryptdecFactory {

	private byte[] initializationVector;

	private char[] password;

	private byte[] salt;

	private int iterationCount;

	private SecretKey key;

	public PBAES128CryptdecFactory(String id, byte[] iv, char[] password,
			byte[] salt, int iterationCount) throws GeneralSecurityException {
		super(id);
		this.initializationVector = Arrays.copyOf(iv, iv.length);
		this.password = Arrays.copyOf(password, password.length);
		this.salt = Arrays.copyOf(salt, salt.length);
		this.iterationCount = iterationCount;
		init();
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		Cipher ecipher = Cipher.getInstance(getCipherId());
		Cipher dcipher = Cipher.getInstance(getCipherId());
		byte[] iv = getInitializationVector();
		if (getInitializationVector() != null) {
			IvParameterSpec ips = new IvParameterSpec(getInitializationVector());
			ecipher.init(Cipher.ENCRYPT_MODE, getKey(), ips);
			dcipher.init(Cipher.DECRYPT_MODE, getKey(), ips);
		} else {
			ecipher.init(Cipher.ENCRYPT_MODE, getKey());
			iv = ecipher.getIV();
			IvParameterSpec ips = new IvParameterSpec(getInitializationVector());
			dcipher.init(Cipher.DECRYPT_MODE, getKey(), ips);
		}
		return new StandardCryptdec(getId(), ecipher, dcipher, iv);
	}

	protected String getCipherAlgorithmId() {
		return "AES";
	}

	protected String getCipherId() {
		return "AES/CBC/PKCS5Padding";
	}

	public byte[] getInitializationVector() {
		return initializationVector;
	}

	public int getIterationCount() {
		return iterationCount;
	}

	private SecretKey getKey() {
		return key;
	}

	protected int getKeyLength() {
		return 128;
	}

	public char[] getPassword() {
		return password;
	}

	protected String getPBKeyFactoryId() {
		return "PBKDF2WithHmacSHA1";
	}

	public byte[] getSalt() {
		return salt;
	}

	private void init() throws GeneralSecurityException {
		KeySpec pbKeySpec = new PBEKeySpec(password, salt, iterationCount,
				getKeyLength());
		SecretKeyFactory pbKeyFactory = SecretKeyFactory
				.getInstance(getPBKeyFactoryId());
		SecretKey pbSecret = pbKeyFactory.generateSecret(pbKeySpec);
		key = new SecretKeySpec(pbSecret.getEncoded(), getCipherAlgorithmId());
	}
}
