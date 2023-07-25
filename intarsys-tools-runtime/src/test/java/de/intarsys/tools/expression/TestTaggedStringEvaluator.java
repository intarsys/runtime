package de.intarsys.tools.expression;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.intarsys.tools.functor.Args;
import junit.framework.TestCase;

public class TestTaggedStringEvaluator extends TestCase {

	public void testDefaultRecursive() throws EvaluationException {

		MapResolver nested = MapResolver.createStrict();
		nested.put("string", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("integer", Integer.valueOf(1)); //$NON-NLS-1$
		nested.put("float", Float.valueOf(1.123456f)); //$NON-NLS-1$
		// recursion tests
		nested.put("oops", "oops:*"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive0", "${string}"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive1", "${recursive0}"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive2", "${recursive1}"); //$NON-NLS-1$ //$NON-NLS-2$
		nested.put("recursive3", "${recursive2}"); //$NON-NLS-1$ //$NON-NLS-2$
		//
		TaggedStringEvaluator formatter;
		formatter = TaggedStringEvaluator.decorate(nested);

		Object result;
		//
		result = formatter.evaluate("${empty:!recursive0}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("${string}")); //$NON-NLS-1$
		result = formatter.evaluate("${empty:!recursive0:*}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("value")); //$NON-NLS-1$
		result = formatter.evaluate("${empty:!'diedel':*}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("diedel")); //$NON-NLS-1$
		result = formatter.evaluate("${empty:!'${recursive0}'}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("${recursive0}")); //$NON-NLS-1$
		result = formatter.evaluate("${empty:!'${recursive0}':*}", Args.create()); //$NON-NLS-1$
		assertThat(result, is("value")); //$NON-NLS-1$
	}

	public void testEvaluateEmpty() throws Exception {
		String template;
		Object result;
		MapResolver map;
		TaggedStringEvaluator evaluator;
		//
		map = new MapResolver();
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("".equals(result));
	}

	public void testEvaluateEscape() throws Exception {
		String template;
		Object result;
		MapResolver map;
		TaggedStringEvaluator evaluator;
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${${}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("${".equals(result));
	}

	public void testEvaluateNonString() throws Exception {
		String template;
		Object result;
		MapResolver map;
		TaggedStringEvaluator evaluator;
		//
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${diedel}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue(Integer.valueOf(42).equals(result));
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = " ${diedel}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue(" 42".equals(result));
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${diedel} ";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("42 ".equals(result));
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${foo}${diedel}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("bar42".equals(result));
	}

	public void testEvaluateSimple() throws Exception {
		String template;
		Object result;
		MapResolver map;
		TaggedStringEvaluator evaluator;
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${foo}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("bar".equals(result));
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = " ${foo}";
		result = evaluator.evaluate(template, Args.create());
		assertTrue(" bar".equals(result));
		//
		map = new MapResolver();
		map.put("foo", "bar");
		map.put("diedel", 42);
		evaluator = TaggedStringEvaluator.decorate(map);
		template = "${foo} ";
		result = evaluator.evaluate(template, Args.create());
		assertTrue("bar ".equals(result));
	}

}
