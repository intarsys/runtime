package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import de.intarsys.tools.exception.InternalError;
import junit.framework.TestCase;

public class TestPBAES128 extends TestCase {
	static {
		try {

			CryptoEnvironment.get().reset();

			Secret passPhrase1 = null;
			Secret passPhrase2 = null;
			byte[] salt = null;
			byte[] iv = null;
			passPhrase1 = Secret.hide("test1".toCharArray());
			passPhrase2 = Secret.hide("test2".toCharArray());
			salt = new byte[] { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x34,
					(byte) 0xE3, (byte) 0x03 };
			iv = new byte[] { (byte) 0x8e, (byte) 0x53, (byte) 0x22, (byte) 0x9a, (byte) 0xae, (byte) 0xff, (byte) 0xE3,
					(byte) 0x00, (byte) 0xba, (byte) 0x9B, (byte) 0xC8, (byte) 0x11, (byte) 0x19, (byte) 0xd1,
					(byte) 0x87, (byte) 0x03 };
			//
			// setup CrytpoEnvironment
			ICryptdecFactory cryptdec1 = new PBAES128CryptdecFactory("TestCryptoEnvironment.test1", iv, passPhrase1,
					salt, 19);
			CryptoEnvironment.get().registerCryptdecFactory(cryptdec1);
			ICryptdecFactory cryptdec2 = new PBAES128CryptdecFactory("TestCryptoEnvironment.test2", iv, passPhrase2,
					salt, 19);
			CryptoEnvironment.get().registerCryptdecFactory(cryptdec2);
			//
			CryptoEnvironment.get().setDefaultCryptdecFactoryEncrypt(cryptdec1);
			CryptoEnvironment.get().setDefaultCryptdecFactoryDecrypt(cryptdec1);
		} catch (GeneralSecurityException e) {
			throw new InternalError("failed to create crypt environment", e);
		}
	}

	public void testCreate() throws Exception {
		ICryptdec cryptdec;
		//
		try {
			cryptdec = CryptoEnvironment.get().createCryptdec("test");
			fail("should not find");
		} catch (Exception e) {
			//
		}
		cryptdec = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test1");
	}

	public void testEncryptBytesWithCryptdec() throws Exception {
		byte[] bytes;
		byte[] encrypted1;
		byte[] decrypted1;
		byte[] encrypted2;
		byte[] decrypted2;
		ICryptdec cryptdec1;
		ICryptdec cryptdec2;
		//
		cryptdec1 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test1");
		cryptdec2 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test2");
		//
		bytes = null;
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec1);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1, cryptdec1);
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		bytes = new byte[0];
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec1);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1, cryptdec1);
		encrypted2 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec2);
		decrypted2 = CryptoEnvironment.get().decryptBytes(encrypted2, cryptdec2);
		assertTrue(!Arrays.equals(encrypted1, encrypted2));
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(!Arrays.equals(bytes, encrypted2));
		assertTrue(Arrays.equals(bytes, decrypted2));
		//
		bytes = new byte[] { 0x00 };
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec1);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1, cryptdec1);
		encrypted2 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec2);
		decrypted2 = CryptoEnvironment.get().decryptBytes(encrypted2, cryptdec2);
		assertTrue(!Arrays.equals(encrypted1, encrypted2));
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(!Arrays.equals(bytes, encrypted2));
		assertTrue(Arrays.equals(bytes, decrypted2));
		//
		bytes = "test".getBytes();
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec1);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1, cryptdec1);
		encrypted2 = CryptoEnvironment.get().encryptBytes(bytes, cryptdec2);
		decrypted2 = CryptoEnvironment.get().decryptBytes(encrypted2, cryptdec2);
		assertTrue(!Arrays.equals(encrypted1, encrypted2));
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(!Arrays.equals(bytes, encrypted2));
		assertTrue(Arrays.equals(bytes, decrypted2));
	}

	public void testEncryptBytesWithDefault() throws Exception {
		byte[] bytes;
		byte[] encrypted1;
		byte[] decrypted1;
		//
		bytes = null;
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		bytes = new byte[0];
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = new byte[] { 0x00 };
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = "test".getBytes();
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(Arrays.equals(bytes, decrypted1));
	}

	public void testEncryptCharsWithDefault() throws Exception {
		char[] chars;
		char[] decrypted1;
		byte[] encrypted1;
		//
		chars = null;
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		chars = "".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = " ".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = "test".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(Arrays.equals(chars, decrypted1));
	}
}
