package de.intarsys.tools.crypto;

import static org.junit.Assert.assertTrue;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSecretCryptoEnvironmentRegistered {

	@Before
	public void setup() throws GeneralSecurityException {

		CryptoEnvironment.get().reset();

		Secret passPhrase1 = null;
		Secret passPhrase2 = null;
		byte[] salt = null;
		byte[] iv = null;
		passPhrase1 = Secret.hide("test1".toCharArray());
		passPhrase2 = Secret.hide("test2".toCharArray());
		salt = new byte[] { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x34, (byte) 0xE3,
				(byte) 0x03 };
		iv = new byte[] { (byte) 0x8e, (byte) 0x53, (byte) 0x22, (byte) 0x9a, (byte) 0xae, (byte) 0xff, (byte) 0xE3,
				(byte) 0x00, (byte) 0xba, (byte) 0x9B, (byte) 0xC8, (byte) 0x11, (byte) 0x19, (byte) 0xd1, (byte) 0x87,
				(byte) 0x03 };
		//
		// setup CrytpoEnvironment
		ICryptdecFactory cryptdec1 = new PBAES128CryptdecFactory("TestCryptoEnvironment.test1", iv, passPhrase1, salt,
				19);
		CryptoEnvironment.get().registerCryptdecFactory(cryptdec1);
		ICryptdecFactory cryptdec2 = new PBAES128CryptdecFactory("TestCryptoEnvironment.test2", iv, passPhrase2, salt,
				19);
		CryptoEnvironment.get().registerCryptdecFactory(cryptdec2);
		//
		CryptoEnvironment.get().setDefaultCryptdecFactoryEncrypt(cryptdec1);
		CryptoEnvironment.get().setDefaultCryptdecFactoryDecrypt(cryptdec1);
	}

	@Test
	public void testEncodedBytesWithCryptdec() throws Exception {
		byte[] bytes;
		String encrypted1;
		byte[] decrypted1;
		String encrypted2;
		byte[] decrypted2;
		ICryptdec cryptdec1;
		ICryptdec cryptdec2;
		//
		cryptdec1 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test1");
		cryptdec2 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test2");
		//
		bytes = null;
		encrypted1 = Secret.hide(cryptdec1, bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		bytes = new byte[0];
		encrypted1 = Secret.hide(cryptdec1, bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		encrypted2 = Secret.hide(cryptdec2, bytes).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(bytes, decrypted2));
		//
		bytes = new byte[] { 0x00 };
		encrypted1 = Secret.hide(cryptdec1, bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		encrypted2 = Secret.hide(cryptdec2, bytes).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(bytes, decrypted2));
		//
		bytes = new byte[] { (byte) 0xff };
		encrypted1 = Secret.hide(cryptdec1, bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		encrypted2 = Secret.hide(cryptdec2, bytes).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(bytes, decrypted2));
		//
		bytes = "test".getBytes();
		encrypted1 = Secret.hide(cryptdec1, bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		encrypted2 = Secret.hide(cryptdec2, bytes).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(bytes, decrypted2));
	}

	@Test
	public void testEncodedBytesWithDefault() throws Exception {
		byte[] bytes;
		String encrypted1;
		byte[] decrypted1;
		//
		bytes = null;
		encrypted1 = Secret.hide(bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		bytes = new byte[0];
		encrypted1 = Secret.hide(bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = new byte[] { 0x00 };
		encrypted1 = Secret.hide(bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = new byte[] { (byte) 0xff };
		encrypted1 = Secret.hide(bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
		//
		bytes = "test".getBytes();
		encrypted1 = Secret.hide(bytes).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getBytes();
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(bytes, decrypted1));
	}

	@Test
	public void testEncodedCharsWithCryptdec() throws Exception {
		char[] chars;
		String encrypted1;
		char[] decrypted1;
		String encrypted2;
		char[] decrypted2;
		ICryptdec cryptdec1;
		ICryptdec cryptdec2;
		//
		cryptdec1 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test1");
		cryptdec2 = CryptoEnvironment.get().createCryptdec("TestCryptoEnvironment.test2");
		//
		chars = null;
		encrypted1 = Secret.hide(cryptdec1, chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		chars = "".toCharArray();
		encrypted1 = Secret.hide(cryptdec1, chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		encrypted2 = Secret.hide(cryptdec2, chars).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
		assertTrue(!Arrays.equals(chars, encrypted2.toCharArray()));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(chars, decrypted2));
		//
		chars = " ".toCharArray();
		encrypted1 = Secret.hide(cryptdec1, chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		encrypted2 = Secret.hide(cryptdec2, chars).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
		assertTrue(!Arrays.equals(chars, encrypted2.toCharArray()));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(chars, decrypted2));
		//
		chars = "test".toCharArray();
		encrypted1 = Secret.hide(cryptdec1, chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		encrypted2 = Secret.hide(cryptdec2, chars).getEncoded();
		decrypted2 = Secret.parse(encrypted2).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
		assertTrue(!Arrays.equals(chars, encrypted2.toCharArray()));
		assertTrue(encrypted2.startsWith("TestCryptoEnvironment.test2#"));
		assertTrue(Arrays.equals(chars, decrypted2));
	}

	@Test
	public void testEncodedCharsWithDefault() throws Exception {
		char[] chars;
		String encrypted1;
		char[] decrypted1;
		//
		chars = null;
		encrypted1 = Secret.hide(chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		assertTrue(encrypted1 == null);
		assertTrue(decrypted1 == null);
		//
		chars = "".toCharArray();
		encrypted1 = Secret.hide(chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = " ".toCharArray();
		encrypted1 = Secret.hide(chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
		//
		chars = "test".toCharArray();
		encrypted1 = Secret.hide(chars).getEncoded();
		decrypted1 = Secret.parse(encrypted1).getChars();
		assertTrue(!Arrays.equals(chars, encrypted1.toCharArray()));
		assertTrue(encrypted1.startsWith("TestCryptoEnvironment.test1#"));
		assertTrue(Arrays.equals(chars, decrypted1));
	}

	@Test
	public void testHideBytes() throws Exception {
		Secret secret;
		byte[] in;
		byte[] out;
		//
		in = "diedel".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(secret.isEmpty());
		//
		in = " ".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "1".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "zur abwechslung mal ein langer text".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "äöüÄÖÜß€".getBytes();
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = new byte[] {};
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(secret.isEmpty());
		//
		in = new byte[] { 0x00 };
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = new byte[] { (byte) 0xff };
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = new byte[] { 0x00, 0x00 };
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = new byte[] { (byte) 0xff, (byte) 0xff };
		secret = Secret.hide(in);
		out = secret.getBytes();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
	}

	@Test
	public void testHideChars() throws Exception {
		Secret secret;
		char[] in;
		char[] out;
		//
		in = "diedel".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(secret.isEmpty());
		//
		in = " ".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "1".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "zur abwechslung mal ein langer text".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "äöüÄÖÜß€".toCharArray();
		secret = Secret.hide(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
	}

	@Test
	public void testHideEmpty() throws GeneralSecurityException {
		Secret secret;
		//
		secret = Secret.hide((char[]) null);
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString() == null);
		Assert.assertTrue(secret.getBytes() == null);
		Assert.assertTrue(secret.getChars() == null);
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(secret.equals(Secret.EMPTY));
		//
		secret = Secret.hide((byte[]) null);
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString() == null);
		Assert.assertTrue(secret.getBytes() == null);
		Assert.assertTrue(secret.getChars() == null);
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(secret.equals(Secret.EMPTY));
		//
		secret = Secret.hide(new char[] {});
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
		secret = Secret.hide(new byte[] {});
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
	}

	@Test
	public void testParse() throws Exception {
		Secret holder;
		char[] in;
		char[] out;

		in = "diedel".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));

		in = "".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));

		in = " ".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));

		in = "1".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));

		in = "zur abwechslung mal ein langer text".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));

		in = "äöüÄÖÜß€".toCharArray();
		holder = Secret.parse(Secret.hide(in).getEncoded());
		out = holder.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
	}
}
