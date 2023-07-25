package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import de.intarsys.tools.collection.ByteArrayTools;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.system.SystemTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Tool methods for some crypto tasks.
 */
public final class CryptoTools {

	private static final ILogger Log = LogTools.getLogger(CryptoTools.class);

	/**
	 * Create a secret from polymorphic {@code value}.
	 *
	 * @param value the value to encrypt as a secret
	 * @return The {@link Secret} representation for value (never null).
	 */
	public static Secret createSecret(Object value) {
		if (value instanceof String) {
			value = StringEvaluatorTools.evaluate(TemplateEvaluator.get(Mode.UNTRUSTED), value);
		}
		if (value == null) {
			return Secret.EMPTY;
		}
		if (value instanceof char[]) {
			return Secret.hideTrimmed((char[]) value);
		}
		if (value instanceof byte[]) {
			return Secret.hide((byte[]) value);
		}
		if (value instanceof String) {
			return Secret.hideTrimmed(((String) value).toCharArray());
		}
		if (value instanceof Secret) {
			return (Secret) value;
		}
		throw new IllegalArgumentException("cannot convert " + value + " to Secret");
	}

	/**
	 * Create a {@link SecureRandom}.
	 *
	 * <p>
	 * The method will choose a preferred SecureRandom algorithm based on the
	 * platform. On Linux we prefer "NativePRNGNonBlocking" because it uses
	 * /dev/urandom exclusively. This prevents problems on virtual machines
	 * where /dev/random might be blocking. On Windows and macOS we prefer
	 * SHA1PRNG, because it is the only implementation on Windows anyway and on
	 * macOS we don't want any implementation that might try to write on
	 * /dev/urandom, because it might actually not be writable (using
	 * /dev/random however would be OK because it never blocks on macOS)
	 */
	public static SecureRandom createSecureRandom() {
		String algorithm;
		if (SystemTools.isLinux()) {
			algorithm = "NativePRNGNonBlocking"; //$NON-NLS-1$
		} else {
			algorithm = "SHA1PRNG"; //$NON-NLS-1$
		}
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			/*
			 * should never fail, but if it does, the default algorithm is as
			 * good as any
			 */
			secureRandom = new SecureRandom();
		}
		return secureRandom;
	}

	/**
	 * Create an {@link ICryptdec} suitable to manage transient secrets. You can
	 * en-/decrypt secrets only as long as the {@link ICryptdec} returned exists.
	 *
	 * @return A transient {@link ICryptdec}
	 */
	public static ICryptdec createSessionCryptdec() {
		Secret passPhrase = null;
		byte[] salt = null;
		byte[] iv = null;
		passPhrase = Secret.create(16);
		salt = ByteArrayTools.createRandomBytes(8);
		iv = ByteArrayTools.createRandomBytes(16);
		try {
			return new PBAES128CryptdecFactory("session", iv, passPhrase, salt, 19).createCryptdec();
		} catch (Exception e) {
			throw new IllegalStateException("cryptdec initialization error", e);
		} finally {
			Arrays.fill(salt, (byte) 0x00);
			Arrays.fill(iv, (byte) 0x00);
		}
	}

	public static byte[] getBytes(Secret secret) throws GeneralSecurityException {
		return secret == null ? null : secret.getBytes();
	}

	public static byte[] getBytes(Secret secret, byte[] defaultValue) {
		try {
			return secret == null ? null : secret.getBytes();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static byte[] getBytesNotNull(Secret secret) throws GeneralSecurityException {
		return secret == null || secret.isEmpty() ? new byte[0] : secret.getBytes();
	}

	public static byte[] getBytesNotNull(Secret secret, byte[] defaultValue) {
		try {
			return secret == null || secret.isEmpty() ? new byte[0] : secret.getBytes();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static char[] getChars(Secret secret) throws GeneralSecurityException {
		return secret == null ? null : secret.getChars();
	}

	public static char[] getChars(Secret secret, char[] defaultValue) {
		try {
			return secret == null ? null : secret.getChars();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static char[] getCharsNotNull(Secret secret) throws GeneralSecurityException {
		return secret == null || secret.isEmpty() ? new char[0] : secret.getChars();
	}

	public static char[] getCharsNotNull(Secret secret, char[] defaultValue) {
		try {
			return secret == null || secret.isEmpty() ? new char[0] : secret.getChars();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static String getString(Secret secret) throws GeneralSecurityException {
		return secret == null ? null : secret.getString();
	}

	public static String getString(Secret secret, String defaultValue) {
		try {
			return secret == null ? null : secret.getString();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static String getStringNotNull(Secret secret) throws GeneralSecurityException {
		return secret == null || secret.isEmpty() ? StringTools.EMPTY : secret.getString();
	}

	public static String getStringNotNull(Secret secret, String defaultValue) {
		try {
			return secret == null || secret.isEmpty() ? StringTools.EMPTY : secret.getString();
		} catch (GeneralSecurityException e) {
			Log.warn("failed to decrypt {}, return default value", secret);
			return defaultValue;
		}
	}

	public static boolean isEmpty(Secret secret) {
		return secret == null || secret.isEmpty();
	}

	private CryptoTools() {
	}

}
