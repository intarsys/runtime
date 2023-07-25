package de.intarsys.tools.yalf.jul;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.intarsys.tools.locator.ClassResourceLocator;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Yalf;
import de.intarsys.tools.yalf.common.LogTools;

public class TestLogTools {

	@Test
	@Ignore("Java 17 does no longer give access to private field")
	public void getIOActive() {
		File dir;
		List<File> files;
		//
		Yalf.get().configure(new ClassResourceLocator(getClass(), "logging_file.properties"));
		//
		dir = LogTools.getLogDirectory();
		Assert.assertEquals(new File(System.getProperty("user.home")), dir);
		//
		files = LogTools.getLogFiles();
		Assert.assertTrue(files.size() == 5);
		Assert.assertEquals("test.index_0.log", files.get(0).getName());
		Assert.assertEquals("test.index_1.log", files.get(1).getName());
		Assert.assertEquals("test.index_2.log", files.get(2).getName());
		Assert.assertEquals("test.index_3.log", files.get(3).getName());
		Assert.assertEquals("test.index_4.log", files.get(4).getName());
	}

	@Test
	public void getIOInactive() {
		File dir;
		List<File> files;
		//
		Yalf.get().configure(new ClassResourceLocator(getClass(), "logging_console.properties"));
		//
		dir = LogTools.getLogDirectory();
		Assert.assertTrue(dir == null);
		//
		files = LogTools.getLogFiles();
		Assert.assertTrue(files.isEmpty());
	}

	@Test
	public void getLogger() {
		ILogger logger;
		//
		logger = LogTools.getLogger("test");
		Assert.assertTrue(logger instanceof JulLogger);
		Assert.assertEquals("test", logger.getName());
		//
		logger = LogTools.getLogger(getClass());
		Assert.assertTrue(logger instanceof JulLogger);
		Assert.assertEquals("de.intarsys.tools.yalf.jul", logger.getName());
	}

}
