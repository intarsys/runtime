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
package de.intarsys.tools.number;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

/**
 * 
 */
@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestNumberWrapper {

	@Test
	public void testDecrementInstance() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0");
		wrapper.increment(-1);

		String result = wrapper.toString();
		assertTrue(result.equals("-1"));

	}

	@Test
	public void testDecrementInterval() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0-10");
		wrapper.increment(-1);

		String result = wrapper.toString();
		assertTrue(result.equals("-1-9"));
	}

	@Test
	public void testDecrementList() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0; 2; 4");
		wrapper.increment(-1);

		String result = wrapper.toString();
		assertTrue(result.equals("-1;1;3"));
	}

	@Test
	public void testDecrementMixed() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0; 4-6; 9");
		wrapper.increment(-1);

		String result = wrapper.toString();
		assertTrue(result.equals("-1;3-5;8"));
	}

	@Test
	public void testIncrementInstance() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0");
		wrapper.increment(1);

		String result = wrapper.toString();
		assertTrue(result.equals("1"));

	}

	@Test
	public void testIncrementInterval() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0-10");
		wrapper.increment(1);

		String result = wrapper.toString();
		assertTrue(result.equals("1-11"));
	}

	@Test
	public void testIncrementList() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0; 2; 4");
		wrapper.increment(1);

		String result = wrapper.toString();
		assertTrue(result.equals("1;3;5"));
	}

	@Test
	public void testIncrementMixed() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("0; 4-6; 9");
		wrapper.increment(1);

		String result = wrapper.toString();
		assertTrue(result.equals("1;5-7;10"));
	}

	@Test
	public void testParseDouble() throws IOException {
		NumberWrapper wrapper = NumberParser.parse("1.2;3.8-4.9");
		Iterator wrapperIterator = wrapper.iterator();
		assertTrue(wrapperIterator.hasNext());

		Number n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(1.2)));

		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(3.8)));

		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(4.8)));
	}

	@Test(expected = IOException.class)
	public void testParseFailAlpha() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("abc;5;12");
	}

	@Test(expected = IOException.class)
	public void testParseFailDot() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("1.;2");
	}

	@Test(expected = IOException.class)
	public void testParseFailDot2() throws Exception {
		de.intarsys.tools.number.NumberParser.parse("15.12.23.23");
	}

	@Test(expected = IOException.class)
	public void testParseFailIncomplete() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("1-");
	}

	@Test(expected = IOException.class)
	public void testParseFailIncomplete2() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("1-;5");
	}

	@Test(expected = IOException.class)
	public void testParseFailSpace() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("5 645 12");
	}

	@Test(expected = IOException.class)
	public void testParseFailSpace2() throws Exception {
		de.intarsys.tools.number.NumberParser.parseInteger("5 645- 12;3");
	}

	@Test
	public void testParseIntegerCornerCase() throws Exception {
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("1;");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(1.0));
		assertFalse(wrapperIterator.hasNext());
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger(";1");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(1.0));
		assertFalse(wrapperIterator.hasNext());
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("");
		wrapperIterator = wrapper.iterator();
		assertFalse(wrapperIterator.hasNext());
		//
	}

	@Test
	public void testParseIntegerList() throws Exception {
		// correct combinations
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("1;2");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(1.0));
		assertThat((Number) wrapperIterator.next(), is(2.0));
		assertFalse(wrapperIterator.hasNext());
	}

	@Test
	public void testParseIntegerListRange() throws Exception {
		// correct combinations
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger(" 5 - 10; 8      -12");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(5.0));
		assertThat((Number) wrapperIterator.next(), is(6.0));
		assertThat((Number) wrapperIterator.next(), is(7.0));
		assertThat((Number) wrapperIterator.next(), is(8.0));
		assertThat((Number) wrapperIterator.next(), is(9.0));
		assertThat((Number) wrapperIterator.next(), is(10.0));
		assertThat((Number) wrapperIterator.next(), is(8.0));
		assertThat((Number) wrapperIterator.next(), is(9.0));
		assertThat((Number) wrapperIterator.next(), is(10.0));
		assertThat((Number) wrapperIterator.next(), is(11.0));
		assertThat((Number) wrapperIterator.next(), is(12.0));
		assertFalse(wrapperIterator.hasNext());
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("9-10;19-20");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(9.0));
		assertThat((Number) wrapperIterator.next(), is(10.0));
		assertThat((Number) wrapperIterator.next(), is(19.0));
		assertThat((Number) wrapperIterator.next(), is(20.0));
		assertFalse(wrapperIterator.hasNext());
	}

	@Test
	public void testParseIntegerListRangeSingle() throws Exception {
		// correct combinations
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger(" 98; 105-107;56; 204  - 206  ");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(98.0));
		assertThat((Number) wrapperIterator.next(), is(105.0));
		assertThat((Number) wrapperIterator.next(), is(106.0));
		assertThat((Number) wrapperIterator.next(), is(107.0));
		assertThat((Number) wrapperIterator.next(), is(56.0));
		assertThat((Number) wrapperIterator.next(), is(204.0));
		assertThat((Number) wrapperIterator.next(), is(205.0));
		assertThat((Number) wrapperIterator.next(), is(206.0));
		assertFalse(wrapperIterator.hasNext());
	}

	@Test
	public void testParseIntegerSingle() throws Exception {
		// correct combinations
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("5");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(5.0));
		assertFalse(wrapperIterator.hasNext());
	}

	@Test
	public void testParseList() throws IOException {
		NumberWrapper wrapper = NumberParser.parseInteger("1;2-3;10-12;9-10");
		Iterator wrapperIterator = wrapper.iterator();

		assertTrue(wrapperIterator.hasNext());
		Number n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(1)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(2)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(3)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(10)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(11)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(12)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(9)));

		assertTrue(wrapperIterator.hasNext());
		n = (Number) wrapperIterator.next();
		assertTrue(n.equals(Double.valueOf(10)));
	}

	@Test
	public void testParseRange() throws Exception {
		// correct combinations
		NumberWrapper wrapper;
		Iterator wrapperIterator;
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger("5-10");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(5.0));
		assertThat((Number) wrapperIterator.next(), is(6.0));
		assertThat((Number) wrapperIterator.next(), is(7.0));
		assertThat((Number) wrapperIterator.next(), is(8.0));
		assertThat((Number) wrapperIterator.next(), is(9.0));
		assertThat((Number) wrapperIterator.next(), is(10.0));
		assertFalse(wrapperIterator.hasNext());
		//
		wrapper = de.intarsys.tools.number.NumberParser.parseInteger(" 5 - 10 ");
		wrapperIterator = wrapper.iterator();
		assertThat((Number) wrapperIterator.next(), is(5.0));
		assertThat((Number) wrapperIterator.next(), is(6.0));
		assertThat((Number) wrapperIterator.next(), is(7.0));
		assertThat((Number) wrapperIterator.next(), is(8.0));
		assertThat((Number) wrapperIterator.next(), is(9.0));
		assertThat((Number) wrapperIterator.next(), is(10.0));
		assertFalse(wrapperIterator.hasNext());
		//
	}
}
