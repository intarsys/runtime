package de.intarsys.tools.functor;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.reflect.FieldException;
import junit.framework.TestCase;

public class TestArgsCruncher extends TestCase {

	public void testExpand() throws FunctorException {
		ArgsCruncher cruncher = new ArgsCruncher();
		IArgs target;
		IArgs args;
		IArgs result;
		//
		MapResolver resolver = new MapResolver();
		resolver.put("b", "y");
		IStringEvaluator evaluator = TaggedStringEvaluator.decorate(resolver);
		TemplateEvaluator.set(Mode.UNTRUSTED, evaluator);
		//
		args = Args.create();
		target = Args.create();
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		//
		args = Args.create();
		ArgTools.putPath(args, "expand", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "${b}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a", null).equals("${b}"));
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.x", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "${b}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a", null).equals("${b}"));
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.a", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "${b}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a", null).equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.a", "");
		target = Args.create();
		ArgTools.putPath(target, "a.x.y", "${b}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a.x.y", null).equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.a", "");
		target = Args.create();
		ArgTools.putPath(target, "a.x.y", "${b}");
		ArgTools.putPath(target, "b.x.y", "${b}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 2);
		assertTrue(ArgTools.getString(target, "a.x.y", null).equals("y"));
		assertTrue(ArgTools.getString(target, "b.x.y", null).equals("${b}"));
	}

	public void testExpandObject() throws FunctorException, FieldException {
		ArgsCruncher cruncher = new ArgsCruncher();
		IArgs target;
		IArgs args;
		IArgs result;
		//
		Object theObject = new Object();
		Object theArgs = Args.create();
		MapResolver resolver = new MapResolver();
		resolver.put("object", theObject);
		resolver.put("args", theArgs);
		IStringEvaluator evaluator = TaggedStringEvaluator.decorate(resolver);
		TemplateEvaluator.set(Mode.UNTRUSTED, evaluator);
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.a", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "${object}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertSame(target, result);
		assertEquals(1, target.size());
		assertSame(theObject, result.get("a"));
		//
		args = Args.create();
		ArgTools.putPath(args, "expand.a", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "${args}");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertSame(target, result);
		assertEquals(1, target.size());
		assertSame(theArgs, result.get("a"));
	}

	public void testSet() throws FunctorException {
		ArgsCruncher cruncher = new ArgsCruncher();
		IArgs target;
		IArgs args;
		IArgs result;
		//
		args = Args.create();
		target = Args.create();
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		//
		args = Args.create();
		ArgTools.putPath(args, "set", "");
		target = Args.create();
		ArgTools.putPath(target, "a", "b");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a", null).equals("b"));
		//
		args = Args.create();
		ArgTools.putPath(args, "set.x", "y");
		target = Args.create();
		ArgTools.putPath(target, "a", "b");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 2);
		assertTrue(ArgTools.getString(target, "a", null).equals("b"));
		assertTrue(ArgTools.getString(target, "x", null).equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "set.a", "y");
		target = Args.create();
		ArgTools.putPath(target, "a", "b");
		args.put("args", target);
		result = cruncher.perform(new FunctorCall(null, args));
		assertTrue(result == target);
		assertTrue(target.size() == 1);
		assertTrue(ArgTools.getString(target, "a", null).equals("y"));
	}
}
