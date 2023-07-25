package de.intarsys.tools.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestLocatorWalker {

	@Test
	public void testBothFlat() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.setVisitDirectories(true);
		loader.setVisitFiles(true);
		loader.setRecursive(false);
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 3);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore")));
	}

	@Test
	public void testBothRecursive() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setVisitDirectories(true);
		loader.setVisitFiles(true);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 7);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/folder1/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/folder1")));
	}

	@Test
	public void testBothRecursiveFilter() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setVisitDirectories(true);
		loader.setVisitFiles(true);
		loader.setFilter(path -> !(path.contains("Ignore") || path.contains("CVS")));
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 3);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
	}

	@Test
	public void testDirectoriesFlat() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.setVisitDirectories(true);
		loader.setVisitFiles(false);
		loader.setRecursive(false);
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 2);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore")));
	}

	@Test
	public void testDirectoriesRecursive() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.setVisitDirectories(true);
		loader.setVisitFiles(false);
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 3);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/folder1")));
	}

	@Test
	public void testDirectoriesRecursiveFilter() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setVisitDirectories(true);
		loader.setVisitFiles(false);
		loader.setFilter(path -> !(path.contains("Ignore") || path.contains("CVS")));
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 1);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1")));
	}

	@Test
	public void testFilesFlat() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.setRecursive(false);
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 1);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
	}

	@Test
	public void testFilesRecursive() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter((path) -> !path.contains("CVS"));
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 4);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/folder1/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder2Ignore/file1.txt")));
	}

	@Test
	public void testFilesRecursiveFilter() throws IOException {
		LocatorWalker loader;
		FileLocator locator;
		List<ILocator> files;
		//
		files = new ArrayList<>();
		locator = new FileLocator("src/test/data");
		loader = new LocatorWalker(locator);
		loader.setFilter(path -> !(path.contains("Ignore") || path.contains("CVS")));
		loader.forEach(node -> files.add(node.locator));
		Assert.assertTrue(files.size() == 2);
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/file1.txt")));
		Assert.assertTrue(files.contains(new FileLocator("src/test/data/folder1/file1.txt")));
	}

}
