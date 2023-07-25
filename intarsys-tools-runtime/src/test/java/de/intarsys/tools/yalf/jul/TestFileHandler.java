package de.intarsys.tools.yalf.jul;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Yalf;
import de.intarsys.tools.yalf.handler.IFileHandler;
import de.intarsys.tools.yalf.handler.IFileHandlerFactory;

public class TestFileHandler {

	@Test
	@Ignore("Java 17 does no longer give access to private field")
	public void create() throws IOException {
		IFileHandlerFactory factory;
		IFileHandler handler;
		File dir;
		List<File> files;

		// create handler using defaults
		factory = (IFileHandlerFactory) Yalf.get().getFactory(IFileHandler.class);
		Assert.assertTrue(factory instanceof JulFileHandlerFactory);
		factory.setUseProfileDir(false);
		handler = (IFileHandler) factory.createHandler();
		dir = handler.getDirectory();
		Assert.assertEquals(new File(System.getProperty("user.dir")), dir);
		files = handler.getFiles();
		Assert.assertTrue(files.size() == 5);
		Assert.assertEquals("log.0.0.log", files.get(0).getName());
		Assert.assertEquals("log.0.1.log", files.get(1).getName());
		Assert.assertEquals("log.0.2.log", files.get(2).getName());
		Assert.assertEquals("log.0.3.log", files.get(3).getName());
		Assert.assertEquals("log.0.4.log", files.get(4).getName());

		// create handler and check parameters
		factory = (IFileHandlerFactory) Yalf.get().getFactory(IFileHandler.class);
		Assert.assertTrue(factory instanceof JulFileHandlerFactory);
		factory.setAppend(true);
		factory.setCount(5);
		factory.setFilename("%h/test.index_%g.log");
		handler = (IFileHandler) factory.createHandler();
		dir = handler.getDirectory();
		Assert.assertEquals(new File(System.getProperty("user.home")), dir);
		files = handler.getFiles();
		Assert.assertTrue(files.size() == 5);
		Assert.assertEquals("test.index_0.log", files.get(0).getName());
		Assert.assertEquals("test.index_1.log", files.get(1).getName());
		Assert.assertEquals("test.index_2.log", files.get(2).getName());
		Assert.assertEquals("test.index_3.log", files.get(3).getName());
		Assert.assertEquals("test.index_4.log", files.get(4).getName());
	}

	@Test
	@Ignore("Java 17 does no longer give access to private field")
	public void log() throws IOException {
		IFileHandlerFactory factory;
		IFileHandler handler;
		File dir;
		List<File> files;
		File file;
		ILogger logger;
		String content;

		// log to file
		factory = (IFileHandlerFactory) Yalf.get().getFactory(IFileHandler.class);
		factory.setAppend(false);
		factory.setCount(1);
		factory.setFilename("%h/test.log");
		factory.setPattern("[%p][%c] %m\r\n");
		handler = (IFileHandler) factory.createHandler();
		files = handler.getFiles();
		file = files.get(0);
		logger = Yalf.get().getLogger("test");
		logger.addHandler(handler);
		logger.warn("hello");
		content = FileTools.getString(file);
		Assert.assertEquals("[WARNING][test                          ] hello\r\n", content);
	}
}
