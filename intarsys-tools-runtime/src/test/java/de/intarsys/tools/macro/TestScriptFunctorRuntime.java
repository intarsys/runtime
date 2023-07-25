package de.intarsys.tools.macro;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import junit.framework.TestCase;

public class TestScriptFunctorRuntime extends TestCase {

	class TestFunctor implements IFunctor {
		private Object result;
		private boolean executed;

		public TestFunctor(Object result) {
			this.result = result;
		}

		public Object getResult() {
			return result;
		}

		public boolean isExecuted() {
			return executed;
		}

		@Override
		public Object perform(IFunctorCall call) throws FunctorException {
			executed = true;
			return result;
		}

	}

	public void testBlockAssign() throws Exception {
		MacroBlock block;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		IArgs args;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue(result == null);
		assertTrue(args.size() == 0);
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("b1".equals(result));
		assertTrue(args.size() == 1);
		assertTrue(args.get("rb1").equals("b1"));
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("b3".equals(result));
		assertTrue(args.size() == 3);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2").equals("b2"));
		assertTrue(args.get("rb3").equals("b3"));
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				String result = (String) call.getArgs().get("rb1") + (String) call.getArgs().get("rb2");
				return result;
			}
		};
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("b1b2".equals(result));
		assertTrue(args.size() == 3);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2").equals("b2"));
		assertTrue(args.get("rb3").equals("b1b2"));
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				String result = (String) call.getArgs().get("rb1") + (String) call.getArgs().get("rb2");
				return result;
			}
		};
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		block.addFinallyExpression(new MacroAssign(f1, "rf1"));
		block.addFinallyExpression(new MacroAssign(f2, "rf2"));
		block.addFinallyExpression(new MacroAssign(f3, "rf3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("b1b2".equals(result));
		assertTrue(args.size() == 6);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2").equals("b2"));
		assertTrue(args.get("rb3").equals("b1b2"));
		assertTrue(args.get("rf1").equals("f1"));
		assertTrue(args.get("rf2").equals("f2"));
		assertTrue(args.get("rf3").equals("f3"));
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new RuntimeException();
			}
		};
		b3 = new TestFunctor("b3") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				String result = (String) call.getArgs().get("rb1") + (String) call.getArgs().get("rb2");
				return result;
			}
		};
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		block.addFinallyExpression(new MacroAssign(f1, "rf1"));
		block.addFinallyExpression(new MacroAssign(f2, "rf2"));
		block.addFinallyExpression(new MacroAssign(f3, "rf3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		try {
			result = null;
			result = block.perform(call);
			fail();
		} catch (Exception e) {
			//
		}
		assertTrue(result == null);
		assertTrue(args.size() == 4);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2") == null);
		assertTrue(args.get("rb3") == null);
		assertTrue(args.get("rf1").equals("f1"));
		assertTrue(args.get("rf2").equals("f2"));
		assertTrue(args.get("rf3").equals("f3"));
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new RuntimeException();
			}
		};
		b3 = new TestFunctor("b3") {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				String result = (String) call.getArgs().get("rb1") + (String) call.getArgs().get("rb2");
				return result;
			}
		};
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		block.addErrorExpression(new MacroAssign(e1, "re1"));
		block.addErrorExpression(new MacroAssign(e2, "re2"));
		block.addErrorExpression(new MacroAssign(e3, "re3"));
		block.addFinallyExpression(new MacroAssign(f1, "rf1"));
		block.addFinallyExpression(new MacroAssign(f2, "rf2"));
		block.addFinallyExpression(new MacroAssign(f3, "rf3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("e3".equals(result));
		assertTrue(args.size() == 7);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2") == null);
		assertTrue(args.get("rb3") == null);
		assertTrue(args.get("re1").equals("e1"));
		assertTrue(args.get("re2").equals("e2"));
		assertTrue(args.get("re3").equals("e3"));
		assertTrue(args.get("rf1").equals("f1"));
		assertTrue(args.get("rf2").equals("f2"));
		assertTrue(args.get("rf3").equals("f3"));
	}

	public void testBlockAssignNested() throws Exception {
		MacroBlock block;
		MacroBlock innerBlock;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor innerb1;
		TestFunctor innerb2;
		TestFunctor innerb3;
		IArgs args;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		{
			innerb1 = new TestFunctor("innerb1");
			innerb2 = new TestFunctor("innerb2");
			innerb3 = new TestFunctor("innerb3");
			innerBlock = new MacroBlock();
			innerBlock.addBlockExpression(new MacroAssign(innerb1, "rinnerb1"));
			innerBlock.addBlockExpression(new MacroAssign(innerb2, "rinnerb2"));
			innerBlock.addBlockExpression(new MacroAssign(innerb3, "rinnerb3"));
		}
		block.addBlockExpression(innerBlock);
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		args = Args.create();
		call = new FunctorCall(null, args);
		result = block.perform(call);
		assertTrue("b3".equals(result));
		assertTrue(args.size() == 6);
		assertTrue(args.get("rb1").equals("b1"));
		assertTrue(args.get("rb2").equals("b2"));
		assertTrue(args.get("rb3").equals("b3"));
		assertTrue(args.get("rinnerb1").equals("innerb1"));
		assertTrue(args.get("rinnerb2").equals("innerb2"));
		assertTrue(args.get("rinnerb3").equals("innerb3"));
	}

	public void testBlockError() throws Exception {
		MacroBlock block;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new FunctorExecutionException(new RuntimeException("error"));
			}
		});
		block.addBlockExpression(b3);
		block.addErrorExpression(e1);
		block.addErrorExpression(e2);
		block.addErrorExpression(e3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue("e3".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(!b2.isExecuted());
		assertTrue(!b3.isExecuted());
		assertTrue(e1.isExecuted());
		assertTrue(e2.isExecuted());
		assertTrue(e3.isExecuted());
		assertTrue(f1.isExecuted());
		assertTrue(f2.isExecuted());
		assertTrue(f3.isExecuted());
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new FunctorExecutionException(new RuntimeException("error"));
			}
		});
		block.addBlockExpression(b3);
		block.addErrorExpression(e1);
		block.addErrorExpression(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new FunctorExecutionException(new RuntimeException("inner"));
			}
		});
		block.addErrorExpression(e3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		call = new FunctorCall(null, Args.create());
		try {
			result = block.perform(call);
			fail("must fail");
		} catch (Exception e) {
			assertTrue("inner".equals(e.getMessage()));
		}
		assertTrue(b1.isExecuted());
		assertTrue(!b2.isExecuted());
		assertTrue(!b3.isExecuted());
		assertTrue(e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(f1.isExecuted());
		assertTrue(f2.isExecuted());
		assertTrue(f3.isExecuted());
	}

	public void testBlockFinally() throws Exception {
		MacroBlock block;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(b2);
		block.addBlockExpression(b3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue("b3".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(b2.isExecuted());
		assertTrue(b3.isExecuted());
		assertTrue(!e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(f1.isExecuted());
		assertTrue(f2.isExecuted());
		assertTrue(f3.isExecuted());
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(new IFunctor() {
			@Override
			public Object perform(IFunctorCall call) throws FunctorException {
				throw new FunctorExecutionException(new RuntimeException("error"));
			}
		});
		block.addBlockExpression(b3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		call = new FunctorCall(null, Args.create());
		try {
			result = block.perform(call);
			fail("must fail");
		} catch (Exception e) {
			assertTrue("error".equals(e.getMessage()));
		}
		assertTrue(b1.isExecuted());
		assertTrue(!b2.isExecuted());
		assertTrue(!b3.isExecuted());
		assertTrue(!e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(f1.isExecuted());
		assertTrue(f2.isExecuted());
		assertTrue(f3.isExecuted());
	}

	public void testBlockResult() throws Exception {
		MacroBlock block;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue(result == null);
		assertTrue(!b1.isExecuted());
		assertTrue(!b2.isExecuted());
		assertTrue(!b3.isExecuted());
		assertTrue(!e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(!f1.isExecuted());
		assertTrue(!f2.isExecuted());
		assertTrue(!f3.isExecuted());
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue("b1".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(!b2.isExecuted());
		assertTrue(!b3.isExecuted());
		assertTrue(!e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(!f1.isExecuted());
		assertTrue(!f2.isExecuted());
		assertTrue(!f3.isExecuted());
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(b2);
		block.addBlockExpression(b3);
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue("b3".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(b2.isExecuted());
		assertTrue(b3.isExecuted());
		assertTrue(!e1.isExecuted());
		assertTrue(!e2.isExecuted());
		assertTrue(!e3.isExecuted());
		assertTrue(!f1.isExecuted());
		assertTrue(!f2.isExecuted());
		assertTrue(!f3.isExecuted());
	}

	public void testCondition() throws Exception {
		MacroCondition cond;
		IFunctorCall call;
		Object result;
		TestFunctor fIf;
		TestFunctor fThen;
		TestFunctor fElse;
		IArgs args;
		//
		fIf = new TestFunctor("true");
		fThen = new TestFunctor("then");
		fElse = new TestFunctor("else");
		cond = new MacroCondition();
		cond.setIfExpression(fIf);
		cond.setThenExpression(fThen);
		cond.setElseExpression(fElse);
		args = Args.create();
		call = new FunctorCall(null, args);
		result = cond.perform(call);
		assertTrue("then".equals(result));
		assertTrue(fIf.isExecuted());
		assertTrue(fThen.isExecuted());
		assertTrue(!fElse.isExecuted());
		//
		fIf = new TestFunctor("false");
		fThen = new TestFunctor("then");
		fElse = new TestFunctor("else");
		cond = new MacroCondition();
		cond.setIfExpression(fIf);
		cond.setThenExpression(fThen);
		cond.setElseExpression(fElse);
		args = Args.create();
		call = new FunctorCall(null, args);
		result = cond.perform(call);
		assertTrue("else".equals(result));
		assertTrue(fIf.isExecuted());
		assertTrue(!fThen.isExecuted());
		assertTrue(fElse.isExecuted());
	}

	public void testReturn() throws Exception {
		MacroBlock block;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		//
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(new MacroReturn(b2));
		block.addBlockExpression(b3);
		call = new FunctorCall(null, Args.create());
		result = block.perform(call);
		assertTrue("b2".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(b2.isExecuted());
		assertTrue(!b3.isExecuted());
	}

	public void testReturnNested() throws Exception {
		MacroBlock outerBlock;
		MacroBlock innerBlock;
		IFunctorCall call;
		Object result;
		TestFunctor b1;
		TestFunctor b3;
		TestFunctor innerb1;
		TestFunctor innerb2;
		TestFunctor innerb3;
		//
		b1 = new TestFunctor("b1");
		b3 = new TestFunctor("b3");
		innerb1 = new TestFunctor("innerb1");
		innerb2 = new TestFunctor("innerb2");
		innerb3 = new TestFunctor("innerb3");
		outerBlock = new MacroBlock();
		outerBlock.addBlockExpression(b1);
		{
			innerBlock = new MacroBlock();
			innerBlock.addBlockExpression(innerb1);
			innerBlock.addBlockExpression(new MacroReturn(innerb2));
			innerBlock.addBlockExpression(innerb3);
		}
		outerBlock.addBlockExpression(innerBlock);
		outerBlock.addBlockExpression(b3);
		call = new FunctorCall(null, Args.create());
		result = outerBlock.perform(call);
		assertTrue("innerb2".equals(result));
		assertTrue(b1.isExecuted());
		assertTrue(innerb1.isExecuted());
		assertTrue(innerb2.isExecuted());
		assertTrue(!innerb3.isExecuted());
		assertTrue(!b3.isExecuted());
	}
}
