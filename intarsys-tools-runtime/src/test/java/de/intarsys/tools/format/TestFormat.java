package de.intarsys.tools.format;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings({ "MultipleStringLiterals" })
public class TestFormat {

	@Test
	public void test() {
		String result;
		//
		result = Format.simple("");
		assertThat(result, is(""));
		//
		result = Format.simple("foo");
		assertThat(result, is("foo"));
		//
		result = Format.simple("foo{}", "bar");
		assertThat(result, is("foobar"));
		//
		result = Format.simple("foo{ }", "bar");
		assertThat(result, is("foo{ }"));
	}

}
