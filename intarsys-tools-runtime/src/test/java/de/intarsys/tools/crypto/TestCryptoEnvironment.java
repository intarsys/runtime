package de.intarsys.tools.crypto;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import junit.framework.TestCase;

public class TestCryptoEnvironment extends TestCase {

	public void testCreate() throws Exception {
		ICryptdec cryptdec;
		//
		try {
			cryptdec = CryptoEnvironment.get().createCryptdec("test");
			fail("should not find");
		} catch (Exception e) {
			//
		}
	}

	public void testRegister() throws Exception {
		CryptoEnvironment.get().reset();
		//
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
		ICryptdecFactory cryptdec1 = new PBAES128CryptdecFactory("Test.test1", iv, passPhrase1, salt, 19);
		CryptoEnvironment.get().registerCryptdecFactory(cryptdec1);
		ICryptdecFactory cryptdec2 = new PBAES128CryptdecFactory("Test.test1", iv, passPhrase2, salt, 19);
		CryptoEnvironment.get().registerCryptdecFactory(cryptdec2);
		ICryptdecFactory cryptdec = CryptoEnvironment.get().lookupCryptdecFactory("Test.test1");
		assertThat(cryptdec, sameInstance(cryptdec1));
	}

}
