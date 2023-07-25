package de.intarsys.tools.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.StringTools;

/**
 * An (encrypted) in-memory representation of a secret value.
 * 
 * If used properly, decrypted secret values can be observed on the VM stack
 * space only, not on the heap.
 *
 * A {@link Secret} can be either created via "hiding" a concrete value or by
 * parsing an already encoded (encrypted) value.
 */
public final class Secret {

	private static final String HASH = "#";

	public static final Secret EMPTY = new Secret(null, null);

	public static final String CHARS_SPECIAL = "!$%&/=?*+#-_"; //$NON-NLS-1$

	public static final String CHARS_DIGITS = "0123456879"; //$NON-NLS-1$

	public static final String CHARS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

	public static final String CHARS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

	public static final String CHARS_ALL = CHARS_DIGITS + CHARS_LOWERCASE + CHARS_UPPERCASE + CHARS_SPECIAL;

	/**
	 * Convenience method for clearing the content of a byte[].
	 * 
	 * @param bytes
	 */
	public static void clear(byte[] bytes) {
		Arrays.fill(bytes, (byte) 0x00);
	}

	/**
	 * Convenience method for clearing the content of a char[].
	 * 
	 * @param chars
	 */
	public static void clear(char[] chars) {
		Arrays.fill(chars, ' ');
	}

	/**
	 * Create a random secret with at least length characters and the default
	 * parameters.
	 * 
	 * @param length
	 *            The length of the password to create
	 * @return
	 */
	public static Secret create(int length) {
		SecureRandom rand = CryptoTools.createSecureRandom();
		return create(length, CHARS_ALL, rand);
	}

	/**
	 * Create a secure random password of length <code>length</code> using a
	 * array of chars to chose and random as random number generator.
	 * 
	 * @param length
	 *            the length of the password to generate
	 * @param validChars
	 *            the char array to chose from
	 * @param random
	 *            A random number generator
	 * @return A new random password.
	 */
	public static Secret create(int length, String validChars, Random random) {
		int size = validChars.length();
		char[] buff = new char[length];
		for (int i = 0; i < length; ++i) {
			buff[i] = validChars.charAt(random.nextInt(size));
		}
		return hide(buff);
	}

	/**
	 * Create an "encrypted" {@link Secret} from value, using the
	 * {@link CryptoEnvironment} default {@link ICryptdec}.
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hide(byte[] value) {
		try {
			ICryptdec cryptdec = CryptoEnvironment.get().createDefaultCryptdecEncrypt();
			return hide(cryptdec, value);
		} catch (GeneralSecurityException e) {
			throw new InternalError("crypto environment not set up correctly", e);
		}
	}

	/**
	 * Create an "encrypted" {@link Secret} from value, using the
	 * {@link CryptoEnvironment} default {@link ICryptdec}.
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hide(char[] value) {
		try {
			ICryptdec cryptdec = CryptoEnvironment.get().createDefaultCryptdecEncrypt();
			return hide(cryptdec, value);
		} catch (GeneralSecurityException e) {
			throw new InternalError("crypto environment not set up correctly", e);
		}
	}

	/**
	 * Create an "encrypted" {@link Secret} from value, using the designated
	 * cryptdec.
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hide(ICryptdec cryptdec, byte[] value) {
		try {
			if (value == null) {
				return new Secret(cryptdec.getId(), null);
			} else if (value.length == 0) {
				return new Secret(cryptdec.getId(), StringTools.EMPTY);
			} else {
				byte[] encrypted = cryptdec.encrypt(value);
				return new Secret(cryptdec.getId(), new String(Base64.encode(encrypted)));
			}
		} catch (GeneralSecurityException e) {
			throw new InternalError("crypto environment not set up correctly", e);
		}
	}

	/**
	 * Create an "encrypted" {@link Secret} from value, using the designated
	 * cryptdec.
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hide(ICryptdec cryptdec, char[] value) {
		byte[] bytes = CharacterTools.toByteArrayUTF8(value);
		return hide(cryptdec, bytes);
	}

	/**
	 * Create an "encrypted" {@link Secret} from value after trimming all
	 * whitespace, using the {@link CryptoEnvironment} default {@link ICryptdec}
	 * .
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hideTrimmed(char[] value) {
		try {
			ICryptdec cryptdec = CryptoEnvironment.get().createDefaultCryptdecEncrypt();
			return hideTrimmed(cryptdec, value);
		} catch (GeneralSecurityException e) {
			throw new InternalError("crypto environment not set up correctly", e);
		}
	}

	/**
	 * Create an "encrypted" {@link Secret} from value after trimming all
	 * whitespace, using the designated cryptdec.
	 * 
	 * @param value
	 * @return The {@link Secret} representation for value
	 */
	public static Secret hideTrimmed(ICryptdec cryptdec, char[] value) {
		byte[] bytes = CharacterTools.toByteArrayUTF8(CharacterTools.trim(value));
		return hide(cryptdec, bytes);
	}

