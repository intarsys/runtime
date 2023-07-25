package de.intarsys.tools.functor;

import de.intarsys.tools.functor.common.DeclarationIO;
import junit.framework.TestCase;

public class TestDeclarationFunctor extends TestCase {

	private IArgs called;

	public void testPerform() throws Exception {
		DeclarationFunctor wrapper;
		IFunctor functor;
		IArgs args;
		functor = new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				return called = call.getArgs();
			}
		};
		//
		wrapper = new DeclarationFunctor<>(functor);
		args = Args.create();
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 0);
		//
		wrapper = new DeclarationFunctor<>(functor);
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 1);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		//
		wrapper = new DeclarationFunctor<>(functor);
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 2);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "x.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "x.xx.xx.x").equals("3"));
		//
		wrapper = new DeclarationFunctor<>(functor);
		ArgumentDeclaration.declareConstant(wrapper.getDeclarationBlock(), "foo", null, "bar", Object.class);
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 3);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "x.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "x.xx.xx.x").equals("3"));
		assertTrue(ArgTools.getPath(called, "foo").equals("bar"));
		//
		wrapper = new DeclarationFunctor<>(functor);
		ArgumentDeclaration.declareConstant(wrapper.getDeclarationBlock(), ".", null, "x", Object.class);
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 2);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x").equals(""));
		//
		wrapper = new DeclarationFunctor<>(functor);
		ArgumentDeclaration.declareFunctor(wrapper.getDeclarationBlock(), ".", null,
				DeclarationIO.createFunctor("ArgLookup", "x"), Object.class, "");
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 3);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x").equals("1"));
		assertTrue(ArgTools.getPath(called, "xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "xx.xx.x").equals("3"));
		//
		wrapper = new DeclarationFunctor<>(functor);
		ArgumentDeclaration.declareFunctor(wrapper.getDeclarationBlock(), "test", null,
				DeclarationIO.createFunctor("ArgLookup", "x"), Object.class, "");
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 3);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "x.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "x.xx.xx.x").equals("3"));
		assertTrue(ArgTools.getPath(called, "test.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "test.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "test.xx.xx.x").equals("3"));
		//
		wrapper = new DeclarationFunctor<>(functor);
		ArgumentDeclaration.declareFunctor(wrapper.getDeclarationBlock(), "test", null,
				DeclarationIO.createFunctor("ArgLookup", "."), Object.class, "");
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "x.x", "1");
		ArgTools.putPath(args, "x.xx.x", "2");
		ArgTools.putPath(args, "x.xx.xx.x", "3");
		wrapper.perform(new FunctorCall(null, args));
		assertTrue(called.size() == 3);
		assertTrue(ArgTools.getPath(called, "a").equals("b"));
		assertTrue(ArgTools.getPath(called, "x.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "x.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "x.xx.xx.x").equals("3"));
		assertTrue(ArgTools.getPath(called, "test.a").equals("b"));
		assertTrue(ArgTools.getPath(called, "test.x.x").equals("1"));
		assertTrue(ArgTools.getPath(called, "test.x.xx.x").equals("2"));
		assertTrue(ArgTools.getPath(called, "test.x.xx.xx.x").equals("3"));
	}
}
