package de.intarsys.tools.lang;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestAliases {

	@Test
	public void test() {
		String input;
		String output;
		//
		input = null;
		output = Aliases.get().resolve(input);
		Assert.assertTrue(output == null);
		//
		input = "";
		output = Aliases.get().resolve(input);
		Assert.assertTrue(output.equals(input));
		//
		input = "schnick";
		output = Aliases.get().resolve(input);
		Assert.assertTrue(output.equals(input));
		//
		input = "foo";
		output = Aliases.get().resolve(input);
		Assert.assertTrue(output.equals("bar"));
	}

}