	/**
	 * Create a {@link Secret} from a serialized (encrypted) secret
	 * representation.
	 * 
	 * @param encoded
	 * @return
	 */
	public static Secret parse(String encoded) {
		return new Secret(encoded);
	}

	private final String id;

	private final String data;

	protected Secret(String encoded) {
		/*
		 * when constructed with a string, we assume that the latter is in the
		 * CryptoEnvironment "encoded" format (cryptdec#data). Make sure
		 * CryptoEnvironment is initialized before "get"ting the Secret value,
		 * however.
		 */
		if (encoded == null) {
			id = null;
			data = null;
		} else {
			int index = encoded.indexOf('#');
			if (index < 0) {
				id = null;
				data = encoded;
			} else {
				id = encoded.substring(0, index);
				data = encoded.substring(index + 1);
			}
		}
	}

	protected Secret(String id, String data) {
		this.id = id;
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Secret) {
			try {
				Secret otherSecret = (Secret) other;
				byte[] bytes = getBytes();
				byte[] otherBytes = otherSecret.getBytes();
				return Arrays.equals(bytes, otherBytes);
			} catch (GeneralSecurityException e) {
				return false;
			}
		}
		return super.equals(other);
	}

	/**
	 * Decrypt the {@link Secret} and return as byte[].
	 * 
	 * If hidden input was null, this is null.
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings("java:S1168")
	public byte[] getBytes() throws GeneralSecurityException {
		if (data == null) {
			return null;
		}
		if ("".equals(data)) {
			return new byte[0];
		}
		try {
			ICryptdec cryptdec = CryptoEnvironment.get().createCryptdec(id);
			byte[] bytes = Base64.decode(data);
			byte[] decrypted = cryptdec.decrypt(bytes);
			return decrypted;
		} catch (IOException e) {
			throw new GeneralSecurityException(e);
		}
	}

	/**
	 * Decrypt the {@link Secret} and return as char[].
	 * 
	 * If hidden input was null, this is null. Character encoding is always
	 * UTF-8.
	 * 
	 * A client should clear the result array as soon as possible.
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings("java:S1168")
	public char[] getChars() throws GeneralSecurityException {
		byte[] bytes = getBytes();
		if (bytes == null) {
			return null;
		}
		try {
			return CharacterTools.toCharArrayUTF8(bytes);
		} finally {
			Arrays.fill(bytes, (byte) 0);
		}
	}

	public String getEncoded() {
		if (StringTools.isEmpty(id)) {
			if (data == null) {
				return null;
			} else if (StringTools.isEmpty(data)) {
				return StringTools.EMPTY;
			} else {
				return data;
			}
		} else {
			if (data == null) {
				return null;
			} else if (StringTools.isEmpty(data)) {
				return id + HASH;
			} else {
				return id + HASH + data;
			}
		}
	}

	/**
	 * Decrypt the {@link Secret} and return as {@link String}.
	 * 
	 * If hidden input was null, this is null. Character encoding is always
	 * UTF-8.
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public String getString() throws GeneralSecurityException {
		byte[] bytes = getBytes();
		if (bytes == null) {
			return null;
		}
		try {
			return StringTools.toStringUTF8(bytes);
		} finally {
			Arrays.fill(bytes, (byte) 0);
		}
	}

	@Override
	public int hashCode() {
		if (StringTools.isEmpty(data)) {
			return 0;
		}
		return data.hashCode();
	}

	/**
	 * True if the input was null or an empty array.
	 * 
	 * @return True if the input was null or an empty array.
	 */
	public boolean isEmpty() {
		return StringTools.isEmpty(data);
	}

	@Override
	public String toString() {
		return getEncoded();
	}
}
