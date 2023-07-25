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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.functor.Args;
import junit.framework.TestCase;

@SuppressWarnings({ "MultipleStringLiterals" })
public class TestReflectiveResolver extends TestCase {
	public static class Dummy {
		public String publicStringField = "stringfield"; //$NON-NLS-1$

		public Dummy publicDummyField;

		public Dummy createdNestedDummy() {
			publicDummyField = new Dummy();
			return publicDummyField;
		}

		public Dummy getPublicDummyGetter() {
			return publicDummyField;
		}

		public List<Object> getPublicListGetter() {
			List<Object> result = new ArrayList<>();
			result.add("element"); //$NON-NLS-1$
			result.add(Integer.valueOf(1));
			return result;
		}

		public Map<String, Object> getPublicMapGetter() {
			Map<String, Object> result = new HashMap<>();
			result.put("key1", "element"); //$NON-NLS-1$ //$NON-NLS-2$
			result.put("key2", Integer.valueOf(1)); //$NON-NLS-1$
			return result;
		}

		public String getPublicStringGetter() {
			return "stringgetter"; //$NON-NLS-1$
		}

		public boolean isPublicBooleanGetter() {
			return true;
		}
	}

	private Dummy dummy;

	private ReflectiveResolver resolver;

	@Override
	protected void setUp() throws Exception {
		dummy = new Dummy();
		resolver = new ReflectiveResolver(dummy);
	}

	public void testNotAvailable() throws EvaluationException {
		Object result;
		dummy.createdNestedDummy().createdNestedDummy();
		//
		try {
			result = resolver.evaluate("notavail", Args.create()); //$NON-NLS-1$
			fail("should throw exception"); //$NON-NLS-1$
		} catch (EvaluationException e) {
			// expected
		}
	}

	public void testOutOfRange() {
		Object result;
		dummy.createdNestedDummy().createdNestedDummy();
		//
		try {
			result = resolver.evaluate("publicListGetter[-1]", Args.create()); //$NON-NLS-1$
			fail("should throw exception"); //$NON-NLS-1$
		} catch (EvaluationException e) {
			// expected
		}
		try {
			result = resolver.evaluate("publicListGetter[5678]", Args.create()); //$NON-NLS-1$
			fail("should throw exception"); //$NON-NLS-1$
		} catch (EvaluationException e) {
			// expected
		}
	}

	public void testPathField() throws EvaluationException {
		Object result;
		dummy.createdNestedDummy().createdNestedDummy();
		//
		result = resolver.evaluate("publicDummyField.publicStringField", //$NON-NLS-1$
				Args.create());
		assertTrue("stringfield".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyField.publicStringGetter", //$NON-NLS-1$
				Args.create());
		assertTrue("stringgetter".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyField.publicDummyField", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Dummy);
		result = resolver.evaluate("publicDummyField.publicDummyGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Dummy);
		result = resolver.evaluate("publicDummyField.publicListGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof List);
		result = resolver.evaluate("publicDummyField.publicListGetter.0", //$NON-NLS-1$
				Args.create());
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyField.publicListGetter.1", //$NON-NLS-1$
				Args.create());
		assertTrue(Integer.valueOf(1).equals(result));
		result = resolver.evaluate("publicDummyField.publicMapGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Map);
		result = resolver.evaluate("publicDummyField.publicMapGetter.key1", //$NON-NLS-1$
				Args.create());
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyField.publicMapGetter.key2", //$NON-NLS-1$
				Args.create());
		assertTrue(Integer.valueOf(1).equals(result));
	}

	public void testPathGetter() throws EvaluationException {
		Object result;
		dummy.createdNestedDummy().createdNestedDummy();
		//
		result = resolver.evaluate("publicDummyGetter.publicStringField", //$NON-NLS-1$
				Args.create());
		assertTrue("stringfield".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyGetter.publicStringGetter", //$NON-NLS-1$
				Args.create());
		assertTrue("stringgetter".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyGetter.publicDummyField", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Dummy);
		result = resolver.evaluate("publicDummyGetter.publicDummyGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Dummy);
		result = resolver.evaluate("publicDummyGetter.publicListGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof List);
		result = resolver.evaluate("publicDummyGetter.publicListGetter.0", //$NON-NLS-1$
				Args.create());
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyGetter.publicListGetter.1", //$NON-NLS-1$
				Args.create());
		assertTrue(Integer.valueOf(1).equals(result));
		result = resolver.evaluate("publicDummyGetter.publicMapGetter", //$NON-NLS-1$
				Args.create());
		assertTrue(result instanceof Map);
		result = resolver.evaluate("publicDummyGetter.publicMapGetter.key1", //$NON-NLS-1$
				Args.create());
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyGetter.publicMapGetter.key2", //$NON-NLS-1$
				Args.create());
		assertTrue(Integer.valueOf(1).equals(result));
	}

	public void testSimple() throws EvaluationException {
		Object result;
		dummy.createdNestedDummy().createdNestedDummy();
		//
		result = resolver.evaluate("publicStringField", Args.create()); //$NON-NLS-1$
		assertTrue("stringfield".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicStringGetter", Args.create()); //$NON-NLS-1$
		assertTrue("stringgetter".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicDummyField", Args.create()); //$NON-NLS-1$
		assertTrue(result instanceof Dummy);
		assertTrue(result != dummy);
		result = resolver.evaluate("publicDummyGetter", Args.create()); //$NON-NLS-1$
		assertTrue(result instanceof Dummy);
		assertTrue(result != dummy);
		result = resolver.evaluate("publicListGetter", Args.create()); //$NON-NLS-1$
		assertTrue(result instanceof List);
		result = resolver.evaluate("publicListGetter.0", Args.create()); //$NON-NLS-1$
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicListGetter.1", Args.create()); //$NON-NLS-1$
		assertTrue(Integer.valueOf(1).equals(result));
		result = resolver.evaluate("publicMapGetter", Args.create()); //$NON-NLS-1$
		assertTrue(result instanceof Map);
		result = resolver.evaluate("publicMapGetter.key1", Args.create()); //$NON-NLS-1$
		assertTrue("element".equals(result)); //$NON-NLS-1$
		result = resolver.evaluate("publicMapGetter.key2", Args.create()); //$NON-NLS-1$
		assertTrue(Integer.valueOf(1).equals(result));
	}
}
