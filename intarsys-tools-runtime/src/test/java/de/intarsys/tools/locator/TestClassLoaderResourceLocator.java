package de.intarsys.tools.locator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestClassLoaderResourceLocator extends TestCase {

	public void testLookup() {
		ClassLoaderResourceLocator locator;
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(),
				"/de/intarsys/tools/locator/resource.txt");
		assertTrue(!locator.exists());
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(), "de.intarsys.tools.locator.resource.txt");
		assertTrue(!locator.exists());
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(), "de/intarsys/tools/locator/resource.txt");
		assertTrue(locator.exists());
	}

	public void testName() {
		ClassLoaderResourceLocator locator;
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(), "de/intarsys/tools/locator/resource.txt");
		Assert.assertTrue(locator.getPath().equals("de/intarsys/tools/locator/resource.txt"));
		Assert.assertTrue(LocatorTools.getBaseName(locator).equals("resource"));
		Assert.assertTrue(LocatorTools.getExtension(locator).equals("txt"));
		Assert.assertTrue(locator.getName().equals("resource.txt"));
	}

	public void testNavigation() {
		ClassLoaderResourceLocator locator;
		ILocator other;
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(), "de/intarsys/tools");
		//
		other = locator.getParent();
		Assert.assertTrue(other.getPath().equals("de/intarsys"));
		other = other.getParent();
		Assert.assertTrue(other.getPath().equals("de"));
		other = other.getParent();
		Assert.assertTrue(other.getPath().equals(""));
		other = other.getParent();
		Assert.assertTrue(other == null);
		//
		other = locator.getChild("locator");
		Assert.assertTrue(other.getPath().equals("de/intarsys/tools/locator"));
		other = other.getChild("resource.txt");
		Assert.assertTrue(other.getPath().equals("de/intarsys/tools/locator/resource.txt"));
	}

	@Test
	public void testXFS() throws IOException {
		ClassLoaderResourceLocator locator;
		ILocator[] children;
		ILocator child;
		//
		locator = new ClassLoaderResourceLocator(getClass().getClassLoader(), "");
		children = locator.listLocators(null);
		Assert.assertTrue(children.length == 1);
		child = children[0];
		Assert.assertTrue(child.getName().equals("xfs-test"));
		children = child.listLocators(null);
		Assert.assertTrue(children.length == 3);
		child = children[0];
		Assert.assertTrue(child.getName().equals("abc"));
		child = children[1];
		Assert.assertTrue(child.getName().equals("foo.txt"));
		child = children[2];
		Assert.assertTrue(child.getName().equals("xyz"));
		//
		child = children[0];
		Assert.assertTrue(child.getName().equals("abc"));
		children = child.listLocators(null);
		Assert.assertTrue(children.length == 3);
		child = children[0];
		Assert.assertTrue(child.getName().equals("123"));
		child = children[1];
		Assert.assertTrue(child.getName().equals("456"));
		child = children[2];
		Assert.assertTrue(child.getName().equals("bar.txt"));
		//
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("123");
		Assert.assertTrue(child.exists());
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("bar.txt");
		Assert.assertTrue(child.exists());
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("gnu.txt");
		Assert.assertTrue(!child.exists());
		//
		child = locator.getChild("xfs-test/abc/123");
		Assert.assertTrue(child.exists());
		child = locator.getChild("xfs-test/abc/bar.txt");
		Assert.assertTrue(child.exists());
		child = locator.getChild("xfs-test/abc/gnu.txt");
		Assert.assertTrue(!child.exists());
	}

}
