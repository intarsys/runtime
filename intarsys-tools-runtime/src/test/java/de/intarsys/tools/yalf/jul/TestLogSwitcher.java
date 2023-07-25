package de.intarsys.tools.yalf.jul;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.yalf.api.IHandlerFactory;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Yalf;
import de.intarsys.tools.yalf.common.LogSwitcher;
import de.intarsys.tools.yalf.handler.IMemoryHandler;

public class TestLogSwitcher {

	@Test
	public void detachedHandler() throws InterruptedException {
		IHandlerFactory factory;
		final LogSwitcher switcher;
		String content;
		final ILogger logger1;
		final ILogger logger2;

		//
		factory = new JulMemoryHandlerFactory();
		factory.setPattern("[%p][%c] %m\r\n");
		switcher = new LogSwitcher();
		switcher.setHandlerFactory(factory);
		switcher.setHandlerPerAttach(false);
		switcher.setLoggerName("test");

		logger1 = Yalf.get().getLogger("test");
		logger2 = Yalf.get().getLogger("test.child");
		logger2.addHandler(switcher.getHandlerDetached());

		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);

		final Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				logger1.warn("t1");
				switcher.attach();
				logger1.warn("t2");
				logger2.warn("t3");
				s1.release();
				s2.acquireUninterruptibly();
				switcher.detach();
				logger1.warn("t4");
			}
		});
		t1.start();

		s1.acquireUninterruptibly();
		logger1.warn("l11");
		logger2.warn("l21");
		s2.release();
		logger1.warn("l12");
		logger2.warn("l22");

		t1.join();
		content = ((IMemoryHandler) switcher.getHandler()).getString();
		switcher.close();
		Assert.assertEquals(
				"[WARNING][test                          ] t2[WARNING][test.child                    ] t3[WARNING][test.child                    ] l21[WARNING][test.child                    ] l22",
				content.replace("\r", "").replace("\n", ""));
	}

	@Test
	public void multiThreadedUniqueHandler() throws InterruptedException {
		IHandlerFactory factory;
		final LogSwitcher switcher;
		String content;
		//
		factory = new JulMemoryHandlerFactory();
		factory.setPattern("[%p][%c] %m\r\n");
		switcher = new LogSwitcher();
		switcher.setHandlerFactory(factory);
		switcher.setHandlerPerAttach(false);
		switcher.setLoggerName("test");

		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);

		final Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				ILogger logger;
				String content;

				logger = Yalf.get().getLogger("test");
				logger.warn("t11");
				switcher.attach();
				logger.warn("t12");
				s1.release();
				s2.acquireUninterruptibly();
				switcher.detach();
				logger.warn("t13");
			}
		});
		t1.start();
		final Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				ILogger logger;

				logger = Yalf.get().getLogger("test");
				logger.warn("t21");
				s1.acquireUninterruptibly();
				switcher.attach();
				logger.warn("t22");
				s2.release();
				switcher.detach();
				logger.warn("t23");
			}
		});
		t2.start();
		t1.join();
		t2.join();
		content = ((IMemoryHandler) switcher.getHandler()).getString();
		switcher.close();
		Assert.assertEquals(
				"[WARNING][test                          ] t12[WARNING][test                          ] t22",
				content.replace("\r", "").replace("\n", ""));
	}

	@Test
	public void singleThreadedUniqueHandler() throws IOException {
		IHandlerFactory factory;
		LogSwitcher switcher;
		ILogger logger;
		String content;
		//
		factory = new JulMemoryHandlerFactory();
		factory.setPattern("[%p][%c] %m\r\n");
		switcher = new LogSwitcher();
		switcher.setHandlerFactory(factory);
		switcher.setHandlerPerAttach(false);
		switcher.setLoggerName("test");

		// log not attached
		switcher.open();
		logger = Yalf.get().getLogger("test");
		logger.warn("hello");
		content = ((IMemoryHandler) switcher.getHandler()).getString();
		switcher.close();
		Assert.assertEquals("", content);

		// log attached
		switcher.attach();
		logger = Yalf.get().getLogger("test");
		logger.warn("hello");
		content = ((IMemoryHandler) switcher.getHandler()).getString();
		switcher.detach();
		switcher.close();
		Assert.assertEquals("[WARNING][test                          ] hello", content.replace("\r", "").replace("\n",
				""));

		// log attached/detached
		logger = Yalf.get().getLogger("test");
		switcher.attach();
		logger.warn("1");
		switcher.detach();
		logger.warn("2");
		switcher.attach();
		logger.warn("3");
		content = ((IMemoryHandler) switcher.getHandler()).getString();
		switcher.detach();
		switcher.close();
		Assert.assertEquals(
				"[WARNING][test                          ] 1[WARNING][test                          ] 3",
				content.replace("\r", "").replace("\n", ""));
	}

}
