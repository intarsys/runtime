package de.intarsys.tools.expression;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import junit.framework.TestCase;

public class TestThreadContextAwareResolver extends TestCase {

	public void testEvaluate() throws EvaluationException, InterruptedException, ExecutionException {
		String expression;
		Object result;
		IArgs args;
		MapResolver mapResolver;
		//
		try {
			expression = "foo";
			args = Args.create();
			result = new ThreadContextAwareResolver().evaluate(expression, args);
			fail();
		} catch (Exception e) {
			//
		}
		//
		expression = "";
		args = Args.create();
		mapResolver = ThreadContextAwareResolver.attachMapResolver();
		mapResolver.put("foo", "bar");
		try {
			result = new ThreadContextAwareResolver().evaluate(expression, args);
			fail();
		} catch (Exception e) {
			//
		} finally {
			ThreadContextAwareResolver.detach(mapResolver);
		}
		//
		expression = "gnu";
		args = Args.create();
		mapResolver = ThreadContextAwareResolver.attachMapResolver();
		mapResolver.put("gnu", "gnat");
		try {
			result = new ThreadContextAwareResolver().evaluate(expression, args);
			assertTrue("gnat".equals(result));
		} finally {
			ThreadContextAwareResolver.detach(mapResolver);
			try {
				result = new ThreadContextAwareResolver().evaluate(expression, args);
				fail();
			} catch (Exception e) {
				//
			}
		}
		//
		FutureTask future = new FutureTask<>(new Callable() {
			@Override
			public Object call() throws Exception {
				String expression = "diedel";
				IArgs args = Args.create();
				Object result;
				MapResolver mapResolver = ThreadContextAwareResolver.attachMapResolver();
				try {
					mapResolver.put("diedel", "doedel");
					result = new ThreadContextAwareResolver().evaluate(expression, args);
					assertTrue("doedel".equals(result));
				} finally {
					ThreadContextAwareResolver.detach(mapResolver);
					try {
						result = new ThreadContextAwareResolver().evaluate(expression, args);
						fail();
					} catch (Exception e) {
						//
					}
				}
				return result;
			}
		});
		expression = "diedel";
		args = Args.create();
		try {
			result = new ThreadContextAwareResolver().evaluate(expression, args);
			fail();
		} catch (Exception e) {
			//
		}
		Thread t = new Thread(future);
		t.start();
		future.get();
		expression = "diedel";
		args = Args.create();
		try {
			result = new ThreadContextAwareResolver().evaluate(expression, args);
			fail();
		} catch (Exception e) {
			//
		}
	}
}
