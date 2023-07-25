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

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import junit.framework.TestCase;

/**
 * 
 */
public class TestProcessingDecoratorRecursive extends TestCase {

	public void testResolverDeepRecursive() throws EvaluationException {

		MapResolver nested = MapResolver.createStrict();
		nested.put("string", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("integer", Integer.valueOf(1)); //$NON-NLS-1$
		nested.put("float", Float.valueOf(1.123456f)); //$NON-NLS-1$
		// recursion tests
		nested.put("oops", "oops:*"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive0", "string"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive1", "recursive0:*"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive2", "recursive1:*"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive3", "recursive2:*"); //$NON-NLS-1$ //$NON-NLS-2$
		//
		ProcessingDecorator formatter;
		formatter = new ProcessingDecorator(nested);
		formatter.setRecursionEvaluator(new IStringEvaluator() {

			@Override
			public Object evaluate(String expression, IArgs args) throws EvaluationException {
				try {
					return formatter.evaluate(expression, args);
				} catch (EvaluationException e) {
					return expression;
				}
			}
		});

		Object result;
		//
		result = formatter.evaluate("string:*", Args.create()); //$NON-NLS-1$
		assertThat(result, is("value")); //$NON-NLS-1$
		result = formatter.evaluate("integer:*", Args.create()); //$NON-NLS-1$
		assertTrue("1".equals(String.valueOf(result))); //$NON-NLS-1$
		result = formatter.evaluate("float:*", Args.create()); //$NON-NLS-1$
		assertTrue("1.123456".equals(String.valueOf(result))); //$NON-NLS-1$
		result = formatter.evaluate("recursive0:*", Args.create()); //$NON-NLS-1$
		assertTrue("value".equals(result)); //$NON-NLS-1$
		result = formatter.evaluate("recursive3:*", Args.create()); //$NON-NLS-1$
		assertTrue("value".equals(result)); //$NON-NLS-1$
		result = formatter.evaluate("oops:*", Args.create()); //$NON-NLS-1$
		assertTrue("oops:*".equals(result)); //$NON-NLS-1$
	}

	public void testTemplateLenientDeepRecursive() throws EvaluationException {

		MapResolver nested = new MapResolver(true);
		nested.put("name", "Jim"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("greeting", "hello, ${name}"); //$NON-NLS-1$
		nested.put("startmessage", "${greeting}. The system is completely functional"); //$NON-NLS-1$
		nested.put("stilldeeper", "${startmessage}"); //$NON-NLS-1$
		nested.put("oops", "oops${oops}"); //$NON-NLS-1$
		nested.put("ohoh", "${ohoh}"); //$NON-NLS-1$
		//
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorateLenient(nested);
		Object result;
		//
		result = evaluator.evaluate("${name}", Args.create()); //$NON-NLS-1$
		assertTrue("Jim".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${greeting}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, ${name}".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${greeting:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${startmessage}", Args.create()); //$NON-NLS-1$
		assertTrue("${greeting}. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${startmessage:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${stilldeeper:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${oops:*}", Args.create()); //$NON-NLS-1$
		assertTrue("oopsoopsoopsoopsoopsoopsoopsoopsoopsoopsoopsoops${oops}".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${ohoh:*}", Args.create()); //$NON-NLS-1$
		assertTrue("${ohoh}".equals(result)); //$NON-NLS-1$
	}

	public void testTemplateLenientDeepRecursiveWithDefault() throws EvaluationException {

		MapResolver nested = new MapResolver(true);
		nested.put("name", "Jim"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("greeting", "hello, ${name}"); //$NON-NLS-1$
		nested.put("ref1", "${greeting}"); //$NON-NLS-1$
		nested.put("ref2", ""); //$NON-NLS-1$
		nested.put("ref3", "${undefined}"); //$NON-NLS-1$
		nested.put("default", "foo"); //$NON-NLS-1$
		//
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorateLenient(nested);
		Object result;
		//
		result = evaluator.evaluate("${ref1:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("hello, Jim")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref2:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("foo")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref3:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("${undefined}")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref4:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("foo")); //$NON-NLS-1$
	}

	public void testTemplateStrictDeepRecursive() throws EvaluationException {

		MapResolver nested = new MapResolver(true);
		nested.put("name", "Jim"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("greeting", "hello, ${name}"); //$NON-NLS-1$
		nested.put("startmessage", "${greeting}. The system is completely functional"); //$NON-NLS-1$
		nested.put("stilldeeper", "${startmessage}"); //$NON-NLS-1$
		nested.put("oops", "oops${oops}"); //$NON-NLS-1$
		nested.put("ohoh", "${ohoh}"); //$NON-NLS-1$
		//
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorate(nested);
		Object result;
		//
		result = evaluator.evaluate("${name}", Args.create()); //$NON-NLS-1$
		assertTrue("Jim".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${greeting}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, ${name}".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${greeting:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${startmessage}", Args.create()); //$NON-NLS-1$
		assertTrue("${greeting}. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${startmessage:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${stilldeeper:*}", Args.create()); //$NON-NLS-1$
		assertTrue("hello, Jim. The system is completely functional".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${oops:*}", Args.create()); //$NON-NLS-1$
		assertTrue("oopsoopsoopsoopsoopsoopsoopsoopsoopsoopsoopsoops${oops}".equals(result)); //$NON-NLS-1$
		result = evaluator.evaluate("${ohoh:*}", Args.create()); //$NON-NLS-1$
		assertTrue("${ohoh}".equals(result)); //$NON-NLS-1$
	}

	public void testTemplateStrictDeepRecursiveWithDefault() throws EvaluationException {

		MapResolver nested = new MapResolver(true);
		nested.put("name", "Jim"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("greeting", "hello, ${name}"); //$NON-NLS-1$
		nested.put("ref1", "${greeting}"); //$NON-NLS-1$
		nested.put("ref2", ""); //$NON-NLS-1$
		nested.put("ref3", "${undefined}"); //$NON-NLS-1$
		nested.put("default", "foo"); //$NON-NLS-1$
		//
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorate(nested);
		Object result;
		//
		result = evaluator.evaluate("${ref1:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("hello, Jim")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref2:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("foo")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref3:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("foo")); //$NON-NLS-1$
		result = evaluator.evaluate("${ref4:*:!default}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("foo")); //$NON-NLS-1$
	}
}
