package de.intarsys.tools.string;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class TestCharacterTools {

	@Test
	public void testTrim() {
		char[] in;
		char[] out;
		//
		in = null;
		out = CharacterTools.trim(in);
		Assert.assertTrue(out == in);
		//
		in = "".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(out == in);
		//
		in = "1".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(out == in);
		//
		in = " ".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(Arrays.equals(out, new char[] {}));
		//
		in = " 1".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(Arrays.equals(out, new char[] { '1' }));
		//
		in = "1 ".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(Arrays.equals(out, new char[] { '1' }));
		//
		in = " 1 ".toCharArray();
		out = CharacterTools.trim(in);
		Assert.assertTrue(Arrays.equals(out, new char[] { '1' }));
	}

}
