package de.intarsys.tools.crypto;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import de.intarsys.tools.exception.InternalError;

public class TestDynamicPBAES128 {

	@BeforeClass
	public static void init() {
		try {
			CryptoEnvironment.get().reset();
			Secret passPhrase1 = Secret.hide("test1".toCharArray());
			Secret passPhrase2 = Secret.hide("test2".toCharArray());
			// some recommendations as of 08.2017 from
			// https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet
			// salt length is 64 bit
			// iterationCount = 10000
			byte[] salt = new byte[] { //
					(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, //
					(byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03, //
					(byte) 0x6e, (byte) 0x01, (byte) 0x02, (byte) 0xf5, //
					(byte) 0xb5, (byte) 0x87, (byte) 0x72, (byte) 0xcc //
			};
			byte[] iv = new byte[] { //
					(byte) 0x74, (byte) 0x5b, (byte) 0x3c, (byte) 0x49, //
					(byte) 0x33, (byte) 0x3d, (byte) 0xc4, (byte) 0x94, //
					(byte) 0x9f, (byte) 0x2a, (byte) 0x8e, (byte) 0xff, //
					(byte) 0x24, (byte) 0x79, (byte) 0x27, (byte) 0xc1 //
			};
			ICryptdecFactory cryptdec1 = new DynamicPBAES128CryptdecFactory("TestCryptoEnvironment.test1", passPhrase1,
					salt, 10000);
			CryptoEnvironment.get().registerCryptdecFactory(cryptdec1);
			ICryptdecFactory cryptdec2 = new DynamicPBAES128CryptdecFactory("TestCryptoEnvironment.test2", passPhrase2,
					salt, 10000);
			CryptoEnvironment.get().registerCryptdecFactory(cryptdec2);
			CryptoEnvironment.get().setDefaultCryptdecFactoryDecrypt(cryptdec1);
			CryptoEnvironment.get().setDefaultCryptdecFactoryEncrypt(cryptdec1);
		} catch (GeneralSecurityException e) {
			throw new InternalError("failed to create crypt environment", e);
		}
	}

	@Test
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
		assertTrue(cryptdec instanceof DynamicCryptdec);
	}

	@Test
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
		assertThat(encrypted1, not(encrypted2));
		assertThat(bytes, not(encrypted1));
		assertThat(bytes, not(encrypted2));
		assertTrue(Arrays.equals(bytes, decrypted1));
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

	@Test
	public void testEncryptBytesWithDefault() throws Exception {
		byte[] bytes;
		byte[] encrypted1;
		byte[] encrypted1Again;
		byte[] decrypted1;
		//
		bytes = null;
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		encrypted1Again = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(encrypted1 == null);
		assertTrue(encrypted1Again == null);
		assertTrue(decrypted1 == null);
		//
		bytes = new byte[0];
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		encrypted1Again = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(!Arrays.equals(encrypted1, encrypted1Again));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = new byte[] { 0x00 };
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		encrypted1Again = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(!Arrays.equals(encrypted1, encrypted1Again));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = "test".getBytes();
		encrypted1 = CryptoEnvironment.get().encryptBytes(bytes);
		encrypted1Again = CryptoEnvironment.get().encryptBytes(bytes);
		decrypted1 = CryptoEnvironment.get().decryptBytes(encrypted1);
		assertTrue(!Arrays.equals(bytes, encrypted1));
		assertTrue(!Arrays.equals(encrypted1, encrypted1Again));
		assertTrue(Arrays.equals(bytes, decrypted1));
	}

	@Test
	public void testEncryptCharsWithDefault() throws Exception {
		char[] chars;
		char[] decrypted1;
		byte[] encrypted1;
		byte[] encrypted1Again;
		//
		chars = null;
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		encrypted1Again = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		chars = "".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		encrypted1Again = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertThat(encrypted1, not(encrypted1Again));
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = " ".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		encrypted1Again = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(!Arrays.equals(encrypted1, encrypted1Again));
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = "test".toCharArray();
		encrypted1 = CryptoEnvironment.get().encryptChars(chars);
		encrypted1Again = CryptoEnvironment.get().encryptChars(chars);
		decrypted1 = CryptoEnvironment.get().decryptChars(encrypted1);
		assertTrue(!Arrays.equals(encrypted1, encrypted1Again));
		assertTrue(Arrays.equals(chars, decrypted1));
	}
}
