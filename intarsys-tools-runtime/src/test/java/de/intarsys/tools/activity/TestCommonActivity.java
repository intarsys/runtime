package de.intarsys.tools.activity;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.concurrent.ITaskCallback;
import de.intarsys.tools.concurrent.TaskFailed;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.LiteralMessageBundle;
import de.intarsys.tools.state.AtomicState;

public class TestCommonActivity {

	class Activity extends CommonActivity {

		public Activity() {
			super();
		}

		public Activity(CommonActivity parent) {
			super(parent);
		}

		@Override
		protected IMessageBundle getDefaultMessageBundle() {
			return new LiteralMessageBundle();
		}

	}

	@Test
	public void testCallbackOrderCancel() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.addTaskCallback(new ITaskCallback() {
			@Override
			public void failed(TaskFailed exception) {
				Assert.assertTrue(a.isDone());
				Assert.assertTrue(AtomicState.CANCELLED.isAncestorOf(a.getState()));
			}

			@Override
			public void finished(Object result) {
				Assert.fail();
			}
		});
		a.enter();
		a.cancel(false);
	}

	@Test
	public void testCallbackOrderFail() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.addTaskCallback(new ITaskCallback() {
			@Override
			public void failed(TaskFailed exception) {
				Assert.assertTrue(a.isDone());
				Assert.assertTrue(AtomicState.FAILED.isAncestorOf(a.getState()));
			}

			@Override
			public void finished(Object result) {
				Assert.fail();
			}
		});
		a.enter();
		a.fail(new RuntimeException());
	}

	@Test
	public void testCallbackOrderFinish() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.addTaskCallback(new ITaskCallback() {
			@Override
			public void failed(TaskFailed exception) {
				Assert.fail();
			}

			@Override
			public void finished(Object result) {
				Assert.assertTrue(a.isDone());
				Assert.assertTrue(AtomicState.OK.isAncestorOf(a.getState()));
			}
		});
		a.enter();
		a.finish(null);
	}

	@Test
	public void testDeferCancel() throws Exception {
		CommonActivity a;
		//
		a = new Activity();
		a.deferredCancel();
		Assert.assertTrue(!a.isDone());
		a.deferredRelease();
		Assert.assertTrue(a.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof CancellationException);
		}
	}

	@Test
	public void testDeferFail() throws Exception {
		CommonActivity a;
		Exception ex;
		//
		a = new Activity();
		ex = new RuntimeException();
		a.deferredFail(ex);
		Assert.assertTrue(!a.isDone());
		a.deferredRelease();
		Assert.assertTrue(a.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getCause() == ex);
		}
	}

	@Test
	public void testDeferFinish() throws Exception {
		CommonActivity a;
		Object result;
		//
		a = new Activity();
		result = new Object();
		a.deferredFinish(result);
		Assert.assertTrue(!a.isDone());
		a.deferredRelease();
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(a.get() == result);
	}

	@Test
	public void testLifecycleCancel() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.enter();
		a.cancel(false);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(a.isCancelled());
		try {
			a.get();
			Assert.fail();
		} catch (CancellationException e) {
			//
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testLifecycleEnter() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.enter();
		Assert.assertTrue(!a.isDone());
		Assert.assertTrue(!a.isCancelled());
		try {
			a.get(100, TimeUnit.MILLISECONDS);
			Assert.fail();
		} catch (TimeoutException e) {
			//
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testLifecycleFail() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		Exception ex;
		//
		a = new Activity();
		a.enter();
		ex = new RuntimeException();
		a.fail(ex);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(!a.isCancelled());
		try {
			a.get();
			Assert.fail();
		} catch (ExecutionException e) {
			Assert.assertTrue(e.getCause() == ex);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testLifecycleFinish() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.enter();
		result = new Object();
		a.finish(result);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(!a.isCancelled());
		Assert.assertTrue(a.get() == result);
	}

	@Test
	public void testLifecycleOK() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final Object result;
		//
		a = new Activity();
		a.enter();
		a.ok();
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(!a.isCancelled());
		Assert.assertTrue(a.get() == null);
	}

	@Test
	public void testPropagateDownCancel() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final CommonActivity b;
		final Object result;
		//
		a = new Activity();
		b = new Activity(a);
		b.addTaskCallback(new ITaskCallback() {

			@Override
			public void failed(TaskFailed exception) {
				a.cancel(false);
			}

			@Override
			public void finished(Object result) {
				a.finish(result);
			}
		});
		a.enter();
		b.enter();
		Assert.assertTrue(a.getChildren().size() == 1);
		a.cancel(false);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(b.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof CancellationException);
		}
		Assert.assertTrue(a.getChildren().size() == 0);
	}

	@Test
	public void testPropagateDownFail() {
		final CommonActivity a;
		final CommonActivity b;
		//
		a = new Activity();
		b = new Activity(a);
		b.addTaskCallback(new ITaskCallback() {

			@Override
			public void failed(TaskFailed exception) {
				a.fail(exception.getCause());
			}

			@Override
			public void finished(Object result) {
				a.finish(result);
			}
		});
		a.enter();
		b.enter();
		Assert.assertTrue(a.getChildren().size() == 1);
		a.fail(new RuntimeException());
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(b.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getCause() instanceof RuntimeException);
		}
		Assert.assertTrue(a.getChildren().size() == 0);
	}

	@Test
	public void testPropagateUpCancel() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final CommonActivity b;
		final Object result;
		//
		a = new Activity();
		b = new Activity(a);
		b.addTaskCallback(new ITaskCallback() {

			@Override
			public void failed(TaskFailed exception) {
				a.cancel(false);
			}

			@Override
			public void finished(Object result) {
				a.finish(result);
			}
		});
		a.enter();
		b.enter();
		Assert.assertTrue(a.getChildren().size() == 1);
		b.cancel(false);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(b.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof CancellationException);
		}
		Assert.assertTrue(a.getChildren().size() == 0);
	}

	@Test
	public void testPropagateUpFail() {
		final CommonActivity a;
		final CommonActivity b;
		//
		a = new Activity();
		b = new Activity(a);
		b.addTaskCallback(new ITaskCallback() {

			@Override
			public void failed(TaskFailed exception) {
				a.fail(exception.getCause());
			}

			@Override
			public void finished(Object result) {
				a.finish(result);
			}
		});
		a.enter();
		b.enter();
		Assert.assertTrue(a.getChildren().size() == 1);
		b.fail(new RuntimeException());
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(b.isDone());
		try {
			a.get();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getCause() instanceof RuntimeException);
		}
		Assert.assertTrue(a.getChildren().size() == 0);
	}

	@Test
	public void testPropagateUpFinish() throws InterruptedException, ExecutionException {
		final CommonActivity a;
		final CommonActivity b;
		final Object result;
		//
		a = new Activity();
		b = new Activity(a);
		b.addTaskCallback(new ITaskCallback() {

			@Override
			public void failed(TaskFailed exception) {
				a.fail(exception);
			}

			@Override
			public void finished(Object result) {
				a.finish(result);
			}
		});
		a.enter();
		b.enter();
		Assert.assertTrue(a.getChildren().size() == 1);
		result = new Object();
		b.finish(result);
		Assert.assertTrue(a.isDone());
		Assert.assertTrue(b.isDone());
		Assert.assertTrue(a.get() == result);
		Assert.assertTrue(a.getChildren().size() == 0);
	}
}
