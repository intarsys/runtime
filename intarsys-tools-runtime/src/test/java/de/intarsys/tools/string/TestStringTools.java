/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.string;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

// TODO StringTools used the default locale. Therefore, these tests should be run with different default locales.
// TODO Instead of assertTrue(actual.equals(expected)) use assertEquals(expected, actual), which has better diagnostics.
// TODO Split methods so that we have only one test per test case so that you can see, which test failed.
// TODO Good candidate for parameterized tests.
public class TestStringTools {
	@Test
	public void formatDate_d() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "d";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedDate(DateFormat.FULL, in)));
	}

	@Test
	public void formatDate_df() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "df";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedDate(DateFormat.FULL, in)));
	}

	@Test
	public void formatDate_dm() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "dm";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedDate(DateFormat.MEDIUM, in)));
	}

	@Test
	public void formatDate_ds() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "ds";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedDate(DateFormat.SHORT, in)));
	}

	private String formattedDate(int style, Date date) {
		return DateFormat.getDateInstance(style).format(date);
	}

	@Test
	public void formatDate_f() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "f";
		String out = StringTools.formatDate(in, format);
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		String compare = dateFormat.format(in);
		assertThat(out, equalTo(compare));
	}

	@Test
	public void formatDate_m() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "m";
		String out = StringTools.formatDate(in, format);
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		String compare = dateFormat.format(in);
		assertThat(out, equalTo(compare));
	}

	@Test
	public void formatDate_s() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "s";
		String out = StringTools.formatDate(in, format);
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		String compare = dateFormat.format(in);
		assertThat(out, equalTo(compare));
	}

	@Test
	public void formatDate_t() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "t";
		String out = StringTools.formatDate(in, format);
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.FULL);
		String compare = dateFormat.format(in);
		assertThat(out, equalTo(compare));
	}

	@Test
	public void formatDate_tf() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "tf";
		String out = StringTools.formatDate(in, format);
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.FULL, Locale.getDefault());
		String compare = dateFormat.format(in);
		assertThat(out, equalTo(compare));
	}

	@Test
	public void formatDate_tm() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "tm";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedTime(DateFormat.MEDIUM, in)));
	}

	@Test
	public void formatDate_ts() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "ts";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(formattedTime(DateFormat.SHORT, in)));
	}

	private String formattedTime(int style, Date date) {
		return DateFormat.getTimeInstance(style).format(date);
	}

	@Test
	public void formatDateEmptyFormat() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo("1965_02_02-07_08_09_000"));
	}

	@Test
	public void formatDateEmptyPattern() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "()";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo(""));
	}

	@Test
	public void formatDatePattern_MM() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "(MM)";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo("02"));
	}

	@Test
	public void formatDatePattern_yyyy() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "(yyyy)";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo("1965"));
	}

	@Test
	public void formatDateUnknown() {
		Date in = new Date(65, 1, 2, 7, 8, 9);
		String format = "x";
		String out = StringTools.formatDate(in, format);
		assertThat(out, equalTo("1965_02_02-07_08_09_000"));
	}

	@Test
	public void testBreakForced() {
		String in;
		String out;
		//
		in = "";
		out = StringTools.breakForced(in, 1, "x");
		assertTrue("".equals(out));
		in = "";
		out = StringTools.breakForced(in, 10, "x");
		assertTrue("".equals(out));
		in = null;
		out = StringTools.breakForced(in, 1, "x");
		assertTrue("".equals(out));
		in = "1";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("1".equals(out));
		in = "12";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("12".equals(out));
		in = "123";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("123".equals(out));
		in = "1234";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("123x4".equals(out));
		in = "12345";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("123x45".equals(out));
		in = "123456";
		out = StringTools.breakForced(in, 3, "x");
		assertTrue("123x456".equals(out));
	}

	@Test
	public void testCamelCase() throws IOException {
		String value;
		String result;
		String expected;
		//
		value = "";
		result = StringTools.toCamelCase(value, "");
		expected = "";
		assertTrue(expected.equals(result));
		//
		value = "a";
		result = StringTools.toCamelCase(value, "");
		expected = "a";
		assertTrue(expected.equals(result));
		//
		value = "aa";
		result = StringTools.toCamelCase(value, "");
		expected = "aa";
		assertTrue(expected.equals(result));
		//
		value = "a.b";
		result = StringTools.toCamelCase(value, "");
		expected = "aB";
		assertTrue(expected.equals(result));
		//
		value = "aa.bb";
		result = StringTools.toCamelCase(value, "");
		expected = "aaBb";
		assertTrue(expected.equals(result));
		//
		value = "aa_bb";
		result = StringTools.toCamelCase(value, "");
		expected = "aaBb";
		assertTrue(expected.equals(result));
		//
		value = "AA_BB";
		result = StringTools.toCamelCase(value, "");
		expected = "aaBb";
		assertTrue(expected.equals(result));
		//
		value = "aCamelCase";
		result = StringTools.toCamelCase(value, "");
		expected = "aCamelCase";
		assertTrue(expected.equals(result));
		//
		value = "ANUPPERCASE";
		result = StringTools.toCamelCase(value, "");
		expected = "anuppercase";
		assertTrue(expected.equals(result));
		//
		value = "aCamelCase";
		result = StringTools.toCamelCase(value, ".");
		expected = "aCamelCase";
		assertTrue(expected.equals(result));
		//
		value = "foo.bar";
		result = StringTools.toCamelCase(value, ".");
		expected = "foo.bar";
		assertTrue(expected.equals(result));
		//
		value = "Foo.Bar";
		result = StringTools.toCamelCase(value, ".");
		expected = "foo.bar";
		assertTrue(expected.equals(result));
		//
		value = "foo_bar.diedel_doedel";
		result = StringTools.toCamelCase(value, ".");
		expected = "fooBar.diedelDoedel";
		assertTrue(expected.equals(result));
		//
		value = "Foo_BAR.dieDel_doedeL";
		result = StringTools.toCamelCase(value, ".");
		expected = "fooBar.diedelDoedel";
		assertTrue(expected.equals(result));
	}

	@Test
	public void testCommonPrefix() {
		String a;
		String b;
		String c;
		//
		a = "x";
		b = "y";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "";
		b = "y";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "x";
		b = "";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "a";
		b = "a";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "ax";
		b = "ay";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "a";
		b = "ay";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "ax";
		b = "a";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		//
		//
		a = "X";
		b = "y";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "";
		b = "Y";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "X";
		b = "";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals(""));
		//
		a = "a";
		b = "A";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "ax";
		b = "AY";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "a";
		b = "AY";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
		//
		a = "ax";
		b = "A";
		c = StringTools.getCommonPrefix(a, b, true);
		assertTrue(c.equals("a"));
	}

	@Test
	public void testFormatFloat() {
		double in = 1234.567;
		String out;
		//
		out = StringTools.formatFloat(in, "");
		assertEquals(formattedDouble("", in), out);
		//
		out = StringTools.formatFloat(in, "()");
		assertEquals(formattedDouble("", in), out);
		//
		out = StringTools.formatFloat(in, "(0,000.0)");
		assertEquals(formattedDouble("0,000.0", in), out);
		//
		out = StringTools.formatFloat(in, "(0.0)");
		assertEquals(formattedDouble("0.0", in), out);
		//
		out = StringTools.formatFloat(in, "(000000,000.000000)");
		assertEquals(formattedDouble("000000,000.000000", in), out);
		//
		out = StringTools.formatFloat(in, "(000000000)");
		assertEquals(formattedDouble("000000000", in), out);
	}

	private String formattedDouble(String pattern, double value) {
		return new DecimalFormat(pattern).format(value);
	}

	@Test
	public void testFormatPath() {
		String in;
		String format;
		String out;
		//
		format = "p";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "p";
		in = "/";
		out = StringTools.format(in, format);
		assertTrue("/".equals(out));
		//
		format = "p";
		in = "gnu";
		out = StringTools.format(in, format);
		assertTrue("gnu".equals(out));
		//
		format = "p";
		in = "gnu/gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu/gnat".equals(out));
		//
		format = "p";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "p";
		in = "c:/gnu";
		out = StringTools.format(in, format);
		assertTrue("c:/gnu".equals(out));
		//
		format = "p";
		in = "not*?<>|:\"";
		out = StringTools.format(in, format);
		assertTrue("not_______".equals(out));

		// suffix slash
		//
		format = "ps+/";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "ps+/";
		in = "/";
		out = StringTools.format(in, format);
		assertTrue("/".equals(out));
		//
		format = "ps+/";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat/".equals(out));
		//
		format = "ps+/";
		in = "gnu\\gnat\\";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat\\".equals(out));
		//
		format = "ps+/";
		in = "gnu\\gnat/";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat/".equals(out));
		//
		format = "ps-/";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "ps-/";
		in = "/";
		out = StringTools.format(in, format);
		assertTrue("/".equals(out));
		//
		format = "ps-/";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "ps-/";
		in = "gnu\\gnat\\";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "ps-/";
		in = "gnu\\gnat/";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));

		// prefix slash
		//
		format = "pp+/";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "pp+/";
		in = "/";
		out = StringTools.format(in, format);
		assertTrue("/".equals(out));
		//
		format = "pp+/";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("/gnu\\gnat".equals(out));
		//
		format = "pp+/";
		in = "\\gnu\\gnat\\";
		out = StringTools.format(in, format);
		assertTrue("\\gnu\\gnat\\".equals(out));
		//
		format = "pp+/";
		in = "/gnu\\gnat/";
		out = StringTools.format(in, format);
		assertTrue("/gnu\\gnat/".equals(out));
		//
		format = "pp-/";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "pp-/";
		in = "/";
		out = StringTools.format(in, format);
		assertTrue("/".equals(out));
		//
		format = "pp-/";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "pp-/";
		in = "\\gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "pp-/";
		in = "/gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));

		// suffix dot
		//
		format = "ps+.";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "ps+.";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat.".equals(out));
		//
		format = "ps+.";
		in = "gnu\\gnat.";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat.".equals(out));
		//
		format = "ps-.";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "ps-.";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));
		//
		format = "ps-.";
		in = "gnu\\gnat.";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat".equals(out));

		// prefix dot
		//
		format = "ps+.";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "pp+.";
		in = "gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue(".gnu\\gnat".equals(out));
		//
		format = "pp+.";
		in = ".gnu\\gnat";
		out = StringTools.format(in, format);
		assertTrue(".gnu\\gnat".equals(out));
		//
		format = "ps+.";
		in = "";
		out = StringTools.format(in, format);
		assertTrue("".equals(out));
		//
		format = "pp-.";
		in = "gnu\\gnat.";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat.".equals(out));
		//
		format = "pp-.";
		in = ".gnu\\gnat.";
		out = StringTools.format(in, format);
		assertTrue("gnu\\gnat.".equals(out));

		// mixed mode
		//
		format = "pp+.s+/";
		in = "gnu";
		out = StringTools.format(in, format);
		assertTrue(".gnu/".equals(out));
		//
		format = "pp-.s-/";
		in = ".gnu/";
		out = StringTools.format(in, format);
		assertTrue("gnu".equals(out));

	}

	@Test
	public void testFormatString() {
		String in = "test";
		String format;
		String out;
		//
		format = "";
		out = StringTools.formatString(in, format);
		assertTrue("test".equals(out));
		//
		format = "()";
		out = StringTools.formatString(in, format);
		assertTrue("test".equals(out));
		//
		format = "(0)";
		out = StringTools.formatString(in, format);
		assertTrue("test".equals(out));
		//
		format = "(1)";
		out = StringTools.formatString(in, format);
		assertTrue("est".equals(out));
		//
		format = "(1,2)";
		out = StringTools.formatString(in, format);
		assertTrue("es".equals(out));
		//
		format = "(1,1)";
		out = StringTools.formatString(in, format);
		assertTrue("e".equals(out));
		//
		format = "(-1)";
		out = StringTools.formatString(in, format);
		assertTrue("t".equals(out));
		//
		format = "(-2)";
		out = StringTools.formatString(in, format);
		assertTrue("st".equals(out));
		//
		format = "(-3,2)";
		out = StringTools.formatString(in, format);
		assertTrue("es".equals(out));
		//
		format = "(-3,-2)";
		out = StringTools.formatString(in, format);
		assertTrue("es".equals(out));
	}

	@Test
	public void testParseCommandline() {
		String[] result = null;

		result = StringTools.parseCommandline("");
		assertTrue(result.length == 0);

		result = StringTools.parseCommandline(" ");
		assertTrue(result.length == 0);

		result = StringTools.parseCommandline("   ");
		assertTrue(result.length == 0);

		result = StringTools.parseCommandline("a");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("a"));

		result = StringTools.parseCommandline("ab");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("ab"));

		result = StringTools.parseCommandline("a b");
		assertTrue(result.length == 2);
		assertTrue(result[0].equals("a"));
		assertTrue(result[1].equals("b"));

		result = StringTools.parseCommandline("a bc");
		assertTrue(result.length == 2);
		assertTrue(result[0].equals("a"));
		assertTrue(result[1].equals("bc"));

		result = StringTools.parseCommandline("a b c");
		assertTrue(result.length == 3);
		assertTrue(result[0].equals("a"));
		assertTrue(result[1].equals("b"));
		assertTrue(result[2].equals("c"));

		result = StringTools.parseCommandline("\"a\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("a"));

		result = StringTools.parseCommandline("\"ab\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("ab"));

		result = StringTools.parseCommandline("\"a b\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("a b"));

		result = StringTools.parseCommandline("\"a bc\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("a bc"));

		result = StringTools.parseCommandline("\"a b c\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("a b c"));

		result = StringTools.parseCommandline("a \\\"b c");
		assertTrue(result.length == 3);
		assertTrue(result[0].equals("a"));
		assertTrue(result[1].equals("\"b"));
		assertTrue(result[2].equals("c"));

		result = StringTools.parseCommandline("a \"b c");
		assertTrue(result.length == 2);
		assertTrue(result[0].equals("a"));
		assertTrue(result[1].equals("b c"));

		result = StringTools.parseCommandline("C:\\temp\\test.txt");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("C:\\temp\\test.txt"));

		result = StringTools.parseCommandline("C:\\temp\\test.txt delete");
		assertTrue(result.length == 2);
		assertTrue(result[0].equals("C:\\temp\\test.txt"));
		assertTrue(result[1].equals("delete"));

		result = StringTools.parseCommandline("\"C:\\temp\\test.txt delete\"");
		assertTrue(result.length == 1);
		assertTrue(result[0].equals("C:\\temp\\test.txt delete"));

		result = StringTools.parseCommandline("a # b asd \nc");
		assertTrue(result.length == 2);
		assertTrue(Arrays.equals(result, new String[] { "a", "c" }));

		result = StringTools.parseCommandline("a#b\nc");
		assertTrue(result.length == 2);
		assertTrue(Arrays.equals(result, new String[] { "a", "c" }));
	}

	@Test
	public void testPathParse() {
		String value;
		List<Token> tokens;
		Token token;
		//
		value = "";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 0);
		//
		value = "foo";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 1);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		//
		value = "\"foo\"";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 1);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		//
		value = "'foo'";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 1);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		//
		value = "foo.bar";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar"));
		//
		value = "foo.\"bar\"";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar"));
		//
		value = "foo.'bar'";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar"));
		//
		value = "foo.bar.three";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 3);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar"));
		assertTrue(tokens.get(2).getValue().equals("three"));
		//
		value = "foo.\"bar.three\"";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar.three"));
		//
		value = "foo.'bar.three'";
		tokens = StringTools.pathParse(value, '.');
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getValue().equals("foo"));
		assertTrue(tokens.get(1).getValue().equals("bar.three"));

	}

	@Test
	public void testQuote() {
		String value;
		String result;
		String expected;
		//
		value = "";
		result = StringTools.quote(value);
		expected = "\"\"";
		assertTrue(expected.equals(result));
		//
		value = "x";
		result = StringTools.quote(value);
		expected = "\"x\"";
		assertTrue(expected.equals(result));
		//
		value = "\"";
		result = StringTools.quote(value);
		expected = "\"\\\"\"";
		assertTrue(expected.equals(result));
		//
		value = "\"\"";
		result = StringTools.quote(value);
		expected = "\"\\\"\\\"\"";
		assertTrue(expected.equals(result));
		//
		value = "\\";
		result = StringTools.quote(value);
		expected = "\"\\\\\"";
		assertTrue(expected.equals(result));
		//
		value = "a\\b";
		result = StringTools.quote(value);
		expected = "\"a\\\\b\"";
		assertTrue(expected.equals(result));
		//
		value = "\"a\\b\"";
		result = StringTools.quote(value);
		expected = "\"\\\"a\\\\b\\\"\"";
		assertTrue(expected.equals(result));
		//
		value = "a\rb";
		result = StringTools.quote(value);
		expected = "\"a\\rb\"";
		assertTrue(expected.equals(result));
		//
		value = "a\nb";
		result = StringTools.quote(value);
		expected = "\"a\\nb\"";
		assertTrue(expected.equals(result));
		//
		value = "a\tb";
		result = StringTools.quote(value);
		expected = "\"a\\tb\"";
		assertTrue(expected.equals(result));
	}

	@Test
	public void testTrimLeft() {

		String in;
		String out;

		in = "test";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals("test"));

		in = "test ";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals("test "));

		in = " test";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals("test"));

		in = "test test";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals("test test"));

		in = "";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals(""));

		in = " ";
		out = StringTools.trimLeft(in);
		assertTrue(out.equals(""));
	}

	@Test
	public void testTrimRight() {
		String in;
		String out;

		in = "test";
		out = StringTools.trimRight(in);
		assertTrue(out.equals("test"));

		in = "test ";
		out = StringTools.trimRight(in);
		assertTrue(out.equals("test"));

		in = " test";
		out = StringTools.trimRight(in);
		assertTrue(out.equals(" test"));

		in = "test test";
		out = StringTools.trimRight(in);
		assertTrue(out.equals("test test"));

		in = "";
		out = StringTools.trimRight(in);
		assertTrue(out.equals(""));

		in = " ";
		out = StringTools.trimRight(in);
		assertTrue(out.equals(""));
	}

	@Test
	public void testUnquote() throws IOException {
		String value;
		String result;
		String expected;
		//
		value = "\"\"";
		result = StringTools.unquote(value);
		expected = "";
		assertTrue(expected.equals(result));
		//
		value = "\"'\"";
		result = StringTools.unquote(value);
		expected = "'";
		assertTrue(expected.equals(result));
		//
		value = "''";
		result = StringTools.unquote(value);
		expected = "";
		assertTrue(expected.equals(result));
		//
		value = "'\"'";
		result = StringTools.unquote(value);
		expected = "\"";
		assertTrue(expected.equals(result));
		//
		value = "\"x\"";
		result = StringTools.unquote(value);
		expected = "x";
		assertTrue(expected.equals(result));
		//
		value = "\"\\\"\"";
		result = StringTools.unquote(value);
		expected = "\"";
		assertTrue(expected.equals(result));
		//
		value = "\"\\\"\\\"\"";
		result = StringTools.unquote(value);
		expected = "\"\"";
		assertTrue(expected.equals(result));
		//
		value = "\"\\\\\"";
		result = StringTools.unquote(value);
		expected = "\\";
		assertTrue(expected.equals(result));
		//
		value = "\"a\\\\b\"";
		result = StringTools.unquote(value);
		expected = "a\\b";
		assertTrue(expected.equals(result));
		//
		value = "";
		result = StringTools.unquote(value);
		expected = "";
		assertTrue(expected.equals(result));
		//
		value = "test";
		result = StringTools.unquote(value);
		expected = "test";
		assertTrue(expected.equals(result));
		//
		value = "x\"x";
		result = StringTools.unquote(value);
		expected = "x\"x";
		assertTrue(expected.equals(result));
		//
		value = "x\\\"x";
		result = StringTools.unquote(value);
		expected = "x\\\"x";
		assertTrue(expected.equals(result));
		//
		value = "x\\";
		result = StringTools.unquote(value);
		expected = "x\\";
		assertTrue(expected.equals(result));
		//
		value = "\"a\\rb\"";
		result = StringTools.unquote(value);
		expected = "a\rb";
		assertTrue(expected.equals(result));
		//
		value = "\"a\\nb\"";
		result = StringTools.unquote(value);
		expected = "a\nb";
		assertTrue(expected.equals(result));
		//
		value = "\"a\\tb\"";
		result = StringTools.unquote(value);
		expected = "a\tb";
		assertTrue(expected.equals(result));
	}
}
