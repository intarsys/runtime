package de.intarsys.tools.yalf.jul;

import java.io.IOException;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.yalf.api.IFilter;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.api.Yalf;
import de.intarsys.tools.yalf.handler.IMemoryHandler;
import de.intarsys.tools.yalf.handler.IMemoryHandlerFactory;

public class TestMemoryHandler {

	@Test
	public void create() throws IOException {
		IMemoryHandlerFactory factory;

		// create handler using defaults
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.createHandler();

		// create handler with parameters
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.setSize(1000);
		factory.createHandler();
	}

	@Test
	public void logFilter() throws IOException {
		IMemoryHandlerFactory factory;
		IMemoryHandler handler;
		ILogger logger;
		String content;

		// log to file
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.setPattern("[%p][%c] %m\r\n");
		factory.setFilter(new IFilter<LogRecord>() {
			@Override
			public boolean isLoggable(LogRecord record) {
				if (record.getMessage().contains("diedel")) {
					return true;
				}
				return false;
			}
		});
		handler = (IMemoryHandler) factory.createHandler();
		logger = Yalf.get().getLogger("test");
		logger.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.warn("hello");
		logger.warn("diedel");
		logger.warn("hello");
		content = handler.getString();
		String expected = "[WARNING][test                          ] diedel";
		Assert.assertEquals(expected, content.replace("\r", "").replace("\n", ""));
	}

	@Test
	public void logLevelDirect() throws IOException {
		IMemoryHandlerFactory factory;
		IMemoryHandler handler;
		ILogger logger;

		// log to file
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.setPattern("[%p][%c] %m\r\n");
		handler = (IMemoryHandler) factory.createHandler();
		logger = Yalf.get().getLogger("test");
		logger.setLevel(null);
		logger.addHandler(handler);
		//
		logger.setLevel(Level.TRACE);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		logger.setLevel(Level.DEBUG);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		logger.setLevel(Level.INFO);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		logger.setLevel(Level.WARN);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		logger.setLevel(Level.SEVERE);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		String content = handler.getString();
		String expected = "[FINEST ][test                          ] trace"
				+ "[FINE   ][test                          ] debug"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[FINE   ][test                          ] debug"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[SEVERE ][test                          ] severe";
		Assert.assertEquals(expected, content.replace("\r", "").replace("\n", ""));
	}

	@Test
	public void logLevelParent() throws IOException {
		IMemoryHandlerFactory factory;
		IMemoryHandler handler;
		ILogger root;
		ILogger logger;
		String content;

		// log to file
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.setPattern("[%p][%c] %m\r\n");
		handler = (IMemoryHandler) factory.createHandler();
		root = Yalf.get().getLogger("");
		logger = Yalf.get().getLogger("test");
		logger.setLevel(null);
		logger.addHandler(handler);
		//
		root.setLevel(Level.TRACE);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		root.setLevel(Level.DEBUG);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		root.setLevel(Level.INFO);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		root.setLevel(Level.WARN);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		root.setLevel(Level.SEVERE);
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.severe("severe");
		content = handler.getString();
		String expected = "[FINEST ][test                          ] trace"
				+ "[FINE   ][test                          ] debug"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[FINE   ][test                          ] debug"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[INFO   ][test                          ] info"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[WARNING][test                          ] warn"
				+ "[SEVERE ][test                          ] severe"
				+ "[SEVERE ][test                          ] severe";
		Assert.assertEquals(expected, content.replace("\r", "").replace("\n", ""));
	}

	@Test
	public void logPlain() throws IOException {
		IMemoryHandlerFactory factory;
		IMemoryHandler handler;
		ILogger logger;
		String content;

		// log to file
		factory = (IMemoryHandlerFactory) Yalf.get().getFactory(IMemoryHandler.class);
		Assert.assertTrue(factory instanceof JulMemoryHandlerFactory);
		factory.setPattern("[%p][%c] %m\r\n");
		handler = (IMemoryHandler) factory.createHandler();
		logger = Yalf.get().getLogger("test");
		logger.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.warn("hello");
		content = handler.getString();
		String expected = "[WARNING][test                          ] hello";
		Assert.assertEquals(expected, content.replace("\r", "").replace("\n", ""));
	}
}
