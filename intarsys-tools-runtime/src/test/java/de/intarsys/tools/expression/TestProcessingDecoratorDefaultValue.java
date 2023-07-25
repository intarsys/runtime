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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestProcessingDecoratorDefaultValue {

	private ProcessingDecorator decoratorLazy;

	private ProcessingDecorator decoratorStrict;

	@Before
	public void setUp() throws Exception {
		MapResolver nested;
		nested = new MapResolver(false);
		nested.put("success", "hello"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("diedel", "doedel"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("gnu", "gnat"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("zick", 10); //$NON-NLS-1$ //$NON-NLS-2$
		//
		decoratorLazy = new ProcessingDecorator(nested);

		nested = new MapResolver(true);
		nested.put("success", "hello"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("diedel", "doedel"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("gnu", "gnat"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("zick", 10); //$NON-NLS-1$ //$NON-NLS-2$
		//
		decoratorStrict = new ProcessingDecorator(nested);
	};

	@Test
	public void testDefaultValueLazy() throws EvaluationException {
		Object result;
		IArgs args;
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo", args); //$NON-NLS-1$
		assertThat(result, nullValue()); // $NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:", args); //$NON-NLS-1$
		assertThat(result, nullValue()); // $NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:!'what'", args); //$NON-NLS-1$
		assertThat(result, is("what")); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:!bar", args); //$NON-NLS-1$
		assertThat(result, nullValue()); // $NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:!diedel", args); //$NON-NLS-1$
		assertThat(result, is("doedel")); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("gnu:!diedel", args); //$NON-NLS-1$
		assertThat(result, is("gnat")); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:!bar:!diedel", args); //$NON-NLS-1$
		assertThat(result, is("doedel")); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:(!bar:i):!diedel", args); //$NON-NLS-1$
		assertThat(result, is("doedel")); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:!zick:i:!diedel", args); //$NON-NLS-1$
		assertThat(result, is("10")); // $NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("foo:(!zick:i):!diedel", args); //$NON-NLS-1$
		assertThat(result, is("10")); // $NON-NLS-1$
	}

	@Test
	public void testDefaultValueStrict() throws EvaluationException {
		Object result;
		IArgs args;
		//
		try {
			args = Args.create();
			result = decoratorStrict.evaluate("foo", args); //$NON-NLS-1$
			fail();
		} catch (EvaluationException e) {
			//
		}
		//
		try {
			args = Args.create();
			result = decoratorStrict.evaluate("foo:", args); //$NON-NLS-1$
			fail();
		} catch (EvaluationException e) {
			//
		}
		//
		try {
			args = Args.create();
			result = decoratorStrict.evaluate("foo:!bar", args); //$NON-NLS-1$
			fail();
		} catch (EvaluationException e) {
			//
		}
		//
		args = Args.create();
		result = decoratorStrict.evaluate("foo:!diedel", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorStrict.evaluate("gnu:!diedel", args); //$NON-NLS-1$
		assertTrue("gnat".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorStrict.evaluate("foo:!bar:!diedel", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorStrict.evaluate("foo:(!bar:i):!diedel", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorStrict.evaluate("foo:!zick:i:!diedel", args); //$NON-NLS-1$
		assertTrue("10".equals(result)); // $NON-NLS-1$
		//
		args = Args.create();
		result = decoratorStrict.evaluate("foo:(!zick:i):!diedel", args); //$NON-NLS-1$
		assertTrue("10".equals(result)); // $NON-NLS-1$
	}

	@Test
	public void testNonDefaultValue() throws EvaluationException {
		Object result;
		IArgs args;
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:!'what'", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:!bar", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:!diedel", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:!bar:!diedel", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:(!bar:i):!diedel", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:!zick:i:!diedel", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
		//
		args = Args.create();
		result = decoratorLazy.evaluate("success:(!zick:i):!diedel", args); //$NON-NLS-1$
		assertTrue("hello".equals(result)); //$NON-NLS-1$
	}

}
