package de.intarsys.tools.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.servicelocator.ServiceLocator;
import de.intarsys.tools.ui.IToolkit;

/**
 * Test the {@link IActivityHandler} behavior for the
 * {@link RequestConfirmation}.
 *
 */
public class TestRequestConfirmation {

	public static class DummyActivityHandler implements IActivityHandler {
		public RequestConfirmation requester;

		@Override
		public <R> void activityEnter(IActivity<R> activity) {
			this.requester = (RequestConfirmation) activity;
		}
	}

	@Before
	public void setup() {
	}

	@Test
	public void testDummyToolkitWithDummyHandler() {
		RequestConfirmation requester;
		IMessage[] options;
		//
		ServiceLocator.get().put(IToolkit.class, new DummyToolkit() {
			@Override
			protected String getToolkitID() {
				return "dummy";
			}
		});
		options = RequestConfirmation.OPTIONS_OK;
		requester = new RequestConfirmation(null, options);
		requester.enter();
		Assert.assertTrue(requester.isDone());
	}

	@Test
	public void testDummyToolkitWithHandlerLock() {
		RequestConfirmation requester;
		IMessage[] options;
		DummyActivityHandler handler;
		//
		handler = new DummyActivityHandler();
		ServiceLocator.get().put(IToolkit.class, new DummyToolkit() {
			@Override
			public IActivityHandler createToolkitObject(Class clazz, Class expectedType, String suffix) {
				return handler;
			}
		});
		options = RequestConfirmation.OPTIONS_OK;
		requester = new RequestConfirmation(null, options);
		requester.enter();
		Assert.assertTrue(!requester.isDone());
		handler.requester.setSelection(null);
		handler.requester.ok();
		Assert.assertTrue(requester.isDone());
	}

	@Test
	public void testDummyToolkitWithHandlerLockSilent() {
		RequestConfirmation requester;
		IMessage[] options;
		//
		IToolkit tk = new DummyToolkit();
		tk.setSilent(true);
		ServiceLocator.get().put(IToolkit.class, tk);
		options = RequestConfirmation.OPTIONS_OK;
		requester = new RequestConfirmation(null, options);
		requester.enter();
		Assert.assertTrue(requester.isDone());
	}

	@Test
	public void testDummyToolkitWithoutHandler() {
		RequestConfirmation requester;
		IMessage[] options;
		//
		ServiceLocator.get().put(IToolkit.class, new DummyToolkit());
		options = RequestConfirmation.OPTIONS_OK;
		requester = new RequestConfirmation(null, options);
		requester.enter();
		Assert.assertTrue(!requester.isDone());
	}

}
