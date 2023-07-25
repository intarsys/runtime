/*
 * Copyright (c) 2008, intarsys GmbH
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
package de.intarsys.tools.expression;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.intarsys.tools.functor.Args;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class TestProcessingDecoratorFormat {

	private MapResolver nested;

	private ProcessingDecorator formatter;

	@Before
	public void setUp() throws Exception {
		nested = new MapResolver(false);
		nested.put("string", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("integer", Integer.valueOf(1)); //$NON-NLS-1$
		nested.put("float", Float.valueOf(1.123456f)); //$NON-NLS-1$
		// recursion tests
		nested.put("value", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive0", "string"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive1", "recursive0"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive2", "recursive1"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive3", "recursive2"); //$NON-NLS-1$ //$NON-NLS-2$
		//
		formatter = new ProcessingDecorator(nested);
	}

	@Test
	public void testDateFormat() throws EvaluationException {
		Object result;
		//
		result = formatter.evaluate("integer:dd", Args.create()); //$NON-NLS-1$
		assertThat(result, is(formattedStartOfEpoch(DateFormat.FULL)));
		result = formatter.evaluate("integer:dds", Args.create()); //$NON-NLS-1$
		assertThat(result, is(formattedStartOfEpoch(DateFormat.SHORT)));
		result = formatter.evaluate("integer:ddm", Args.create()); //$NON-NLS-1$
		assertThat(result, is(formattedStartOfEpoch(DateFormat.MEDIUM)));
		result = formatter.evaluate("integer:ddf", Args.create()); //$NON-NLS-1$
		assertThat(result, is(formattedStartOfEpoch(DateFormat.FULL)));
	}

	private String formattedStartOfEpoch(int style) {
		return DateFormat.getDateInstance(style).format(new Date(0));
	}

	@Test
	public void testDatePatternFormat() throws EvaluationException {
		Object result;
		//
		result = formatter.evaluate("integer:d(yyyyMMdd)", Args.create()); //$NON-NLS-1$
		assertThat(result, is("19700101"));
		result = formatter.evaluate("integer:d(yyyy.MM.dd)", Args.create()); //$NON-NLS-1$
		assertThat(result, is("1970.01.01"));
		result = formatter.evaluate("integer:d(yyyy.MM.dd hh:mm)", Args.create()); //$NON-NLS-1$
		assertThat(result, is("1970.01.01 01:00"));
		result = formatter.evaluate("integer:(d(yyyy.MM.dd hh:mm))", Args.create()); //$NON-NLS-1$
		assertThat(result, is("1970.01.01 01:00"));
	}

	@Test
	public void testDateTimeFormat() throws EvaluationException {
		Object result;
		String compare;
		DateFormat dateFormat;
		//
		result = formatter.evaluate("integer:d", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is("1970_01_01-01_00_00_001"));
		result = formatter.evaluate("integer:ds", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
		result = formatter.evaluate("integer:dm", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
		result = formatter.evaluate("integer:df", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
	}

	@Test
	public void testDefaultFormat() throws EvaluationException {
		Object result;
		//
		result = formatter.evaluate("string:", Args.create()); //$NON-NLS-1$
		assertThat(result, is("value")); //$NON-NLS-1$
		result = formatter.evaluate("integer:", Args.create()); //$NON-NLS-1$
		assertThat(result, is(1)); // $NON-NLS-1$
		result = formatter.evaluate("float:", Args.create()); //$NON-NLS-1$
		assertThat(result, is(1.123456f)); // $NON-NLS-1$
		result = formatter.evaluate("recursive0:", Args.create()); //$NON-NLS-1$
		assertThat(result, is("string")); //$NON-NLS-1$
	}

	@Test
	public void testTimeFormat() throws EvaluationException {
		Object result;
		String compare;
		DateFormat dateFormat;
		//
		result = formatter.evaluate("integer:dt", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getTimeInstance(DateFormat.FULL);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
		result = formatter.evaluate("integer:dts", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
		result = formatter.evaluate("integer:dtm", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
		result = formatter.evaluate("integer:dtf", Args.create()); //$NON-NLS-1$
		dateFormat = DateFormat.getTimeInstance(DateFormat.FULL);
		compare = dateFormat.format(new Date(1));
		assertThat(result, is(compare));
	}

	@Test
	public void testUnformatted() throws EvaluationException {
		Object result;
		//
		result = formatter.evaluate("string", Args.create()); //$NON-NLS-1$
		assertTrue("value".equals(result)); //$NON-NLS-1$
		result = formatter.evaluate("integer", Args.create()); //$NON-NLS-1$
		assertTrue(Integer.valueOf(1).equals(result));
		result = formatter.evaluate("float", Args.create()); //$NON-NLS-1$
		assertTrue(Float.valueOf(1.123456f).equals(result));
		result = formatter.evaluate("recursive0", Args.create()); //$NON-NLS-1$
		assertTrue("string".equals(result)); //$NON-NLS-1$
	}

}
