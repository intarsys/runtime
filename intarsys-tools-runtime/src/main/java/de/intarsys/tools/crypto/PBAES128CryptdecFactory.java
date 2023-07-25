package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Factory for {@link ICryptdec} instances, based on AES128. The secret is
 * derived from a password using a key derivation function (PBKDF2WithHmacSHA1).
 * 
 * as of https://tools.ietf.org/html/rfc2898, 19 iterations is not safe, use >
 * 2000
 * 
 * review increase security level, factor out salt, increase iterations...
 */
public class PBAES128CryptdecFactory extends PBCryptdecFactory {

	private byte[] initializationVector;

	private Secret password;

	private byte[] salt;

	private int iterationCount;

	private SecretKey key;

	public PBAES128CryptdecFactory(String id, byte[] iv, Secret password, byte[] salt, int iterationCount)
			throws GeneralSecurityException {
		super(id);
		this.initializationVector = Arrays.copyOf(iv, iv.length);
		this.password = password;
		this.salt = Arrays.copyOf(salt, salt.length);
		this.iterationCount = iterationCount;
		init();
	}

	@Override
	public ICryptdec createCryptdec() throws GeneralSecurityException {
		Cipher ecipher = Cipher.getInstance(getCipherId());
		Cipher dcipher = Cipher.getInstance(getCipherId());
		@SuppressWarnings("java:S3329")
		IvParameterSpec ips = new IvParameterSpec(getInitializationVector());
		ecipher.init(Cipher.ENCRYPT_MODE, getKey(), ips);
		dcipher.init(Cipher.DECRYPT_MODE, getKey(), ips);
		return new StaticCryptdec(getId(), ecipher, dcipher);
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

	protected Secret getPassword() {
		return password;
	}

	protected String getPBKeyFactoryId() {
		return "PBKDF2WithHmacSHA1";
	}

	public byte[] getSalt() {
		return salt;
	}

	private void init() throws GeneralSecurityException {
		SecretKeyFactory pbKeyFactory = SecretKeyFactory.getInstance(getPBKeyFactoryId());
		PBEKeySpec pbKeySpec = new PBEKeySpec(password.getChars(), salt, iterationCount, getKeyLength());
		try {
			SecretKey pbSecret = pbKeyFactory.generateSecret(pbKeySpec);
			key = new SecretKeySpec(pbSecret.getEncoded(), getCipherAlgorithmId());
		} finally {
			pbKeySpec.clearPassword();
		}
	}
}
