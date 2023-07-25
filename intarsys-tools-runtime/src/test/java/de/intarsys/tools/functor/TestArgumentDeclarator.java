package de.intarsys.tools.functor;

import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.functor.common.DeclarationIO;
import junit.framework.TestCase;

public class TestArgumentDeclarator extends TestCase {

	public void testDeclarationArgLookupNotStrict() throws Exception {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator().apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x") == null);
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator().apply(declBlock, args);
		assertTrue(args.size() == 3);
		assertTrue(ArgTools.getPath(args, "x").equals("b"));
		assertTrue(ArgTools.getPath(args, "a").equals("b"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a", "b");
		ArgTools.putPath(args, "a.aa", "bb");
		ArgTools.putPath(args, "a.aaa.a", "x");
		ArgTools.putPath(args, "a.aaa.aa", "xx");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator().apply(declBlock, args);
		assertTrue(args.size() == 3);
		assertTrue(ArgTools.getPath(args, "x") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "x.a").equals("b"));
		assertTrue(ArgTools.getPath(args, "x.aa").equals("bb"));
		assertTrue(ArgTools.getPath(args, "x.aaa.a").equals("x"));
		assertTrue(ArgTools.getPath(args, "x.aaa.aa").equals("xx"));
		assertTrue(ArgTools.getPath(args, "a") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "a.a").equals("b"));
		assertTrue(ArgTools.getPath(args, "a.aa").equals("bb"));
		assertTrue(ArgTools.getPath(args, "a.aaa.a").equals("x"));
		assertTrue(ArgTools.getPath(args, "a.aaa.aa").equals("xx"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a", "b");
		ArgTools.putPath(args, "a.aa", "bb");
		ArgTools.putPath(args, "a.aaa.a", "x");
		ArgTools.putPath(args, "a.aaa.aa", "xx");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareFunctor(declBlock, ".", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator().apply(declBlock, args);
		assertTrue(args.size() == 4);
		assertTrue(ArgTools.getPath(args, "a").equals("b"));
		assertTrue(ArgTools.getPath(args, "aa").equals("bb"));
		assertTrue(ArgTools.getPath(args, "aaa.a").equals("x"));
		assertTrue(ArgTools.getPath(args, "aaa.aa").equals("xx"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
	}

	public void testDeclarationArgLookupStrict() throws Exception {
		IArgs args;
		DeclarationBlock declBlock;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x") == null);
		//
		args = Args.create();
		ArgTools.putPath(args, "a", "b");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x").equals("b"));
		assertTrue(ArgTools.getPath(args, "a") == null);
		assertTrue(ArgTools.getPath(args, "foo") == null);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a", "b");
		ArgTools.putPath(args, "a.aa", "bb");
		ArgTools.putPath(args, "a.aaa.a", "x");
		ArgTools.putPath(args, "a.aaa.aa", "xx");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		ArgumentDeclaration.declareFunctor(declBlock, "x", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "x.a").equals("b"));
		assertTrue(ArgTools.getPath(args, "x.aa").equals("bb"));
		assertTrue(ArgTools.getPath(args, "x.aaa.a").equals("x"));
		assertTrue(ArgTools.getPath(args, "x.aaa.aa").equals("xx"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a", "b");
		ArgTools.putPath(args, "a.aa", "bb");
		ArgTools.putPath(args, "a.aaa.a", "x");
		ArgTools.putPath(args, "a.aaa.aa", "xx");
		ArgTools.putPath(args, "foo", "bar");
		declBlock = new DeclarationBlock(null);
		ArgumentDeclaration.declareFunctor(declBlock, ".", null,
				DeclarationIO.createFunctor("ArgLookup", "a"), Object.class, "");
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 3);
		assertTrue(ArgTools.getPath(args, "a").equals("b"));
		assertTrue(ArgTools.getPath(args, "aa").equals("bb"));
		assertTrue(ArgTools.getPath(args, "aaa.a").equals("x"));
		assertTrue(ArgTools.getPath(args, "aaa.aa").equals("xx"));
	}

	public void testDeclarationNestedNotStrict() throws DeclarationException {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, null, Object.class);
		declArg.declare("a-b", null, null, Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a") == null);
		assertTrue(((IArgs) args.get("a")).get("a-b") == null);
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.put("b", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("b").equals("test"));
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add("test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("test"));
		//
		args = Args.create();
		args.put("a", Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add(Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		ArgTools.putPath(args, "a.a-c", "diedel");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 3);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		assertTrue(((IArgs) args.get("a")).get("a-c").equals("diedel"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 1);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, null, Object.class).declare("a-b-a", null, "y", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "b", null, null, Object.class);
		declArg.declare("b-a", null, "i", Object.class);
		declArg.declare("b-b", null, null, Object.class).declare("b-b-a", null, "j", Object.class);
		new ArgumentDeclarator(false, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) ((IArgs) args.get("a")).get("a-b")).get("a-b-a").equals("y"));
		assertTrue(args.get("b") instanceof IArgs);
		assertTrue(((IArgs) args.get("b")).size() == 2);
		assertTrue(((IArgs) args.get("b")).get("b-a").equals("i"));
		assertTrue(((IArgs) ((IArgs) args.get("b")).get("b-b")).get("b-b-a").equals("j"));
	}

	public void testDeclarationNestedStrictLazy() throws DeclarationException {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, null, Object.class);
		declArg.declare("a-b", null, null, Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a") == null);
		assertTrue(((IArgs) args.get("a")).get("a-b") == null);
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.put("b", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("b") == null);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add("test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("test"));
		//
		args = Args.create();
		args.put("a", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("test"));
		//
		args = Args.create();
		args.put("a", Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add(Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		ArgTools.putPath(args, "a.a-c", "diedel");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		assertTrue(((IArgs) args.get("a")).get("a-c") == null);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 1);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, null, Object.class).declare("a-b-a", null, "y", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "b", null, null, Object.class);
		declArg.declare("b-a", null, "i", Object.class);
		declArg.declare("b-b", null, null, Object.class).declare("b-b-a", null, "j", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) ((IArgs) args.get("a")).get("a-b")).get("a-b-a").equals("y"));
		assertTrue(args.get("b") instanceof IArgs);
		assertTrue(((IArgs) args.get("b")).size() == 2);
		assertTrue(((IArgs) args.get("b")).get("b-a").equals("i"));
		assertTrue(((IArgs) ((IArgs) args.get("b")).get("b-b")).get("b-b-a").equals("j"));
	}

	public void testDeclarationNestedStrictNotLazy() throws DeclarationException {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, null, Object.class);
		declArg.declare("a-b", null, null, Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a") == null);
		assertTrue(((IArgs) args.get("a")).get("a-b") == null);
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.put("b", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("b") == null);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add("test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("test"));
		//
		args = Args.create();
		args.put("a", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("test"));
		//
		args = Args.create();
		args.put("a", Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		args.add(Args.create());
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		ArgTools.putPath(args, "a.a-c", "diedel");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, "y", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("test"));
		assertTrue(((IArgs) args.get("a")).get("a-b").equals("y"));
		assertTrue(((IArgs) args.get("a")).get("a-c") == null);
		//
		args = Args.create();
		ArgTools.putPath(args, "a.a-a", "test");
		declBlock = new DeclarationBlock(null);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 0);
		assertTrue(args.get("a") == null);
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "a", null, null, Object.class);
		declArg.declare("a-a", null, "x", Object.class);
		declArg.declare("a-b", null, null, Object.class).declare("a-b-a", null, "y", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "b", null, null, Object.class);
		declArg.declare("b-a", null, "i", Object.class);
		declArg.declare("b-b", null, null, Object.class).declare("b-b-a", null, "j", Object.class);
		new ArgumentDeclarator(true, false).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("a") instanceof IArgs);
		assertTrue(((IArgs) args.get("a")).size() == 2);
		assertTrue(((IArgs) args.get("a")).get("a-a").equals("x"));
		assertTrue(((IArgs) ((IArgs) args.get("a")).get("a-b")).get("a-b-a").equals("y"));
		assertTrue(args.get("b") instanceof IArgs);
		assertTrue(((IArgs) args.get("b")).size() == 2);
		assertTrue(((IArgs) args.get("b")).get("b-a").equals("i"));
		assertTrue(((IArgs) ((IArgs) args.get("b")).get("b-b")).get("b-b-a").equals("j"));
	}

	public void testDeclarationNotStrict() throws DeclarationException {
		IArgs args;
		DeclarationBlock declaration;
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 0);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, null, Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("a") == null);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("x"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("q", "w");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		// assertTrue(args.size() == 2);
		assertTrue(args.get("q").equals("w"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("a", "y");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		args.add("z");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("a").equals("y"));
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get(1).equals("z"));
		//
		//
		args = Args.create();
		args.put("a", "y");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(false, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		//
	}

	public void testDeclarationSetAll() throws DeclarationException {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		// ugly corner case
		// args = Args.create();
		// args.put("a", "b");
		// declBlock = new DeclarationBlock(null);
		// declArg = ArgumentDeclaration.declare(declBlock, ".", null,
		// Args.create(), Object.class);
		// new ArgumentDeclarator(true, true).apply(declBlock, args);
		// assertTrue(args.size() == 0);
		//
		args = Args.create();
		args.put("a", "b");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, ".", null, Args.createNamed("x", "y", "foo", "bar"),
				Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(ArgTools.getPath(args, "x").equals("y"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
		//
		args = Args.create();
		args.put("a", "b");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, ".", null, "x=y; foo=bar", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(ArgTools.getPath(args, "x").equals("y"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
		//
		args = Args.create();
		args.put("a", "b");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, ".", null,
				Args.createNamed("x", "y", "foo", "bar", "nested", Args.createNamed("gnu", "gnat")), Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 3);
		assertTrue(ArgTools.getPath(args, "x").equals("y"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
		assertTrue(ArgTools.getPath(args, "nested") instanceof IArgs);
		assertTrue(ArgTools.getPath(args, "nested.gnu").equals("gnat"));
		//
		args = Args.create();
		args.put("a", "b");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, ".", null, "x=y; foo=bar", Object.class);
		new ArgumentDeclarator(true, true).apply(declBlock, args);
		assertTrue(args.size() == 2);
		assertTrue(ArgTools.getPath(args, "x").equals("y"));
		assertTrue(ArgTools.getPath(args, "foo").equals("bar"));
	}

	public void testDeclarationStrictLazy() throws DeclarationException {
		IArgs args;
		DeclarationBlock declaration;
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 0);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, null, Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("a") == null);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("x"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("q", "w");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		// assertTrue(args.size() == 2);
		assertTrue(!args.isDefined("q"));
		assertTrue(!args.isDefined("foo"));
		assertTrue(args.get(0).equals("x"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("a", "y");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(!args.isDefined("foo"));
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		args.add("z");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("y"));
		assertTrue(args.get(0).equals("y"));
		//
		args = Args.create();
		args.put("a", "y");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 2);
		assertTrue(args.get("a").equals("y"));
		assertTrue(args.get("foo").equals("bar"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, true).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		//
	}

	public void testDeclarationStrictNotLazy() throws DeclarationException {
		IArgs args;
		DeclarationBlock declaration;
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 0);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, null, Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0) == null);
		assertTrue(args.get("a") == null);
		//
		args = Args.create();
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("x"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("q", "w");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		// assertTrue(args.size() == 2);
		assertTrue(!args.isDefined("q"));
		assertTrue(!args.isDefined("foo"));
		assertTrue(args.get(0).equals("x"));
		assertTrue(args.get("a").equals("x"));
		//
		args = Args.create();
		args.put("a", "y");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(!args.isDefined("foo"));
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get(0).equals("y"));
		assertTrue(args.get("a").equals("y"));
		//
		args = Args.create();
		args.add("y");
		args.add("z");
		declaration = new DeclarationBlock(null);
		declaration.addDeclarationElement(new ArgumentDeclaration(null, "a", null, "x", Object.class));
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 1);
		assertTrue(args.get("a").equals("y"));
		assertTrue(args.get(0).equals("y"));
		//
		//
		args = Args.create();
		args.put("a", "y");
		args.put("foo", "bar");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 0);
		assertTrue(!args.isDefined("foo"));
		assertTrue(!args.isDefined("a"));
		//
		args = Args.create();
		args.add("y");
		declaration = new DeclarationBlock(null);
		new ArgumentDeclarator(true, false).apply(declaration, args);
		assertTrue(args.size() == 0);
		//
	}

	public void testInstanceSpecLegacy() throws Exception {
		IArgs args;
		DeclarationBlock declBlock;
		ArgumentDeclaration declArg;
		InstanceSpec spec;
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "xArgs", null, null, Object.class);
		ArgumentDeclaration.declareConstant(declArg.getDeclarationBlock(), "foo", null, "bar", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "x", null, "de.intarsys.gnu.Gnat", Object.class);
		new ArgumentDeclarator().apply(declBlock, args);
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("bar"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Gnat"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("bar"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Gnat"));
		//
		args = Args.create();
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "x", null, "de.intarsys.gnu.Gnat", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "xArgs", null, null, Object.class);
		ArgumentDeclaration.declareConstant(declArg.getDeclarationBlock(), "foo", null, "bar", Object.class);
		new ArgumentDeclarator().apply(declBlock, args);
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("bar"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Gnat"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("bar"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Gnat"));
		//
		args = Args.create();
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "x", null, "de.intarsys.gnu.Gnat", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "xArgs", null, null, Object.class);
		ArgumentDeclaration.declareConstant(declArg.getDeclarationBlock(), "foo", null, "bar", Object.class);
		new ArgumentDeclarator().apply(declBlock, args);
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
		//
		args = Args.create();
		ArgTools.putPath(args, "xArgs.foo", "diedel");
		ArgTools.putPath(args, "x", "de.intarsys.gnu.Doedel");
		declBlock = new DeclarationBlock(null);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "xArgs", null, null, Object.class);
		ArgumentDeclaration.declareConstant(declArg.getDeclarationBlock(), "foo", null, "bar", Object.class);
		declArg = ArgumentDeclaration.declareConstant(declBlock, "x", null, "de.intarsys.gnu.Gnat", Object.class);
		new ArgumentDeclarator().apply(declBlock, args);
		spec = InstanceSpec.get(args, "x", Object.class, null);
		assertTrue(args.size() == 1);
		assertTrue(ArgTools.getPath(args, "x.args.foo").equals("diedel"));
		assertTrue(ArgTools.getPath(args, "x.factory").equals("de.intarsys.gnu.Doedel"));
		assertTrue(ArgTools.getPath(spec.getArgs(), "foo").equals("diedel"));
		assertTrue(spec.getFactory().equals("de.intarsys.gnu.Doedel"));
	}
}
