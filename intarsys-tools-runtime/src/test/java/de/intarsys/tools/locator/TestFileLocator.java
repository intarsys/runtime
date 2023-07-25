package de.intarsys.tools.locator;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.file.PathTools;

public class TestFileLocator {

	@Test
	public void testDirectoryName() throws Exception {
		File file = new File(File.listRoots()[0], "temp");
		FileLocator locator = new FileLocator(file.getAbsolutePath());
		Assert.assertEquals("temp", locator.getName());
		Assert.assertEquals(PathTools.toCanonicalSeparator(file.getAbsolutePath()), locator.getPath());
		Assert.assertEquals(file.toURI().toString(), locator.toURI().toString());
	}

	@Test
	public void testFileName() throws Exception {
		File file = new File(File.listRoots()[0], "temp/resource.txt");
		FileLocator locator = new FileLocator(file.getAbsolutePath());
		Assert.assertEquals("resource.txt", locator.getName());
		Assert.assertEquals(PathTools.toCanonicalSeparator(file.getAbsolutePath()), locator.getPath());
		Assert.assertEquals(file.toURI().toString(), locator.toURI().toString());
	}

}
