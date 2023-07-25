package de.intarsys.tools.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.string.StringTools;

public class TestSecretCryptoEnvironmentDefault {

	@Before
	public void setup() {
		CryptoEnvironment.get().reset();
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
		secret = Secret.hideTrimmed((char[]) null);
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
		secret = Secret.hideTrimmed(new char[] {});
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
		secret = Secret.hideTrimmed(new char[] { ' ' });
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
	public void testHideTrimmedChars() throws Exception {
		Secret secret;
		char[] in;
		char[] out;
		//
		in = "diedel".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(secret.isEmpty());
		//
		in = " ".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(new char[] {}, out));
		Assert.assertTrue(secret.isEmpty());
		//
		in = " 1".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(new char[] { '1' }, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "1 ".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(new char[] { '1' }, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = " 1 ".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(new char[] { '1' }, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "1".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "zur abwechslung mal ein langer text".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
		//
		in = "äöüÄÖÜß€".toCharArray();
		secret = Secret.hideTrimmed(in);
		out = secret.getChars();
		Assert.assertTrue(Arrays.equals(in, out));
		Assert.assertTrue(!secret.isEmpty());
	}

	@Test
	public void testParse() throws GeneralSecurityException {
		Secret secret;
		//
		secret = Secret.parse("plain#");
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
		secret = Secret.parse("plain#" + new String(Base64.encode(StringTools.toByteArray("test"))));
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals("test"));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), StringTools.toByteArray("test")));
		Assert.assertTrue(Arrays.equals(secret.getChars(), "test".toCharArray()));
		Assert.assertTrue(!secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
	}

	@Test
	public void testParseEmpty() throws GeneralSecurityException {
		Secret secret;
		//
		secret = Secret.parse(null);
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString() == null);
		Assert.assertTrue(secret.getBytes() == null);
		Assert.assertTrue(secret.getChars() == null);
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(secret.equals(Secret.EMPTY));
		//
		secret = Secret.parse("");
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
		secret = Secret.parse("plain#");
		Assert.assertTrue(secret != null);
		Assert.assertTrue(secret.getString().equals(""));
		Assert.assertTrue(Arrays.equals(secret.getBytes(), new byte[0]));
		Assert.assertTrue(Arrays.equals(secret.getChars(), new char[0]));
		Assert.assertTrue(secret.isEmpty());
		Assert.assertTrue(!secret.equals(Secret.EMPTY));
		//
	}
}
