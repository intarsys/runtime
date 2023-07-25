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
import static org.hamcrest.Matchers.nullValue;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import junit.framework.TestCase;

public class TestProcessingDecoratorConditional extends TestCase {

	private ProcessingDecorator formatter;

	@Override
	protected void setUp() throws Exception {
		MapResolver mapResolver = new MapResolver(false);
		mapResolver.put("diedel", "doedel"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condFalseObject", Boolean.FALSE); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condFalseString", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condFString", "f"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("cond0String", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("cond0Object", 0); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condDummyObject", "dummy"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condTrueObject", Boolean.TRUE); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condTrueString", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("condTString", "t"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("cond1String", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		mapResolver.put("cond1Object", 1); //$NON-NLS-1$ //$NON-NLS-2$
		ScopedResolver scopedResolver = ScopedResolver.create(
				new DynamicArgsResolver(),
				mapResolver);
		//
		formatter = new ProcessingDecorator(scopedResolver);
	};

	public void testConditionMatchArgs() throws EvaluationException {
		Object result;
		IArgs args;
		//
		args = Args.createNamed("x", Boolean.FALSE); //$NON-NLS-1$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", "f"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", 0); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", "bobcat"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		args = Args.createNamed("x", Boolean.TRUE); //$NON-NLS-1$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.createNamed("x", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.createNamed("x", "t"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.createNamed("x", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		args = Args.createNamed("x", 1); //$NON-NLS-1$ //$NON-NLS-2$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
	}

	public void testConditionMatchNegate() throws EvaluationException {
		Object result;
		IArgs args = Args.create();
		//
		result = formatter.evaluate("diedel:?!condFalseObject", args); //$NON-NLS-1$
		assertThat(result, is("doedel")); //$NON-NLS-1$
		//
		result = formatter.evaluate("diedel:?!condTrueObject", args); //$NON-NLS-1$
		assertThat(result, nullValue()); // $NON-NLS-1$
	}

	public void testConditionMatchOther() throws EvaluationException {
		Object result;
		IArgs args = Args.create();
		//
		result = formatter.evaluate("diedel:?condFalseObject", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?condFalseString", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?condFString", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?cond0String", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?cond0Object", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?condDummyObject", args); //$NON-NLS-1$
		assertTrue(result == null);
		//
		result = formatter.evaluate("diedel:?condTrueObject", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		result = formatter.evaluate("diedel:?condTrueString", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		result = formatter.evaluate("diedel:?condTString", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		result = formatter.evaluate("diedel:?cond1String", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
		//
		result = formatter.evaluate("diedel:?cond1Object", args); //$NON-NLS-1$
		assertTrue("doedel".equals(result)); //$NON-NLS-1$
	}

	public void testConditionNone() throws EvaluationException {
		Object result;
		IArgs args;
		//
		args = Args.create();
		result = formatter.evaluate("diedel:?", args); //$NON-NLS-1$
		assertTrue(result == null);
	}

	public void testConditionUndefined() throws EvaluationException {
		Object result;
		IArgs args;
		//
		args = Args.create();
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		args = Args.createNamed("y", Boolean.FALSE); //$NON-NLS-1$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
		args = Args.createNamed("y", Boolean.TRUE); //$NON-NLS-1$
		result = formatter.evaluate("diedel:?x", args); //$NON-NLS-1$
		assertTrue(result == null);
	}

}
