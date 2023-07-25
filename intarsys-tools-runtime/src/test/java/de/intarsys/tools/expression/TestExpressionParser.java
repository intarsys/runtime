package de.intarsys.tools.expression;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class TestExpressionParser {

	@Test
	public void testReadExpressionFunction() throws IOException {
		Reader r;
		String input;
		Expression output;
		//
		input = "diedel()";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel()"));
		//
		input = "diedel(doedel)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel(doedel)"));
		//
		input = "diedel(foo,bar)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel(foo,bar)"));
	}

	@Test
	public void testReadExpressionMulti() throws IOException {
		Reader r;
		String input;
		Expression output;
		//
		input = "a:b:c";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("a"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("b"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("c"));
		//
		input = "a:b:";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("a"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("b"));
		output = parser.parse(r);
		assertThat(output, nullValue());
		//
		input = "a:!b:!c";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("a"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("!b"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("!c"));
		//
		input = "(b):!c";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("(b)"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("!c"));
		//
		input = "diedel(xx:yy:zz):foo(bar:z)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel(xx:yy:zz)"));
		output = parser.parse(r);
		assertThat(output.getCode(), is("foo(bar:z)"));
	}

	@Test
	public void testReadExpressionParantheses() throws IOException {
		Reader r;
		String input;
		Expression output;
		//
		input = "(foo)";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("(foo)"));
		//
		input = "(foo;bar)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("(foo;bar)"));
		//
		input = "(foo:bar)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("(foo:bar)"));
	}

	@Test
	public void testReadExpressionPlain() throws IOException {
		Reader r;
		String input;
		Expression output;
		//
		input = "";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertNull(output);
		//
		input = "diedel";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel"));
		//
		input = " diedel";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel"));
		//
		input = "diedel ";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel"));
	}

	@Test
	public void testReadExpressionQuoted() throws IOException {
		Reader r;
		String input;
		StringLiteral output;
		//
		input = "''";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = (StringLiteral) parser.parse(r);
		assertThat(output.getValue(), is(""));
		assertThat(output.isQuoted(), is(true));
		//
		input = "\"d'ie:del\"";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = (StringLiteral) parser.parse(r);
		assertThat(output.getValue(), is("d'ie:del"));
		assertThat(output.isQuoted(), is(true));
		//
		input = "'d\"ie:del'";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = (StringLiteral) parser.parse(r);
		assertThat(output.getValue(), is("d\"ie:del"));
		assertThat(output.isQuoted(), is(true));
		//
		input = "'diedel(\"foo\")'";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = (StringLiteral) parser.parse(r);
		assertThat(output.getValue(), is("diedel(\"foo\")"));
		assertThat(output.isQuoted(), is(true));
	}

	@Test
	public void testReadExpressionWithDelimiter() throws IOException {
		Reader r;
		String input;
		Expression output;
		//
		input = "a:b:c";
		r = new StringReader(input);
		ExpressionParser parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("a"));
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("b"));
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("c"));
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output, nullValue());
		//
		input = "diedel(xx:yy:zz):foo(bar:z)";
		r = new StringReader(input);
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("diedel(xx:yy:zz)"));
		parser = new ExpressionParser(':');
		output = parser.parse(r);
		assertThat(output.getCode(), is("foo(bar:z)"));
	}
}
